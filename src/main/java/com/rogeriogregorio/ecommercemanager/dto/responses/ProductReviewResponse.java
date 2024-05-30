package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.ProductReviewPK;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class ProductReviewResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ProductReviewPK id = new ProductReviewPK();
    private Integer rating;
    private String comment;
    private Instant moment;

    public ProductReviewResponse(Product product, User user, Integer rating,
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
}
