package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class ReceiptPaymentDTO {

    private Long paymentId;
    private Instant paymentMoment;
    private String transactionId;
    private String paymentType;
    private String paymentStatus;
    private String chargeLink;

    private Long orderId;
    private Instant orderMoment;
    private String orderStatus;
    private String clientName;
    private String clienteEmail;
    private List<String> items;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal totalAmountPaid;

    public ReceiptPaymentDTO(Payment payment) {
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
                ).collect(Collectors.toList());
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

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("E-COMMERCE MANAGER\n");
        stringBuilder.append("Recibo de Pagamento\n");
        stringBuilder.append("====================\n");
        stringBuilder.append("Id do pagamento: ").append(paymentId).append("\n");
        stringBuilder.append("Data: ").append(paymentMoment).append("\n");
        stringBuilder.append("Id da transação: ").append(transactionId).append("\n");
        stringBuilder.append("Tipo de pagamento: ").append(paymentType).append("\n");
        stringBuilder.append("Status do pagamento: ").append(paymentStatus).append("\n");
        stringBuilder.append("Link da cobrança: ").append(chargeLink).append("\n");
        stringBuilder.append("\nDetalhes do Pedido\n");
        stringBuilder.append("====================\n");
        stringBuilder.append("Id do pedido: ").append(orderId).append("\n");
        stringBuilder.append("Data do pedido: ").append(orderMoment).append("\n");
        stringBuilder.append("Status do pedido: ").append(orderStatus).append("\n");
        stringBuilder.append("Cliente: ").append(clientName).append("\n");
        stringBuilder.append("\nItens:\n");
        for (String item : items) {
            stringBuilder.append(" - ").append(item).append("\n");
        }
        stringBuilder.append("\nSubtotal: ").append(subtotal).append("\n");
        stringBuilder.append("Desconto: ").append(discount).append("%\n");
        stringBuilder.append("Valor total pago: ").append(totalAmountPaid).append("\n");
        stringBuilder.append("====================\n");
        stringBuilder.append("Muito obrigado pela sua compra!\n");

        return stringBuilder.toString();
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
