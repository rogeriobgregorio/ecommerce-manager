package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryItemService inventoryItemService;
    private final UserService userService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, InventoryItemService inventoryItemService, UserService userService, Converter converter) {
        this.orderRepository = orderRepository;
        this.inventoryItemService = inventoryItemService;
        this.userService = userService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAllOrders() {

        try {
            return orderRepository
                    .findAll()
                    .stream()
                    .map(order -> converter.toResponse(order, OrderResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todos os pedidos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todos os pedidos: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public OrderResponse createOrder(OrderRequest orderRequest) {

        orderRequest.setId(null);

        Order order = buildOrder(orderRequest);

        inventoryItemService.isListItemsAvailable(order);

        try {
            orderRepository.save(order);
            logger.info("Pedido criado: {}", order);
            return converter.toResponse(order, OrderResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pedido: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void savePaidOrder(Order order) {

        try {
            orderRepository.save(order);
            logger.info("Pedido pago salvo: {}", order);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar salvar o pedido pago: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar salvar o pedido pago: " + exception);
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

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o pedido: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteOrder(Long id) {

        Order order = findOrderById(id);

        if (isOrderPaid(order)) {
            throw new IllegalStateException("Não é possível excluir um pedido que já foi pago.");
        }

        try {
            orderRepository.deleteById(id);
            logger.warn("Pedido removido: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o pedido: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findOrderByClientId(Long id) {

        return orderRepository
                .findByClient_Id(id)
                .orElseThrow(() -> {
                    logger.warn("Nenhum pedido encontrado com o ID do cliente: {}", id);
                    return new NotFoundException("Nenhum pedido encontrado com o ID do cliente: " + id + ".");
                })
                .stream()
                .map(orderEntity -> converter.toResponse(orderEntity, OrderResponse.class))
                .toList();
    }

    public Order findOrderById(Long id) {

        return orderRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pedido não encontrado com o ID: {}", id);
                    return new NotFoundException("Pedido não encontrado com o ID: " + id + ".");
                });
    }

    public boolean isOrderItemsPresent(Order order) {

        return order.getItems() != null;
    }

    public boolean isDeliveryAddressPresent(Order order) {

        return order.getClient().getAddress() != null;
    }

    public boolean isOrderPaid(Order order) {

        String orderStatus = order.getOrderStatus().name();
        return Set.of("PAID", "SHIPPED", "DELIVERED").contains(orderStatus);
    }

    public void validateOrderStatusChange(OrderRequest orderRequest) {

        OrderStatus requestedStatus = orderRequest.getOrderStatus();
        Order order = findOrderById(orderRequest.getId());
        boolean isOrderPaid = isOrderPaid(order);

        if (isOrderPaid && requestedStatus == OrderStatus.WAITING_PAYMENT) {
            throw new IllegalStateException("Não é possível alterar o status de pagamento: pedido já pago.");
        }

        if (!isOrderPaid && requestedStatus != OrderStatus.CANCELED) {
            throw new IllegalStateException("Não é possível alterar o status de entrega: pedido aguardando pagamento.");
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
