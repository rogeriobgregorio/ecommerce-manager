package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.ProductReviewPK;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_product_reviews")
public class ProductReview implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ProductReviewPK id = new ProductReviewPK();

    @NotNull
    @Column(name = "rating")
    @Range(min = 1, max = 5, message = "The rating must be between 1 and 5.")
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "moment")
    @NotNull(message = "The product review timestamp cannot be null.")
    private Instant moment;

    public ProductReview() {
    }

    public ProductReview(Product product, User user, Integer rating,
                         String comment, Instant moment) {

        id.setProduct(product);
        id.setUser(user);
        this.rating = rating;
        this.comment = comment;
        this.moment = moment;
    }

    @JsonIgnore
    public Product getProduct() {
        return id.getProduct();
    }

    public void setId(Product product) {
        id.setProduct(product);
    }

    public User getUser() {
        return id.getUser();
    }

    public void setUser(User user) {
        id.setUser(user);
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductReview that = (ProductReview) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[ProductReview: id= " + id
                + ", rating= " + rating
                + ", comment= " + comment
                + ", moment= " + moment + "]";
    }
}
