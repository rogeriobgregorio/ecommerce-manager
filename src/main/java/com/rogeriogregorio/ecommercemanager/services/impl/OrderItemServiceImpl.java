package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(OrderItemServiceImpl.class);

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderService orderService, ProductService productService, Converter converter) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.productService = productService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> findAllOrderItems() {

        try {
            return orderItemRepository
                    .findAll()
                    .stream()
                    .map(orderItemEntity -> converter.toResponse(orderItemEntity, OrderItemResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todos os itens do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todos os itens do pedido: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public OrderItemResponse findOrderItemById(Long orderId, Long itemId) {

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        return orderItemRepository
                .findById(id)
                .map(orderItemEntity -> converter.toResponse(orderItemEntity, OrderItemResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Itens não encontrado com o ID: {}", id);
                    return new NotFoundException("Itens não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        Order order = orderService.findOrderById(orderItemRequest.getOrderId());

        if (orderService.isOrderPaid(order)) {
            throw new IllegalStateException("Não é possível adicionar um item a um pedido que já foi pago.");
        }

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        try {
            orderItemRepository.save(orderItem);
            logger.info("Item do pedido criado: {}", orderItem);
            return converter.toResponse(orderItem, OrderItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar item do pedido: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        Order order = orderService.findOrderById(orderItemRequest.getOrderId());

        if (orderService.isOrderPaid(order)) {
            throw new IllegalStateException("Não é possível atualizar um item de um pedido que já foi pago.");
        }

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        try {
            orderItemRepository.save(orderItem);
            logger.info("Item do pedido atualizado: {}", orderItem);
            return converter.toResponse(orderItem, OrderItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o item do pedido: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteOrderItem(Long orderId, Long itemId) {

        Order order = orderService.findOrderById(orderId);

        if (orderService.isOrderPaid(order)) {
            throw new IllegalStateException("Não é possível excluir um item de um pedido que já foi pago.");
        }

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        try {
            orderItemRepository.deleteById(id);
            logger.warn("Item do pedido removido: {}", id.getProductEntity());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o item do pedido: " + exception);
        }
    }

    public OrderItemPK buildOrderItemPK(Long orderId, Long itemId) {

        Order order = orderService.findOrderById(orderId);
        Product product = productService.findProductById(itemId);

        OrderItemPK id = new OrderItemPK();
        id.setOrderEntity(order);
        id.setProductEntity(product);

        return id;
    }

    public OrderItem buildOrderItem(OrderItemRequest orderItemRequest) {

        Order order = orderService.findOrderById(orderItemRequest.getOrderId());
        Product product = productService.findProductById(orderItemRequest.getProductId());
        int quantity = orderItemRequest.getQuantity();
        BigDecimal price = product.getPrice();

        return new OrderItem(order, product, quantity, price);
    }
}
