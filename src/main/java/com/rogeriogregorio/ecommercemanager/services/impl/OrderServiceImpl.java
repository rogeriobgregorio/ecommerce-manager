package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.DiscountCouponService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceImpl extends ErrorHandlerTemplateImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final DiscountCouponService discountCouponService;
    private final Converter converter;
    private final List<OrderStatusStrategy> validators;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserService userService,
                            DiscountCouponService discountCouponService,
                            Converter converter,
                            List<OrderStatusStrategy> validators) {

        this.orderRepository = orderRepository;
        this.userService = userService;
        this.discountCouponService = discountCouponService;
        this.converter = converter;
        this.validators = validators;
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findAllOrders(Pageable pageable) {

        return handleError(() -> orderRepository.findAll(pageable),
                "Error while trying to fetch all orders: ")
                .map(order -> converter.toResponse(order, OrderResponse.class));
    }

    @Transactional(readOnly = false)
    public OrderResponse createOrder(OrderRequest orderRequest) {

        Order order = buildCreateOrder(orderRequest);

        handleError(() -> orderRepository.save(order),
                "Error while trying to create the order: ");
        logger.info("Order created: {}", order);

        return converter.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public void savePaidOrder(Order order) {

        handleError(() -> {
            orderRepository.save(order);
            return null;
        }, "Error while trying to save the paid order: ");
        logger.info("Paid order saved: {}", order);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderResponseById(Long id) {

        return handleError(() -> orderRepository.findById(id),
                "Error while trying to find the order by ID: ")
                .map(order -> converter.toResponse(order, OrderResponse.class))
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public OrderResponse updateOrder(OrderRequest orderRequest) {

        Order order = buildUpdateOrder(orderRequest);

        handleError(() -> orderRepository.save(order),
                "Error while trying to update the order: ");
        logger.info("Order updated: {}", order);

        return converter.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public OrderResponse updateOrderStatus(OrderRequest orderRequest) {

        Order order = buildUpdateOrderStatus(orderRequest);

        handleError(() -> orderRepository.save(order),
                "Error while trying to update the order: ");
        logger.info("Order updated: {}", order);

        return converter.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteOrder(Long id) {

        validateOrderDeleteEligibility(id);

        handleError(() -> {
            orderRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the order: ");
        logger.warn("Order removed: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrderByClientId(Long id, Pageable pageable) {

        return handleError(() -> orderRepository.findByClient_Id(id, pageable),
                "Error while trying to fetch orders by customer ID: ")
                .map(order -> converter.toResponse(order, OrderResponse.class));
    }

    public Order findOrderById(Long id) {

        return handleError(() -> orderRepository.findById(id),
                "Error while trying to find order by ID: ")
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id + "."));
    }

    public void validateOrderStatusChange(OrderRequest orderRequest, Order order) {

        for (OrderStatusStrategy validator : validators) {
            validator.validate(orderRequest, order);
        }
    }

    public void validateOrderDeleteEligibility(Long id) {

        Order order = findOrderById(id);
        boolean isOrderPaid = order.isOrderPaid();

        if (isOrderPaid) {
            throw new IllegalStateException("You cannot delete an order that has already been paid for.");
        }
    }

    public void validateDiscountCoupon(DiscountCoupon discountCoupon) {

        if (discountCoupon == null) return;

        boolean isDiscountCouponValid = discountCoupon.isValid();

        if (!isDiscountCouponValid) {
            throw new IllegalStateException("The discount coupon is not within its validity period.");
        }
    }

    public Order buildUpdateOrderStatus(OrderRequest orderRequest) {

        Long orderId = orderRequest.getId();
        Order order = findOrderById(orderId);
        Instant instant = Instant.now();
        OrderStatus orderStatus = orderRequest.getOrderStatus();
        User user = order.getClient();
        DiscountCoupon discountCoupon = order.getDiscountCoupon();

        validateOrderStatusChange(orderRequest, order);

        return new Order(orderId, instant, orderStatus, user, discountCoupon);
    }

    public Order buildCreateOrder(OrderRequest orderRequest) {

        orderRequest.setId(null);
        Instant instant = Instant.now();
        OrderStatus orderStatus = OrderStatus.WAITING_PAYMENT;
        User client = userService.findUserById(orderRequest.getClientId());

        return new Order(instant, orderStatus, client);
    }

    public Order buildUpdateOrder(OrderRequest orderRequest) {

        Long orderId = orderRequest.getId();
        Instant instant = Instant.now();

        boolean orderStatusRequest = orderRequest.getOrderStatus() == OrderStatus.CANCELED;
        OrderStatus orderStatus = orderStatusRequest ? OrderStatus.CANCELED : OrderStatus.WAITING_PAYMENT;

        User user = userService.findUserById(orderRequest.getClientId());
        String code = orderRequest.getDiscountCouponCode();
        DiscountCoupon discountCoupon = discountCouponService.findDiscountCouponByCode(code);

        validateDiscountCoupon(discountCoupon);

        return new Order(orderId, instant, orderStatus, user, discountCoupon);
    }
}
