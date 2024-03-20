package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
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

import java.util.List;
import java.util.stream.Collectors;

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
                    .collect(Collectors.toList());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todos os itens do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todos os itens do pedido: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        OrderEntity orderEntity = orderService.findOrderEntityById(orderItemRequest.getOrderId());

        if (orderService.isOrderPaid(orderEntity)) {
            throw new IllegalStateException("Não é possível adicionar um item a um pedido que já foi pago.");
        }

        OrderItemEntity orderItemEntity = buildOrderItemFromRequest(orderItemRequest);

        try {
            orderItemRepository.save(orderItemEntity);
            logger.info("Item do pedido criado: {}", orderItemEntity.toString());
            return converter.toResponse(orderItemEntity, OrderItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar item do pedido: " + exception);
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
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        OrderEntity orderEntity = orderService.findOrderEntityById(orderItemRequest.getOrderId());

        if (orderService.isOrderPaid(orderEntity)) {
            throw new IllegalStateException("Não é possível editar um item de um pedido que já foi pago.");
        }

        OrderItemEntity orderItemEntity = buildOrderItemFromRequest(orderItemRequest);

        try {
            orderItemRepository.save(orderItemEntity);
            logger.info("Item do pedido atualizado: {}", orderItemEntity.toString());
            return converter.toResponse(orderItemEntity, OrderItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o item do pedido: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteOrderItem(Long orderId, Long itemId) {

        OrderEntity orderEntity = orderService.findOrderEntityById(orderId);

        if (orderService.isOrderPaid(orderEntity)) {
            throw new IllegalStateException("Não é possível excluir um item de um pedido que já foi pago.");
        }

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        try {
            orderItemRepository.deleteById(id);
            logger.warn("Item do pedido removido: {}", id.getProductEntity().toString());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o item do pedido: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public OrderItemPK buildOrderItemPK(Long orderId, Long itemId) {

        OrderEntity orderEntity = orderService.findOrderEntityById(orderId);
        ProductEntity productEntity = productService.findProductEntityById(itemId);

        OrderItemPK id = new OrderItemPK();
        id.setOrderEntity(orderEntity);
        id.setProductEntity(productEntity);

        return id;
    }

    @Transactional(readOnly = true)
    public OrderItemEntity buildOrderItemFromRequest(OrderItemRequest orderItemRequest) {

        OrderEntity orderEntity = orderService.findOrderEntityById(orderItemRequest.getOrderId());
        ProductEntity productEntity = productService.findProductEntityById(orderItemRequest.getProductId());

        return new OrderItemEntity(orderEntity, productEntity, orderItemRequest.getQuantity(), productEntity.getPrice());
    }
}
