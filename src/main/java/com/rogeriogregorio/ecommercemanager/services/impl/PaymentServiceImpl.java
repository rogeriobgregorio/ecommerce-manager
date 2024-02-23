package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
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

        PaymentEntity paymentEntity = converter.toEntity(paymentRequest, PaymentEntity.class);

        try {
            OrderEntity orderEntity = converter.toEntity(orderRepository.findById(paymentRequest.getOrderId()), OrderEntity.class);

            paymentEntity.setOrderEntity(orderEntity);

            orderEntity.setPaymentEntity(paymentEntity);
            orderRepository.save(orderEntity);

            paymentRepository.save(paymentEntity);
            logger.info("Pagamento criado: {}", paymentEntity.toString());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pagamento: " + exception);

        } catch (NullPointerException exception) {
            logger.error("Erro ao tentar criar o pagamento: {}", exception.getMessage(), exception);
            throw new NotFoundException("Erro ao tentar criar o pagamento: " + exception);
        }

        return converter.toResponse(paymentEntity, PaymentResponse.class);
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

        PaymentEntity paymentEntity = converter.toEntity(paymentRequest, PaymentEntity.class);

        paymentRepository.findById(paymentEntity.getId()).orElseThrow(() -> {
            logger.warn("Pagamento não encontrado com o ID: {}", paymentEntity.getId());
            return new NotFoundException("Pagamento não encontrado com o ID: " + paymentEntity.getId() + ".");
        });

        try {
            OrderEntity orderEntity = converter.toEntity(orderRepository.findById(paymentRequest.getOrderId()), OrderEntity.class);

            paymentEntity.setOrderEntity(orderEntity);

            orderEntity.setPaymentEntity(paymentEntity);
            orderRepository.save(orderEntity);

            paymentRepository.save(paymentEntity);
            logger.info("Pagamento atualizado: {}", paymentEntity.toString());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o pagamento: " + exception);
        }

        return converter.toResponse(paymentEntity, PaymentResponse.class);
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
