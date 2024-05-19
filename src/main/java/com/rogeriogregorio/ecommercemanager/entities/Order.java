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

    public Order(Long id, Instant moment, OrderStatus orderStatus,
                 User client, DiscountCoupon coupon) {

        this.id = id;
        this.moment = moment;
        setOrderStatus(orderStatus);
        this.client = client;
        this.coupon = coupon;
    }

    public Order(Long id, Instant moment,
                 User client, DiscountCoupon coupon) {

        this.id = id;
        this.moment = moment;
        this.client = client;
        this.coupon = coupon;
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
    public List<Map.Entry<Product, Integer>> getProductQuantities() {

        return items.stream()
                .collect(Collectors.groupingBy(OrderItem::getProduct,
                        Collectors.summingInt(OrderItem::getQuantity)))
                .entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
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
}
