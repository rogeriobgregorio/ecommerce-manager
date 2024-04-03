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
                "Erro ao tentar buscar todos os pedidos: {}")
                .map(order -> converter.toResponse(order, OrderResponse.class));
    }

    @Transactional(readOnly = false)
    public OrderResponse createOrder(OrderRequest orderRequest) {

        orderRequest.setId(null);
        Order order = buildOrder(orderRequest);

        handleError(() -> orderRepository.save(order),
                "Erro ao tentar criar o pedido: ");

        logger.info("Pedido criado: {}", order);
        return converter.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public void savePaidOrder(Order order) {

        handleError(() -> {
            orderRepository.save(order);
            return null;
        }, "Erro ao tentar salvar o pedido pago: ");

        logger.info("Pedido pago salvo: {}", order);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderResponseById(Long id) {

        return handleError(() -> orderRepository.findById(id),
                "Erro ao tentar encontrar o pedido pelo ID: ")
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


        handleError(() -> orderRepository.save(order),
                "Erro ao tentar atualizar o pedido: ");

        logger.info("Pedido atualizado: {}", order);
        return converter.toResponse(order, OrderResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteOrder(Long id) {

        validateOrderDeleteEligibility(id);

        handleError(() -> {
            orderRepository.deleteById(id);
            return null;
        }, "Erro ao tentar excluir o pedido: ");
        logger.warn("Pedido removido: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrderByClientId(Long id, Pageable pageable) {

        return handleError(() -> orderRepository.findByClient_Id(id, pageable),
                "Erro ao tentar buscar pedidos pelo ID do cliente: ")
                .map(order -> converter.toResponse(order, OrderResponse.class));
    }

    public Order findOrderById(Long id) {

        return handleError(() -> orderRepository.findById(id),
                "Erro ao tentar encontrar pedido pelo ID: ")
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
