package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.PixWebhookDTO;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import com.rogeriogregorio.ecommercemanager.services.strategy.payments.PaymentStrategy;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InventoryItemService inventoryItemService;
    private final StockMovementService stockMovementService;
    private final List<OrderStrategy> validators;
    private final OrderService orderService;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final List<PaymentStrategy> paymentMethods;
    private final Logger logger = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InventoryItemService inventoryItemService,
                              StockMovementService stockMovementService,
                              List<OrderStrategy> validators, OrderService orderService,
                              ErrorHandler errorHandler, DataMapper dataMapper,
                              List<PaymentStrategy> paymentMethods) {

        this.paymentRepository = paymentRepository;
        this.inventoryItemService = inventoryItemService;
        this.stockMovementService = stockMovementService;
        this.validators = validators;
        this.orderService = orderService;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
        this.paymentMethods = paymentMethods;
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllPayments(Pageable pageable) {

        return errorHandler.catchException(() -> paymentRepository.findAll(pageable),
                        "Error while trying to fetch all payments: ")
                .map(payment -> dataMapper.toResponse(payment, PaymentResponse.class));
    }

    public PaymentResponse createPaymentProcess(PaymentRequest paymentRequest) {

        Order order = orderService.findOrderById(paymentRequest.getOrderId());
        validateOrder(order);

        Payment payment = getPaymentStrategy(paymentRequest).createPayment(order);

        errorHandler.catchException(() -> paymentRepository.save(payment),
                "Error while trying to create paid payment with charge: ");
        logger.info("Payment with charge saved: {}", payment);

        return dataMapper.toResponse(payment, PaymentResponse.class);
    }

    private void validateOrder(Order order) {

        validators.forEach(strategy -> strategy.validateOrder(order));
    }

    private PaymentStrategy getPaymentStrategy(PaymentRequest paymentRequest) {

        PaymentType paymentType = paymentRequest.getPaymentType();

        return paymentMethods
                .stream()
                .filter(strategy -> strategy.getSupportedPaymentMethod().equals(paymentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Payment method not supported: " + paymentType));
    }

    @Transactional(readOnly = false)
    public void savePaidCharges(PixWebhookDTO pixWebhookDTO) {

        List<Payment> paidPayments = buildPaidPayments(pixWebhookDTO);

        for (Payment paidPayment : paidPayments) {
            errorHandler.catchException(() -> paymentRepository.save(paidPayment),
                    "Error while trying to save payment with paid charge: ");
            logger.info("Payment with paid charge saved: {}", paidPayment);

            updateInventoryStock(paidPayment);
        }
    }

    private void updateInventoryStock(Payment payment) {

        Order orderPaid = payment.getOrder();
        inventoryItemService.updateInventoryItemQuantity(orderPaid);
        stockMovementService.updateStockMovementExit(orderPaid);
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

    private Payment findByTxId(String txId) {

        return errorHandler.catchException(() -> paymentRepository.findByTxId(txId),
                        "Error while trying to find the payment by txId: ")
                .orElseThrow(() -> new NotFoundException("Payment not found with txId: " + txId + "."));
    }

    private List<Payment> buildPaidPayments(PixWebhookDTO pixWebhookDTO) {

        List<PixWebhookDTO.Pix> pixList = pixWebhookDTO.getPix();
        List<Payment> paymentList = new ArrayList<>();

        for (PixWebhookDTO.Pix pix : pixList) {
            Payment payment = findByTxId(pix.getTxid());

            Long orderId = payment.getOrder().getId();
            Order orderToBePaid = orderService.findOrderById(orderId);
            orderToBePaid.setPayment(payment);
            orderToBePaid.setOrderStatus(OrderStatus.PAID);
            orderService.savePaidOrder(orderToBePaid);

            payment.toBuilder()
                    .withOrder(orderToBePaid)
                    .withPaymentStatus(PaymentStatus.CONCLUDED)
                    .build();

            paymentList.add(payment);
        }
        return paymentList;
    }
}

