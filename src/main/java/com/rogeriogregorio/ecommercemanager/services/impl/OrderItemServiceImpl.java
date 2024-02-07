package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderItemService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final Converter<OrderItemRequest, OrderItemEntity, OrderItemResponse> orderItemConverter;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, Converter<OrderItemRequest, OrderItemEntity, OrderItemResponse> orderItemConverter) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemConverter = orderItemConverter;
    }

    @Transactional(readOnly = true)
    public List<OrderItemResponse> findAllOrderItem() {

        try {
            return orderItemRepository
                    .findAll()
                    .stream()
                    .map(orderItemConverter::entityToResponse)
                    .collect(Collectors.toList());

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar categorias: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar categorias.", exception);
        }
    }

    @Transactional(readOnly = false)
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        orderItemRequest.setId(null);

        OrderItemEntity orderItemEntity = orderItemConverter.requestToEntity(orderItemRequest);

        try {
            orderItemRepository.save(orderItemEntity);
            logger.info("Usuário criado: {}", orderItemEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar o categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o categoria.", exception);
        }

        return orderItemConverter.entityToResponse(orderItemEntity);
    }

    @Transactional(readOnly = true)
    public OrderItemResponse findOrderItemById(Long id) {

        return orderItemRepository
                .findById(id)
                .map(orderItemConverter::entityToResponse)
                .orElseThrow(() -> {
                    logger.warn("Categoria não encontrado com o ID: {}", id);
                    return new NotFoundException("Categoria não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        OrderItemEntity orderItemEntity = orderItemConverter.requestToEntity(orderItemRequest);

        orderItemRepository.findById(orderItemEntity.getOrderEntity().getId()).orElseThrow(() -> {
            logger.warn("Usuário não encontrado com o ID: {}", orderItemEntity.getOrderEntity().getId());
            return new NotFoundException("Usuário não encontrado com o ID: " + orderItemEntity.getOrderEntity().getId() + ".");
        });

        try {
            orderItemRepository.save(orderItemEntity);
            logger.info("Categoria atualizada: {}", orderItemEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar a categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar a categoria.", exception);
        }

        return orderItemConverter.entityToResponse(orderItemEntity);
    }

    @Transactional(readOnly = false)
    public void deleteOrderItem(Long id) {

        orderItemRepository.findById(id).orElseThrow(() -> {
            logger.warn("Categoria não encontrada com o ID: {}", id);
            return new NotFoundException("Categoria não encontrada com o ID: " + id + ".");
        });

        try {
            orderItemRepository.deleteById(id);
            logger.warn("Categoria removida: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir a categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir a categoria.", exception);
        }
    }
}
