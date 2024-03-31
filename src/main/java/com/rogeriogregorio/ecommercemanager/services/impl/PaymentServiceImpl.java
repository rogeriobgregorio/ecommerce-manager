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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final InventoryItemService inventoryItemService;
    private final StockMovementService stockMovementService;
    private final Converter converter;
    private final List<PaymentValidator> validators;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderService orderService,
                              InventoryItemService inventoryItemService,
                              StockMovementService stockMovementService,
                              Converter converter,
                              List<PaymentValidator> validators) {

        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.inventoryItemService = inventoryItemService;
        this.stockMovementService = stockMovementService;
        this.converter = converter;
        this.validators = validators;
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findAllPayments() {

        try {
            return paymentRepository
                    .findAll()
                    .stream()
                    .map(payment -> converter.toResponse(payment, PaymentResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar pagamentos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar pagamentos: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);

        Payment payment = buildPayment(paymentRequest);

        try {
            paymentRepository.save(payment);
            inventoryItemService.updateInventoryItemQuantity(payment.getOrder());
            stockMovementService.updateStockMovementExit(payment.getOrder());
            logger.info("Pagamento criado: {}", payment);
            return converter.toResponse(payment, PaymentResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o pagamento: " + exception);
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

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o pagamento: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o pagamento: " + exception);
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
        for (PaymentValidator validator : validators) {
            validator.validate(order);
        }
    }

    public Payment buildPayment(PaymentRequest paymentRequest) {

        Order orderToBePaid = orderService.findOrderById(paymentRequest.getOrderId());

        validatePayment(orderToBePaid);

        inventoryItemService.isListItemsAvailable(orderToBePaid);

        Payment payment = new Payment(Instant.now(), orderToBePaid);

        orderToBePaid.setPayment(payment);
        orderToBePaid.setOrderStatus(OrderStatus.PAID);
        orderService.savePaidOrder(orderToBePaid);

        return payment;
    }
}

