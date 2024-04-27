package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class ProductResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imgUrl;
    private ProductDiscount productDiscount;
    private Set<Category> categories = new HashSet<>();
    private Set<OrderItem> items = new HashSet<>();

    public ProductResponse() {
    }

    public ProductResponse(Long id, String name, String description, Double price,
                           String imgUrl, ProductDiscount productDiscount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = BigDecimal.valueOf(price);
        this.imgUrl = imgUrl;
        this.productDiscount = productDiscount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = BigDecimal.valueOf(price);
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public ProductDiscount getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(ProductDiscount productDiscount) {
        this.productDiscount = productDiscount;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public boolean isDiscountPresent() {
        return productDiscount != null;
    }

    public BigDecimal getPriceWithDiscount() {

        BigDecimal productPrice = getPrice();

        if (isDiscountPresent() && productDiscount.isValid()) {
            BigDecimal discount = productDiscount.getDiscount();
            BigDecimal discountPercentage = discount.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            BigDecimal discountValue = productPrice.multiply(discountPercentage);
            productPrice = productPrice.subtract(discountValue);
        }

        return productPrice.setScale(2, RoundingMode.HALF_UP);
    }

    @JsonIgnore
    public Set<Order> getOrders() {

        Set<Order> orders = new HashSet<>();
        for (OrderItem orderItem : items) {
            orders.add(orderItem.getOrder());
        }
        return orders;
    }

    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }
}
