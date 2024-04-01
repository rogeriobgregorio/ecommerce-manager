package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.*;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InventoryItemService inventoryItemService;
    private final StockMovementService stockMovementService;
    private final OrderService orderService;
    private final Converter converter;
    private final List<PaymentStrategy> validators;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InventoryItemService inventoryItemService,
                              StockMovementService stockMovementService,
                              OrderService orderService,
                              Converter converter,
                              List<PaymentStrategy> validators) {

        this.paymentRepository = paymentRepository;
        this.inventoryItemService = inventoryItemService;
        this.stockMovementService = stockMovementService;
        this.orderService = orderService;
        this.converter = converter;
        this.validators = validators;
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllPayments(Pageable pageable) {

        try {
            Page<Payment> paymentsPage = paymentRepository.findAll(pageable);
            return paymentsPage
                    .map(payment -> converter
                    .toResponse(payment, PaymentResponse.class));

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar buscar pagamentos: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar buscar pagamentos: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);
        Payment payment = buildPayment(paymentRequest);

        try {
            paymentRepository.save(payment);
            updateInventoryStock(payment);
            logger.info("Pagamento criado: {}", payment);
            return converter.toResponse(payment, PaymentResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar criar o pagamento: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar criar o pagamento: " + ex);
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse findPaymentResponseById(Long id) {

        return paymentRepository
                .findById(id)
                .map(payment -> converter.toResponse(payment, PaymentResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Pagamento não encontrado com o ID: {}", id);
                    return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public void deletePayment(Long id) {

        Payment payment = findPaymentById(id);

        try {
            paymentRepository.deleteById(id);
            logger.warn("Pagamento removido: {}", payment);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar excluir o pagamento: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar excluir o pagamento: " + ex);
        }
    }

    public Payment findPaymentById(Long id) {

        return paymentRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Pagamento não encontrado com o ID: {}", id);
                    return new NotFoundException("Pagamento não encontrado com o ID: " + id + ".");
                });
    }

    public void validatePayment(Order order) {
        for (PaymentStrategy validator : validators) {
            validator.validate(order);
        }
    }

    public void updateInventoryStock(Payment payment) {

        Order orderPaid = payment.getOrder();
        inventoryItemService.updateInventoryItemQuantity(orderPaid);
        stockMovementService.updateStockMovementExit(orderPaid);
    }

    public Payment buildPayment(PaymentRequest paymentRequest) {

        Long orderId = paymentRequest.getOrderId();
        Order orderToBePaid = orderService.findOrderById(orderId);

        validatePayment(orderToBePaid);

        inventoryItemService.isListItemsAvailable(orderToBePaid);

        Payment payment = new Payment(Instant.now(), orderToBePaid);

        orderToBePaid.setPayment(payment);
        orderToBePaid.setOrderStatus(OrderStatus.PAID);
        orderService.savePaidOrder(orderToBePaid);

        return payment;
    }
}

