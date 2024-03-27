package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
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

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final InventoryItemService inventoryItemService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderService orderService, InventoryItemService inventoryItemService, Converter converter) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.inventoryItemService = inventoryItemService;
        this.converter = converter;
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

            inventoryItemService.saveInventoryItem(payment.getOrderEntity());

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

    public Payment buildPayment(PaymentRequest paymentRequest) {

        Order orderToBePaid = orderService.findOrderById(paymentRequest.getOrderId());

        validatePayment(orderToBePaid);

        Payment payment = new Payment(Instant.now(), orderToBePaid);

        orderToBePaid.setPaymentEntity(payment);
        orderToBePaid.setOrderStatus(OrderStatus.PAID);
        orderService.savePaidOrder(orderToBePaid);

        return payment;
    }

    public void validatePayment(Order order) {

        if (orderService.isOrderPaid(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: pedido já pago.");
        }

        if (!orderService.isOrderItemsPresent(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: não há item no pedido.");
        }

        if (!orderService.isAddressClientPresent(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
        }
    }
}

