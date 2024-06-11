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
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserService userService,
                            DiscountCouponService discountCouponService,
                            List<OrderStatusStrategy> statusValidators,
                            CatchError catchError, DataMapper dataMapper) {

        this.orderRepository = orderRepository;
        this.userService = userService;
        this.discountCouponService = discountCouponService;
        this.statusValidators = statusValidators;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAllOrders(Pageable pageable) {

        return catchError.run(() -> orderRepository.findAll(pageable)
                .map(order -> dataMapper.map(order, OrderResponse.class)));
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {

        User client = userService.getUserIfExists(orderRequest.getClientId());

        Order order = Order.newBuilder()
                .withMoment(Instant.now())
                .withOrderStatus(OrderStatus.WAITING_PAYMENT)
                .withClient(client)
                .build();

        Order savedOrder = catchError.run(() -> orderRepository.save(order));
        LOGGER.info("Order created: {}", savedOrder);
        return dataMapper.map(savedOrder, OrderResponse.class);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderById(Long id) {

        return catchError.run(() -> orderRepository.findById(id)
                .map(order -> dataMapper.map(order, OrderResponse.class))
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id + ".")));
    }

    @Transactional
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {

        Order order = getOrderIfExists(id);

        String code = orderRequest.getDiscountCouponCode();
        DiscountCoupon discountCoupon = validateDiscountCoupon(code);

        if (orderRequest.getOrderStatus() == OrderStatus.CANCELED) {
            order.setOrderStatus(orderRequest.getOrderStatus());
        }

        order.toBuilder()
                .withMoment(Instant.now())
                .withCoupon(discountCoupon)
                .build();

        Order uodatedOrder = catchError.run(() -> orderRepository.save(order));
        LOGGER.info("Order updated: {}", uodatedOrder);
        return dataMapper.map(uodatedOrder, OrderResponse.class);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderRequest orderRequest) {

        Order order = getOrderIfExists(id).toBuilder()
                .withMoment(Instant.now())
                .withOrderStatus(orderRequest.getOrderStatus())
                .build();

        statusValidators.forEach(strategy -> strategy.validateStatusChange(orderRequest, order));

        Order uodatedOrder = catchError.run(() -> orderRepository.save(order));
        LOGGER.info("Order status updated: {}", uodatedOrder);
        return dataMapper.map(uodatedOrder, OrderResponse.class);
    }

    @Transactional
    public void deleteOrder(Long id) {

        Order order = getOrderIfExists(id);

        if (order.isOrderPaid()) {
            throw new IllegalStateException("Paid order cannot be deleted.");
        }

        catchError.run(() -> orderRepository.delete(order));
        LOGGER.warn("Order deleted: {}", order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrderByClientId(Long id, Pageable pageable) {

        return catchError.run(() -> orderRepository.findByClient_Id(id, pageable)
                .map(order -> dataMapper.map(order, OrderResponse.class)));
    }

    public Order getOrderIfExists(Long id) {

        return catchError.run(() -> orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id + ".")));
    }

    @Transactional
    public void savePaidOrder(Order order) {

        catchError.run(() -> orderRepository.save(order));
        LOGGER.info("Paid order saved: {}", order);
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
