package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.PixWebhookDto;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import com.rogeriogregorio.ecommercemanager.services.strategy.payments.PaymentStrategy;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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
    private final MailService mailService;
    private final OrderService orderService;
    private final List<OrderStrategy> orderValidators;
    private final List<PaymentStrategy> paymentMethods;
    private final CatchError catchError;
    private final DataMapper dataMapper;

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InventoryItemService inventoryItemService,
                              StockMovementService stockMovementService,
                              MailService mailService,
                              OrderService orderService,
                              List<OrderStrategy> orderValidators,
                              List<PaymentStrategy> paymentMethods,
                              CatchError catchError,
                              DataMapper dataMapper) {

        this.paymentRepository = paymentRepository;
        this.inventoryItemService = inventoryItemService;
        this.stockMovementService = stockMovementService;
        this.mailService = mailService;
        this.orderService = orderService;
        this.orderValidators = orderValidators;
        this.paymentMethods = paymentMethods;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllPayments(Pageable pageable) {

        return catchError.run(() -> paymentRepository.findAll(pageable))
                .map(payment -> dataMapper.map(payment, PaymentResponse.class));
    }

    @Transactional
    public PaymentResponse createPaymentProcess(PaymentRequest paymentRequest) {

        Order order = orderService.getOrderIfExists(paymentRequest.getOrderId());
        orderValidators.forEach(strategy -> strategy.validateOrder(order));

        Payment payment = getPaymentStrategy(paymentRequest).createPayment(order);

        Payment savedPayment = catchError.run(() -> paymentRepository.save(payment));
        LOGGER.info("Payment with charge saved: {}", savedPayment);
        return dataMapper.map(savedPayment, PaymentResponse.class);
    }

    private PaymentStrategy getPaymentStrategy(PaymentRequest paymentRequest) {

        PaymentType paymentType = paymentRequest.getPaymentType();

        return paymentMethods.stream()
                .filter(strategy -> strategy.getSupportedPaymentMethod().equals(paymentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Payment method not supported: " + paymentType));
    }

    @Transactional
    public void savePaidPixCharges(PixWebhookDto pixWebhook) {

        List<Payment> paidPixChargeList = buildPaidPixCharges(pixWebhook);

        for (Payment paymentPix : paidPixChargeList) {
            Payment savedPaymentPix = catchError.run(() -> paymentRepository.save(paymentPix));
            LOGGER.info("Payment pix with paid charge saved: {}", savedPaymentPix);

            updateInventoryStock(savedPaymentPix);
            //CompletableFuture.runAsync(() -> mailService.sendPaymentReceiptEmail(savedPaymentPix));// TODO reativar método
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse findPaymentById(Long id) {

        return catchError.run(() -> paymentRepository.findById(id))
                .map(payment -> dataMapper.map(payment, PaymentResponse.class))
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + id + "."));
    }

    @Transactional
    public void deletePayment(Long id) {

        Payment payment = getPaymentIfExists(id);

        catchError.run(() -> paymentRepository.delete(payment));
        LOGGER.warn("Payment deleted: {}", payment);
    }

    @Transactional(readOnly = true)
    private Payment findByTxId(String txId) {

        return catchError.run(() -> paymentRepository.findByTxId(txId))
                .orElseThrow(() -> new NotFoundException("Payment not found with txId: " + txId + "."));
    }

    public Payment getPaymentIfExists(Long id) {

        return catchError.run(() -> paymentRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("Payment not found with ID: " + id + "."));
    }

    private void updateInventoryStock(Payment payment) {

        Order orderPaid = payment.getOrder();
        inventoryItemService.updateInventoryItemQuantity(orderPaid);
        stockMovementService.updateStockMovementExit(orderPaid);
    }

    private List<Payment> buildPaidPixCharges(PixWebhookDto pixWebhook) {

        List<PixWebhookDto.Pix> pixList = pixWebhook.getPix();
        List<Payment> paidPixChargeList = new ArrayList<>();

        for (PixWebhookDto.Pix pix : pixList) {
            Payment paymentPix = findByTxId(pix.getTxid());

            Long orderId = paymentPix.getOrder().getId();
            Order order = orderService.getOrderIfExists(orderId);
            order.setPayment(paymentPix);
            order.setOrderStatus(OrderStatus.PAID);
            orderService.savePaidOrder(order);

            paymentPix.toBuilder()
                    .withOrder(order)
                    .withPaymentStatus(PaymentStatus.CONCLUDED)
                    .build();

            paidPixChargeList.add(paymentPix);
        }
        return paidPixChargeList;
    }
}

