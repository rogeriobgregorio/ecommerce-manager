package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderItemService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
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
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(OrderItemServiceImpl.class);

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, Converter converter) {
        this.orderItemRepository = orderItemRepository;
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

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar itens do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar itens do pedido.", exception);
        }
    }

    @Transactional(readOnly = false)
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        orderItemRequest.setId(null);

        OrderItemEntity orderItemEntity = converter.toEntity(orderItemRequest, OrderItemEntity.class);

        try {
            orderItemRepository.save(orderItemEntity);
            logger.info("Item do pedido criado: {}", orderItemEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar item do pedido.", exception);
        }

        return converter.toResponse(orderItemEntity, OrderItemResponse.class);
    }

    @Transactional(readOnly = true)
    public OrderItemResponse findOrderItemById(Long id) {

        return orderItemRepository
                .findById(id)
                .map(orderItemEntity -> converter.toResponse(orderItemEntity, OrderItemResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Itens do pedido não encontrados com o ID: {}", id);
                    return new NotFoundException("Itens do pedido não encontrados com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        OrderItemEntity orderItemEntity = converter.toEntity(orderItemRequest, OrderItemEntity.class);

        orderItemRepository.findById(orderItemEntity.getOrderEntity().getId()).orElseThrow(() -> {
            logger.warn("Itens do pedido não encontrados com o ID: {}", orderItemEntity.getOrderEntity().getId());
            return new NotFoundException("Itens do pedido não encontrados com o ID: " + orderItemEntity.getOrderEntity().getId() + ".");
        });

        try {
            orderItemRepository.save(orderItemEntity);
            logger.info("Item do pedido atualizado: {}", orderItemEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar os item do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar os item do pedido.", exception);
        }

        return converter.toResponse(orderItemEntity, OrderItemResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteOrderItem(Long id) {

        orderItemRepository.findById(id).orElseThrow(() -> {
            logger.warn("Itens do pedido não encontrados com o ID: {}", id);
            return new NotFoundException("Itens do pedido não encontrados com o ID: " + id + ".");
        });

        try {
            orderItemRepository.deleteById(id);
            logger.warn("Itens do pedido removidos: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir os itens do pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir os itens do pedido.", exception);
        }
    }
}
