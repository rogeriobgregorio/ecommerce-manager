package com.rogeriogregorio.ecommercemanager.services.strategy.payments.methods;

import com.rogeriogregorio.ecommercemanager.dto.PixChargeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixQRCodeDTO;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.payment.PixService;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.strategy.payments.PaymentStrategy;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class PixPayment implements PaymentStrategy {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PixService pixService;
    private final List<OrderStrategy> validators;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(PixPayment.class);

    @Autowired
    public PixPayment(PaymentRepository paymentRepository,
                      OrderService orderService,
                      PixService pixService,
                      List<OrderStrategy> validators,
                      ErrorHandler errorHandler,
                      DataMapper dataMapper) {

        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.pixService = pixService;
        this.validators = validators;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {

        paymentRequest.setId(null);
        Payment payment = buildPaymentPix(paymentRequest);

        errorHandler.catchException(() -> paymentRepository.save(payment),
                "Error while trying to create paid payment with charge: ");
        logger.info("Payment with charge saved: {}", payment);

        return dataMapper.toResponse(payment, PaymentResponse.class);
    }

    @Override
    public PaymentType getSupportedPaymentMethod() {

        return PaymentType.PIX;
    }

    private void validateOrderToBePaid(Order order) {

        validators.forEach(validator -> validator.validateOrder(order));
    }

    private Payment buildPaymentPix(PaymentRequest paymentRequest) {

        Long orderId = paymentRequest.getOrderId();
        Order orderToBePaid = orderService.findOrderById(orderId);
        validateOrderToBePaid(orderToBePaid);

        PixChargeDTO pixCharge = pixService.createImmediatePixCharge(orderToBePaid);
        PixQRCodeDTO pixQRCode = pixService.generatePixQRCode(pixCharge);

        return Payment.newBuilder()
                .withMoment(Instant.now())
                .withOrder(orderToBePaid)
                .withTxId(pixCharge.getTxid())
                .withPaymentType(paymentRequest.getPaymentType())
                .withChargeLink(pixQRCode.getLinkVisualizacao())
                .build();
    }
}
