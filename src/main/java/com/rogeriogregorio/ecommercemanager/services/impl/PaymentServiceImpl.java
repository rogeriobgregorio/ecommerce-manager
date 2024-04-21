package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.*;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentServiceImpl extends ErrorHandlerTemplateImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InventoryItemService inventoryItemService;
    private final StockMovementService stockMovementService;
    private final OrderService orderService;
    private final Converter converter;
    private final List<PaymentStrategy> validators;

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

        return handleError(() -> paymentRepository.findAll(pageable),
                "Error while trying to fetch all payments: ")
                .map(payment -> converter.toResponse(payment, PaymentResponse.class));
    }

    @Transactional(readOnly = false)
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);
        Payment payment = buildPayment(paymentRequest);

        handleError(() -> paymentRepository.save(payment),
                "Error while trying to create the payment: ");
        logger.info("Payment created: {}", payment);

        updateInventoryStock(payment);
        return converter.toResponse(payment, PaymentResponse.class);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findPaymentResponseById(Long id) {

        return handleError(() -> paymentRepository.findById(id),
                "Error while trying to find the payment by ID: ")
                .map(payment -> converter.toResponse(payment, PaymentResponse.class))
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public void deletePayment(Long id) {

        Payment payment = findPaymentById(id);

        handleError(() -> {
            paymentRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the payment: ");
        logger.warn("Payment removed: {}", payment);
    }

    public Payment findPaymentById(Long id) {

        return handleError(() -> paymentRepository.findById(id),
                "Error while trying to find the payment by ID:")
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + id + "."));
    }

    public void updateInventoryStock(Payment payment) {

        Order orderPaid = payment.getOrder();
        inventoryItemService.updateInventoryItemQuantity(orderPaid);
        stockMovementService.updateStockMovementExit(orderPaid);
    }

    public void validateOrder(Order order) {
        for (PaymentStrategy validator : validators) {
            validator.validate(order);
        }
    }

    public Payment buildPayment(PaymentRequest paymentRequest) {

        Long orderId = paymentRequest.getOrderId();

        Order orderToBePaid = orderService.findOrderById(orderId);

        validateOrder(orderToBePaid);

        Payment payment = new Payment(Instant.now(), orderToBePaid);

        orderToBePaid.setPayment(payment);
        orderToBePaid.setOrderStatus(OrderStatus.PAID);
        orderService.savePaidOrder(orderToBePaid);

        return payment;
    }
}

