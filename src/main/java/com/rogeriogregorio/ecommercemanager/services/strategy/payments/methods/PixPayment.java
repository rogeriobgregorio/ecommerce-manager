package com.rogeriogregorio.ecommercemanager.services.strategy.payments.methods;

import com.rogeriogregorio.ecommercemanager.dto.PixChargeDto;
import com.rogeriogregorio.ecommercemanager.dto.PixQRCodeDto;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.payment.PixService;
import com.rogeriogregorio.ecommercemanager.services.strategy.payments.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PixPayment implements PaymentStrategy {

    private static final PaymentType PIX_PAYMENT = PaymentType.PIX;

    private final PixService pixService;

    @Autowired
    public PixPayment(PixService pixService) {
        this.pixService = pixService;
    }

    @Override
    public PaymentType getSupportedPaymentMethod() {
        return PaymentType.PIX;
    }

    @Override
    public Payment createPayment(Order order) {

        PixChargeDto pixCharge = pixService.createImmediatePixCharge(order);
        PixQRCodeDto pixQRCode = pixService.generatePixQRCode(pixCharge);

        return Payment.newBuilder()
                .withMoment(Instant.now())
                .withOrder(order)
                .withTxId(pixCharge.getTxid())
                .withPaymentType(PIX_PAYMENT)
                .withChargeLink(pixQRCode.getLinkVisualizacao())
                .build();
    }
}