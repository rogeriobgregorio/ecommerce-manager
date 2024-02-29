package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository, Converter converter) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAllPayments() {

        try {
            return paymentRepository
                    .findAll()
                    .stream()
                    .map(paymentEntity -> converter.toResponse(paymentEntity, PaymentResponse.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar pagamentos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar pagamentos: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);
        paymentRequest.setMoment(Instant.now());

        OrderEntity orderEntity = orderRepository.findById(paymentRequest.getOrderId()).orElseThrow(() -> {
                    logger.warn("Pedido não encontrado com o ID: {}", paymentRequest.getOrderId());
                    return new NotFoundException("Pedido não encontrado com o ID: " + paymentRequest.getOrderId() + ".");
                });

        PaymentEntity paymentEntity = converter.toEntity(paymentRequest, PaymentEntity.class);

        paymentEntity.setOrderEntity(orderEntity);
        orderEntity.setPaymentEntity(paymentEntity);
        orderEntity.setOrderStatus(OrderStatus.PAID);

        try {
            orderRepository.save(orderEntity);
            paymentRepository.save(paymentEntity);
            logger.info("Pagamento criado: {}", paymentEntity.toString());

            return converter.toResponse(paymentEntity, PaymentResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pagamento: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse findPaymentById(Long id) {

        return paymentRepository
                .findById(id)
                .map(paymentEntity -> converter.toResponse(paymentEntity, PaymentResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Pagamento não encontrado com o ID: {}", id);
                    return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public PaymentResponse updatePayment(PaymentRequest paymentRequest) {

        paymentRepository.findById(paymentRequest.getId()).orElseThrow(() -> {
            logger.warn("Pagamento não encontrado com o ID: {}", paymentRequest.getId());
            return new NotFoundException("Pagamento não encontrado com o ID: " + paymentRequest.getId() + ".");
        });

        OrderEntity orderEntity = orderRepository.findById(paymentRequest.getOrderId()).orElseThrow(() -> {
                    logger.warn("Pedido não encontrado com o ID: {}", paymentRequest.getOrderId());
                    return new NotFoundException("Pedido não encontrado com o ID: " + paymentRequest.getOrderId() + ".");
                });

        PaymentEntity paymentEntity = converter.toEntity(paymentRequest, PaymentEntity.class);

        paymentEntity.setOrderEntity(orderEntity);
        orderEntity.setPaymentEntity(paymentEntity);

        try {
            orderRepository.save(orderEntity);
            paymentRepository.save(paymentEntity);
            logger.info("Pagamento criado: {}", paymentEntity.toString());

            return converter.toResponse(paymentEntity, PaymentResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pagamento: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deletePayment(Long id) {

        paymentRepository.findById(id).orElseThrow(() -> {
            logger.warn("Pagamento não encontrado com o ID: {}", id);
            return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
        });

        try {
            paymentRepository.deleteById(id);
            logger.warn("Pagamento removido: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o pagamento: " + exception);
        }
    }
}
