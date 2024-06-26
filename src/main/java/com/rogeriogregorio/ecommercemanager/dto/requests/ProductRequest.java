package com.rogeriogregorio.ecommercemanager.dto.requests;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ProductRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private BigDecimal price;
    private String imgUrl;
    private Long discountId;
    private List<Long> categoryIdList;

    public ProductRequest() {
    }

    public ProductRequest(String name,
                          String description, BigDecimal price,
                          String imgUrl, Long discountId,
                          List<Long> categoryIdList) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.discountId = discountId;
        this.categoryIdList = categoryIdList;
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

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public List<Long> getCategoryIdList() {
        return categoryIdList;
    }

    public void setCategoryIdList(List<Long> categoryId) {
        this.categoryIdList = categoryId;
    }
}
