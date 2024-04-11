package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
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
import java.util.Set;

@Service
public class OrderServiceImpl extends ErrorHandlerTemplateImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final Converter converter;
    private final List<OrderStatusStrategy> validators;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserService userService,
                            Converter converter,
                            List<OrderStatusStrategy> validators) {

        this.orderRepository = orderRepository;
        this.userService = userService;
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

        orderRequest.setId(null);
        Order order = buildOrder(orderRequest);

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

        validateOrderStatusChange(orderRequest);
        Order order = buildOrder(orderRequest);

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

    public void validateOrderStatusChange(OrderRequest orderRequest) {

        Long orderId = orderRequest.getId();
        Order order = findOrderById(orderId);

        for (OrderStatusStrategy validator : validators) {
            validator.validate(order, orderRequest);
        }
    }

    public void validateOrderDeleteEligibility(Long id) {

        Order order = findOrderById(id);
        boolean isOrderPaid = order.isOrderPaid();

        if (isOrderPaid) {
            throw new IllegalStateException("You cannot delete an order that has already been paid for.");
        }
    }

    public Order buildOrder(OrderRequest orderRequest) {

        Long orderId = orderRequest.getId();
        Instant instant = Instant.now();
        OrderStatus orderStatus = (orderId == null) ? OrderStatus.WAITING_PAYMENT : orderRequest.getOrderStatus();
        User client = userService.findUserById(orderRequest.getClientId());

        return new Order(orderId, instant, orderStatus, client);
    }
}
