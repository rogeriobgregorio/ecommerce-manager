package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ProductRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;
    private Long categoryId;

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, Double price, String imgUrl, Long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.categoryId = categoryId;
    }

    public ProductRequest(Long id, String name, String description, Double price, String imgUrl, Long categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.categoryId = categoryId;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategories(Long categoryId) {
        this.categoryId = categoryId;
    }
}
