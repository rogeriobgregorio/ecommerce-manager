package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_products", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "unique_name_constraint")
})
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank(message = "The name must not be blank.")
    @Size(max = 250, message = "The name must have a maximum of 250 characters.")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @NotBlank(message = "The description must not be blank.")
    private String description;

    @Column(name = "price")
    @NotNull(message = "The price cannot be null.")
    @DecimalMin(value = "0.01", message = "The price must be greater than zero.")
    private BigDecimal price;

    @Column(name = "img_url")
    @URL(message = "The image URL must be valid.")
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "product_discount_id")
    private ProductDiscount productDiscount;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> items = new HashSet<>();

    @OneToMany(mappedBy = "id.product", fetch = FetchType.EAGER)
    private Set<ProductReview> reviews = new HashSet<>();

    public Product() {
    }

    public Product(String name, String description,
                   BigDecimal price, String imgUrl) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    public Product(Long id, String name, String description, BigDecimal price,
                   String imgUrl, ProductDiscount productDiscount) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
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

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public Set<ProductReview> getReviews() {
        return reviews;
    }

    @JsonIgnore
    public Set<Order> getOrders() {

        Set<Order> orders = new HashSet<>();
        for (OrderItem orderItem : items) {
            orders.add(orderItem.getOrder());
        }
        return orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product that = (Product) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Product: id= " + id
                + ", name= " + name
                + ", description= " + description
                + ", price= " + price
                + ", imgUrl= " + imgUrl
                + "categories= " + categories + "]";
    }
}
