package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.PixChargeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixQRCodeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixWebHook;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.pix.PixService;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.*;
import com.rogeriogregorio.ecommercemanager.services.strategy.PaymentStrategy;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InventoryItemService inventoryItemService;
    private final StockMovementService stockMovementService;
    private final OrderService orderService;
    private final PixService pixService;
    private final List<PaymentStrategy> validators;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InventoryItemService inventoryItemService,
                              StockMovementService stockMovementService,
                              OrderService orderService, PixService pixService,
                              List<PaymentStrategy> validators,
                              ErrorHandler errorHandler, DataMapper dataMapper) {

        this.paymentRepository = paymentRepository;
        this.inventoryItemService = inventoryItemService;
        this.stockMovementService = stockMovementService;
        this.orderService = orderService;
        this.pixService = pixService;
        this.validators = validators;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllPayments(Pageable pageable) {

        return errorHandler.catchException(() -> paymentRepository.findAll(pageable),
                        "Error while trying to fetch all payments: ")
                .map(payment -> dataMapper.toResponse(payment, PaymentResponse.class));
    }

    @Transactional(readOnly = false)
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);
        Payment payment = buildPaymentWithCharge(paymentRequest);

        errorHandler.catchException(() -> paymentRepository.save(payment),
                "Error while trying to create paid payment with charge: ");
        logger.info("Payment with charge saved: {}", payment);

        return dataMapper.toResponse(payment, PaymentResponse.class);
    }

    @Transactional(readOnly = false)
    public void savePaidPaymentsFromWebHook(JSONObject webhook) {

        List<Payment> paidPayments = buildPaidPayments(webhook);

        for (Payment paidPayment : paidPayments) {
            errorHandler.catchException(() -> paymentRepository.save(paidPayment),
                    "Error while trying to save payment with paid charge: ");
            logger.info("Payment with paid charge saved: {}", paidPayment);

            updateInventoryStock(paidPayment);
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse findPaymentById(Long id) {

        return errorHandler.catchException(() -> paymentRepository.findById(id),
                        "Error while trying to find the payment by ID: ")
                .map(payment -> dataMapper.toResponse(payment, PaymentResponse.class))
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public void deletePayment(Long id) {

        isPaymentExists(id);

        errorHandler.catchException(() -> {
            paymentRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the payment: ");
        logger.warn("Payment removed: {}", id);
    }

    private void isPaymentExists(Long id) {

        boolean isPaymentExists = errorHandler.catchException(() -> paymentRepository.existsById(id),
                "Error while trying to check the presence of the payment:");

        if (!isPaymentExists) {
            throw new NotFoundException("Payment not found with ID: " + id + ".");
        }
    }

    private void validateOrderToBePaid(Order order) {

        validators.forEach(validator -> validator.validateOrder(order));

    }

    private void updateInventoryStock(Payment payment) {

        Order orderPaid = payment.getOrder();
        inventoryItemService.updateInventoryItemQuantity(orderPaid);
        stockMovementService.updateStockMovementExit(orderPaid);
    }

    private Payment findByTxId(String txId) {

        return errorHandler.catchException(() -> paymentRepository.findByTxId(txId),
                        "Error while trying to find the payment by txId: ")
                .orElseThrow(() -> new NotFoundException("Payment not found with txId: " + txId + "."));
    }

    private Payment buildPaymentWithCharge(PaymentRequest paymentRequest) {

        Long orderId = paymentRequest.getOrderId();
        Order orderToBePaid = orderService.findOrderById(orderId);
        validateOrderToBePaid(orderToBePaid);

        Payment payment = new Payment(Instant.now(), orderToBePaid);

        PixChargeDTO pixCharge = pixService.createImmediatePixCharge(payment.getOrder());
        String txId = pixCharge.getTxid();
        payment.setTxId(txId);

        PixQRCodeDTO pixQRCode = pixService.generatePixQRCode(pixCharge);
        String pixQRCodeLink = pixQRCode.getLinkVisualizacao();
        payment.setPixQRCodeLink(pixQRCodeLink);

        return payment;
    }

    private List<Payment> buildPaidPayments(JSONObject webhook) {

        PixWebHook pixWebHook = dataMapper.fromJson(webhook, PixWebHook.class);
        List<PixWebHook.Pix> pixArray = pixWebHook.getPix();
        List<Payment> payments = new ArrayList<>();

        for (PixWebHook.Pix pix : pixArray) {
            String txId = pix.getTxid();
            Payment payment = findByTxId(txId);

            Long orderId = payment.getOrder().getId();
            Order orderToBePaid = orderService.findOrderById(orderId);
            orderToBePaid.setPayment(payment);
            orderToBePaid.setOrderStatus(OrderStatus.PAID);

            orderService.savePaidOrder(orderToBePaid);
            payment.setOrder(orderToBePaid);

            payments.add(payment);
        }

        return payments;
    }
}

