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
import com.rogeriogregorio.ecommercemanager.services.strategy.OrderStrategy;
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
    private final List<OrderStrategy> validators;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserService userService,
                            DiscountCouponService discountCouponService,
                            List<OrderStrategy> validators,
                            ErrorHandler errorHandler, DataMapper dataMapper) {

        this.orderRepository = orderRepository;
        this.userService = userService;
        this.discountCouponService = discountCouponService;
        this.validators = validators;
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

        orderRequest.setId(null);
        Order order = buildCreateOrder(orderRequest);

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
    public OrderResponse updateOrder(OrderRequest orderRequest) {

        Order order = buildUpdateOrder(orderRequest);

        errorHandler.catchException(() -> orderRepository.save(order),
                "Error while trying to update the order: ");
        logger.info("Order updated: {}", order);

        return dataMapper.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public OrderResponse updateOrderStatus(OrderRequest orderRequest) {

        Order order = buildUpdateOrderStatus(orderRequest);

        errorHandler.catchException(() -> orderRepository.save(order),
                "Error while trying to update the order: ");
        logger.info("Order updated: {}", order);

        return dataMapper.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteOrder(Long id) {

        Order order = findOrderById(id);
        validateOrderDeleteEligibility(order);

        errorHandler.catchException(() -> {
            orderRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the order: ");
        logger.warn("Order removed: {}", order);
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

    private void validateOrderStatusChange(OrderRequest orderRequest, Order order) {

        validators.forEach(validator -> validator.validateStatusChange(orderRequest, order));
    }

    private void validateOrderDeleteEligibility(Order order) {

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

    private Order buildCreateOrder(OrderRequest orderRequest) {

        Instant instant = Instant.now();
        OrderStatus orderStatus = OrderStatus.WAITING_PAYMENT;
        User client = userService.findUserById(orderRequest.getClientId());

        return new Order(instant, orderStatus, client);
    }

    private Order buildUpdateOrder(OrderRequest orderRequest) {

        Order order = findOrderById(orderRequest.getId());
        order.setMoment(Instant.now());

        if (orderRequest.getOrderStatus() == OrderStatus.CANCELED) {
            order.setOrderStatus(orderRequest.getOrderStatus());
        }

        String code = orderRequest.getDiscountCouponCode();
        DiscountCoupon discountCoupon = validateDiscountCoupon(code);

        order.setCoupon(discountCoupon);

        return order;
    }

    private Order buildUpdateOrderStatus(OrderRequest orderRequest) {

        Order order = findOrderById(orderRequest.getId());

        validateOrderStatusChange(orderRequest, order);

        order.setMoment(Instant.now());
        order.setOrderStatus(orderRequest.getOrderStatus());

        return order;
    }
}
