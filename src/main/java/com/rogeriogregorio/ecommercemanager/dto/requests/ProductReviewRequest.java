package com.rogeriogregorio.ecommercemanager.dto.requests;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class ProductReviewRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;
    private Long userId;
    private Integer rating;
    private String comment;

    public ProductReviewRequest() {
    }

    public ProductReviewRequest(Long productId, Long userId,
                                Integer rating, String comment) {

        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
