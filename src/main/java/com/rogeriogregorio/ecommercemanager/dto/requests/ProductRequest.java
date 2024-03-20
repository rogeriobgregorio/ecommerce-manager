package com.rogeriogregorio.ecommercemanager.dto.requests;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ProductRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imgUrl;
    private List<Long> categoryIdList;

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, Double price, String imgUrl, List<Long> categoryIdList) {
        this.name = name;
        this.description = description;
        this.price = BigDecimal.valueOf(price);
        this.imgUrl = imgUrl;
        this.categoryIdList = categoryIdList;
    }

    public ProductRequest(Long id, String name, String description, Double price, String imgUrl, List<Long> categoryIdList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = BigDecimal.valueOf(price);
        this.imgUrl = imgUrl;
        this.categoryIdList = categoryIdList;
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

    public List<Long> getCategoryIdList() {
        return categoryIdList;
    }

    public void setCategoryIdList(List<Long> categoryId) {
        this.categoryIdList = categoryId;
    }

    @Override
    public String toString() {
        return "[Produto: id= " + id + ", name= " + name + ", description= " + description
                + ", price= " + price + ", imgUrl= " + imgUrl + ", categoryIdList= " + categoryIdList +"]";
    }
}
