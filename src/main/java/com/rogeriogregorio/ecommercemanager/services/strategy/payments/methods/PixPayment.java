package com.rogeriogregorio.ecommercemanager.services.strategy.payments.methods;

import com.rogeriogregorio.ecommercemanager.dto.PixChargeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixQRCodeDTO;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.payment.PixService;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.strategy.payments.PaymentStrategy;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PixPayment implements PaymentStrategy {

    private static final PaymentType PIX = PaymentType.PIX;

    private final PixService pixService;

    @Autowired
    public PixPayment(PixService pixService) {

        this.pixService = pixService;
    }

    @Override
    public Payment createPayment(Order order) {

        PixChargeDTO pixCharge = pixService.createImmediatePixCharge(order);
        PixQRCodeDTO pixQRCode = pixService.generatePixQRCode(pixCharge);

        return Payment.newBuilder()
                .withMoment(Instant.now())
                .withOrder(order)
                .withTxId(pixCharge.getTxid())
                .withPaymentType(PIX)
                .withChargeLink(pixQRCode.getLinkVisualizacao())
                .build();
    }

    @Override
    public PaymentType getSupportedPaymentMethod() {
        return PaymentType.PIX;
    }
}