package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.*;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStatusStrategy;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final DiscountCouponService discountCouponService;
    private final List<OrderStatusStrategy> statusValidators;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserService userService,
                            DiscountCouponService discountCouponService,
                            List<OrderStatusStrategy> statusValidators,
                            ErrorHandler errorHandler, DataMapper dataMapper) {

        this.orderRepository = orderRepository;
        this.userService = userService;
        this.discountCouponService = discountCouponService;
        this.statusValidators = statusValidators;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAllOrders(Pageable pageable) {

        return errorHandler.catchException(() -> orderRepository.findAll(pageable),
                        "Error while trying to fetch all orders: ")
                .map(order -> dataMapper.toResponse(order, OrderResponse.class));
    }

    @Transactional(readOnly = false)
    public OrderResponse createOrder(OrderRequest orderRequest) {

        User client = userService.findUserById(orderRequest.getClientId());

        Order order = Order.newBuilder()
                .withMoment(Instant.now())
                .withOrderStatus(OrderStatus.WAITING_PAYMENT)
                .withClient(client)
                .build();

        errorHandler.catchException(() -> orderRepository.save(order),
                "Error while trying to create the order: ");
        logger.info("Order created: {}", order);

        return dataMapper.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public void savePaidOrder(Order order) {

        errorHandler.catchException(() -> {
            orderRepository.save(order);
            return null;
        }, "Error while trying to save the paid order: ");
        logger.info("Paid order saved: {}", order);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderResponseById(Long id) {

        return errorHandler.catchException(() -> orderRepository.findById(id),
                        "Error while trying to find the order by ID: ")
                .map(order -> dataMapper.toResponse(order, OrderResponse.class))
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {

        Order order = findOrderById(id);

        String code = orderRequest.getDiscountCouponCode();
        DiscountCoupon discountCoupon = validateDiscountCoupon(code);

        if (orderRequest.getOrderStatus() == OrderStatus.CANCELED) {
            order.setOrderStatus(orderRequest.getOrderStatus());
        }

        order.toBuilder()
                .withMoment(Instant.now())
                .withCoupon(discountCoupon)
                .build();

        errorHandler.catchException(() -> orderRepository.save(order),
                "Error while trying to update the order: ");
        logger.info("Order updated: {}", order);

        return dataMapper.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public OrderResponse updateOrderStatus(Long id, OrderRequest orderRequest) {

        verifyOrderExists(id);
        Order order = findOrderById(id).toBuilder()
                .withMoment(Instant.now())
                .withOrderStatus(orderRequest.getOrderStatus())
                .build();

        statusValidators.forEach(strategy -> strategy.validateStatusChange(orderRequest, order));

        errorHandler.catchException(() -> orderRepository.save(order),
                "Error while trying to update the order: ");
        logger.info("Order updated: {}", order);

        return dataMapper.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteOrder(Long id) {

        verifyOrderExists(id);
        validateOrderDeleteEligibility(id);

        errorHandler.catchException(() -> {
            orderRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the order: ");
        logger.warn("Order removed with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrderByClientId(Long id, Pageable pageable) {

        return errorHandler.catchException(() -> orderRepository.findByClient_Id(id, pageable),
                        "Error while trying to fetch orders by customer ID: ")
                .map(order -> dataMapper.toResponse(order, OrderResponse.class));
    }

    public Order findOrderById(Long id) {

        return errorHandler.catchException(() -> orderRepository.findById(id),
                        "Error while trying to find order by ID: ")
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id + "."));
    }

    private void verifyOrderExists(Long id) {

        boolean isOrderExists = errorHandler.catchException(() -> orderRepository.existsById(id),
                "Error while trying to check the presence of the order: ");

        if (!isOrderExists) {
            throw new NotFoundException("Order not found with ID: " + id + ".");
        }
    }

    private void validateOrderDeleteEligibility(Long id) {

        Order order = findOrderById(id);
        boolean isOrderPaid = order.isOrderPaid();

        if (isOrderPaid) {
            throw new IllegalStateException("You cannot delete an order that has already been paid for.");
        }
    }

    private DiscountCoupon validateDiscountCoupon(String code) {

        if (code == null) return null;

        DiscountCoupon discountCoupon = discountCouponService.findDiscountCouponByCode(code);
        boolean isDiscountCouponValid = discountCoupon.isValid();

        if (!isDiscountCouponValid) {
            throw new IllegalStateException("The discount coupon is not within its validity period.");
        }

        return discountCoupon;
    }
}
