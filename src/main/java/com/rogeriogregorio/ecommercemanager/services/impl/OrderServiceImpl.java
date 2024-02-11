package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final Converter<OrderRequest, OrderEntity, OrderResponse> orderConverter;
    private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, Converter<OrderRequest, OrderEntity, OrderResponse> orderConverter) {
        this.orderRepository = orderRepository;
        this.orderConverter = orderConverter;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAllOrders() {

        try {
            return orderRepository
                    .findAll()
                    .stream()
                    .map(orderConverter::entityToResponse)
                    .collect(Collectors.toList());

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar pedidos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar pedidos.", exception);
        }
    }

    @Transactional(readOnly = false)
    public OrderResponse createOrder(OrderRequest orderRequest) {

        orderRequest.setId(null);

        OrderEntity orderEntity = orderConverter.requestToEntity(orderRequest);

        try {
            orderRepository.save(orderEntity);
            logger.info("Pedido criado: {}", orderEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar o pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pedido.", exception);
        }

        return orderConverter.entityToResponse(orderEntity);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderById(Long id) {

        return orderRepository
                .findById(id)
                .map(orderConverter::entityToResponse)
                .orElseThrow(() -> {
                    logger.warn("Pedido não encontrado com o ID: {}", id);
                    return new NotFoundException("Pedido não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public OrderResponse updateOrder(OrderRequest orderRequest) {

        OrderEntity orderEntity = orderConverter.requestToEntity(orderRequest);

        orderRepository.findById(orderEntity.getId()).orElseThrow(() -> {
            logger.warn("Pedido não encontrado com o ID: {}", orderEntity.getId());
            return new NotFoundException("Pedido não encontrado com o ID: " + orderEntity.getId() + ".");
        });

        try {
            orderRepository.save(orderEntity);
            logger.info("Pedido atualizado: {}", orderEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar o pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o pedido.", exception);
        }

        return orderConverter.entityToResponse(orderEntity);
    }

    @Transactional(readOnly = true)
    public void deleteOrder(Long id) {

        orderRepository.findById(id).orElseThrow(() -> {
            logger.warn("Usuário não encontrado com o ID: {}", id);
            return new NotFoundException("Usuário não encontrado com o ID: " + id + ".");
        });

        try {
            orderRepository.deleteById(id);
            logger.warn("Pedido removido: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir o pedido: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o pedido.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findOrderByClientId(Long id) {

        try {
            return orderRepository
                    .findByClient_Id(id)
                    .stream()
                    .map(orderConverter::entityToResponse)
                    .collect(Collectors.toList());

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar pedidos pelo id do cliente: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar pedidos pelo id do cliente.", exception);
        }
    }
}
