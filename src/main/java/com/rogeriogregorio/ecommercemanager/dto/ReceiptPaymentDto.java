package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ReceiptPaymentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long paymentId;
    private final Instant paymentMoment;
    private final String transactionId;
    private final String paymentType;
    private final String paymentStatus;
    private final String chargeLink;

    private final Long orderId;
    private final Instant orderMoment;
    private final String orderStatus;
    private final String clientName;
    private final String clienteEmail;
    private final List<String> items;
    private final BigDecimal subtotal;
    private final BigDecimal discount;
    private final BigDecimal totalAmountPaid;

    public ReceiptPaymentDto(Payment payment) {
        this.paymentId = payment.getId();
        this.paymentMoment = payment.getMoment();
        this.transactionId = payment.getTxId();
        this.paymentType = payment.getPaymentType().name();
        this.paymentStatus = payment.getPaymentStatus().name();
        this.chargeLink = payment.getChargeLink();

        Order order = payment.getOrder();
        this.orderId = order.getId();
        this.orderMoment = order.getMoment();
        this.orderStatus = order.getOrderStatus().name();
        this.clientName = order.getClient().getName();
        this.clienteEmail = order.getClient().getEmail();
        this.items = order.getItems().stream()
                .map(item -> item
                        .getProduct()
                        .getName()
                        + " (Quantity: " + item.getQuantity()
                        + ", Subtotal: " + item.getSubTotal() + ")"
                ).toList();
        this.subtotal = order.getSubTotal();
        if (order.isDiscountCouponPresent() && order.getCoupon().isValid()) {
            this.discount = order.getCoupon().getDiscount();
        } else {
            this.discount = BigDecimal.ZERO;
        }
        this.totalAmountPaid = order.getTotalFinal();
    }

    @Override
    public String toString() {

        final String separationLine = "====================\n";

        StringBuilder receipt = new StringBuilder();
        receipt.append("E-COMMERCE MANAGER\n");
        receipt.append("Recibo de Pagamento\n");
        receipt.append(separationLine);
        receipt.append("Id do pagamento: ").append(paymentId).append("\n");
        receipt.append("Data: ").append(paymentMoment).append("\n");
        receipt.append("Id da transação: ").append(transactionId).append("\n");
        receipt.append("Tipo de pagamento: ").append(paymentType).append("\n");
        receipt.append("Status do pagamento: ").append(paymentStatus).append("\n");
        receipt.append("Link da cobrança: ").append(chargeLink).append("\n");
        receipt.append("\nDetalhes do Pedido\n");
        receipt.append(separationLine);
        receipt.append("Id do pedido: ").append(orderId).append("\n");
        receipt.append("Data do pedido: ").append(orderMoment).append("\n");
        receipt.append("Status do pedido: ").append(orderStatus).append("\n");
        receipt.append("Cliente: ").append(clientName).append("\n");
        receipt.append("\nItens:\n");
        for (String item : items) {
            receipt.append(" - ").append(item).append("\n");
        }
        receipt.append("\nSubtotal: ").append(subtotal).append("\n");
        receipt.append("Desconto: ").append(discount).append("%\n");
        receipt.append("Valor total pago: ").append(totalAmountPaid).append("\n");
        receipt.append(separationLine);
        receipt.append("Muito obrigado pela sua compra!\n");

        return receipt.toString();
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Instant getPaymentMoment() {
        return paymentMoment;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getChargeLink() {
        return chargeLink;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Instant getOrderMoment() {
        return orderMoment;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClienteEmail() { return clienteEmail; }

    public List<String> getItems() {
        return items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }
}
