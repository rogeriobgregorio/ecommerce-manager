package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "tb_orders")
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "moment")
    @NotNull(message = "The order timestamp cannot be null.")
    private Instant moment;

    @Column(name = "order_status")
    @NotNull(message = "The order status cannot be null.")
    private Integer orderStatus;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @NotNull(message = "The client cannot be null.")
    private User client;

    @OneToMany(mappedBy = "id.order")
    private Set<OrderItem> items = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "discount_coupon_id")
    private DiscountCoupon coupon;

    @OneToOne(mappedBy = "order", cascade = CascadeType.REMOVE)
    private Payment payment;

    public Order() {
    }

    public Order(Instant moment, OrderStatus
            orderStatus, User client) {

        this.moment = moment;
        setOrderStatus(orderStatus);
        this.client = client;
    }

    public Order(Long id, Instant moment, Integer orderStatus,
                 User client, DiscountCoupon coupon, Payment payment) {

        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
        this.coupon = coupon;
        this.payment = payment;
    }

    private Order(Builder builder) {
        setId(builder.id);
        setMoment(builder.moment);
        orderStatus = builder.orderStatus;
        setClient(builder.client);
        items = builder.items;
        setCoupon(builder.coupon);
        setPayment(builder.payment);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public OrderStatus getOrderStatus() {
        return OrderStatus.valueOf(orderStatus);
    }

    public void setOrderStatus(OrderStatus orderStatus) {

        if (orderStatus == null) {
            throw new IllegalArgumentException("The order status cannot be null.");
        }

        this.orderStatus = orderStatus.getCode();
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public DiscountCoupon getCoupon() {
        return coupon;
    }

    public void setCoupon(DiscountCoupon coupon) {
        this.coupon = coupon;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getSubTotal() {

        return items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalFinal() {

        BigDecimal total = getSubTotal();

        if (isDiscountCouponPresent() && coupon.isValid()) {
            BigDecimal discount = coupon.getDiscount();
            BigDecimal discountPercentage = discount.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            BigDecimal discountValue = total.multiply(discountPercentage);
            total = total.subtract(discountValue);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    @JsonIgnore
    public List<String> getProductQuantities() {

        return items.stream()
                .collect(Collectors.groupingBy(
                        orderItem -> orderItem.getProduct().getName(),
                        Collectors.summingInt(OrderItem::getQuantity)))
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": unidades = " + entry.getValue())
                .collect(Collectors.toList());
    }

    public boolean isOrderPaid() {

        String currentStatus = getOrderStatus().name();
        return Set.of("PAID", "SHIPPED", "DELIVERED").contains(currentStatus);
    }

    public boolean isDiscountCouponPresent() {
        return coupon != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order that = (Order) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Order: id= " + id
                + ", moment= " + moment
                + ", orderStatus= " + orderStatus
                + ", client= " + client
                + ", discount coupon= " + coupon + "]";
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withMoment(this.moment)
                .withOrderStatus(OrderStatus.valueOf(this.orderStatus))
                .withClient(this.client)
                .withCoupon(this.coupon)
                .withPayment(this.payment);
    }

    public static final class Builder {
        private Long id;
        private Instant moment;
        private Integer orderStatus;
        private User client;
        private Set<OrderItem> items;
        private DiscountCoupon coupon;
        private Payment payment;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withMoment(Instant moment) {
            this.moment = moment;
            return this;
        }

        public Builder withOrderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus.getCode();
            return this;
        }

        public Builder withClient(User client) {
            this.client = client;
            return this;
        }

        public Builder withItems(Set<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder withCoupon(DiscountCoupon coupon) {
            this.coupon = coupon;
            return this;
        }

        public Builder withPayment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
