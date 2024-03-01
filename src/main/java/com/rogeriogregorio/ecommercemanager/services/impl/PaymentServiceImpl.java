package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
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
    private final OrderService orderService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderService orderService, Converter converter) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
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

        if (isOrderPaid(paymentRequest)) {
            logger.info("Pagamento já realizado: {}", paymentRequest.toString());
            return converter.toResponse(paymentRequest, PaymentResponse.class);
        }

        OrderRequest orderRequest = new OrderRequest(paymentRequest.getOrderId(), OrderStatus.PAID);
        OrderResponse orderResponse = orderService.updateOrder(orderRequest);
        OrderEntity orderEntity = converter.toEntity(orderResponse, OrderEntity.class);

        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderEntity);

        try {
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
    public void deletePayment(Long id) {

        findPaymentById(id);

        try {
            paymentRepository.deleteById(id);
            logger.warn("Pagamento removido: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o pagamento: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public Boolean isOrderPaid(PaymentRequest paymentRequest) {

        try {
            OrderResponse order = orderService.findOrderById(paymentRequest.getOrderId());
            return order.getOrderStatus() == OrderStatus.PAID;

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar verificar o status do pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar verificar o status do pagamento: " + exception);
        }
    }
}

