package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final Converter converter;
    private final List<OrderStatusStrategy> validators;
    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

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
    public Page<OrderResponse> findAllOrders(int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> ordersPage = orderRepository.findAll(pageable);
            return ordersPage
                    .map(order -> converter
                    .toResponse(order, OrderResponse.class));

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar buscar todos os pedidos: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar buscar todos os pedidos: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public OrderResponse createOrder(OrderRequest orderRequest) {

        orderRequest.setId(null);
        Order order = buildOrder(orderRequest);

        try {
            orderRepository.save(order);
            logger.info("Pedido criado: {}", order);
            return converter.toResponse(order, OrderResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar criar o pedido: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar criar o pedido: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public void savePaidOrder(Order order) {

        try {
            orderRepository.save(order);
            logger.info("Pedido pago salvo: {}", order);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar salvar o pedido pago: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar salvar o pedido pago: " + ex);
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderResponseById(Long id) {

        return orderRepository
                .findById(id)
                .map(order -> converter.toResponse(order, OrderResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Pedido não encontrado com o ID: {}", id);
                    return new NotFoundException("Pedido não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public OrderResponse updateOrder(OrderRequest orderRequest) {

        validateOrderStatusChange(orderRequest);
        Order order = buildOrder(orderRequest);

        try {
            orderRepository.save(order);
            logger.info("Pedido atualizado: {}", order);
            return converter.toResponse(order, OrderResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar atualizar o pedido: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar atualizar o pedido: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public void deleteOrder(Long id) {

        validateOrderDeleteEligibility(id);

        try {
            orderRepository.deleteById(id);
            logger.warn("Pedido removido: {}", id);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar excluir o pedido: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar excluir o pedido: " + ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrderByClientId(Long id, int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> ordersPage = orderRepository.findByClient_Id(id, pageable);
            return ordersPage
                    .map(order -> converter
                    .toResponse(order, OrderResponse.class));

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar buscar pedidos pelo ID do cliente: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar buscar pedidos pelo ID do cliente: " + ex);
        }
    }

    public Order findOrderById(Long id) {

        return orderRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido não encontrado com o ID: {}", id);
                    return new NotFoundException("Pedido não encontrado com o ID: " + id + ".");
                });
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
        String orderStatus = order.getOrderStatus().name();
        boolean isOrderPaid = Set.of("PAID", "SHIPPED", "DELIVERED").contains(orderStatus);

        if (isOrderPaid) {
            throw new IllegalStateException("Não é possível excluir um pedido que já foi pago.");
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
