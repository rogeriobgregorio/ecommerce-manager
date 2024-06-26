package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;

import java.io.Serial;
import java.io.Serializable;

public class InventoryItemResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Product product;
    private Integer quantityInStock;
    private Integer quantitySold;
    private Integer stockStatus;

    public InventoryItemResponse() {
    }

    public InventoryItemResponse(Long id, Product product, Integer quantityInStock,
                                 Integer quantitySold, Integer stockStatus) {

        this.id = id;
        this.product = product;
        this.quantityInStock = quantityInStock;
        this.quantitySold = quantitySold;
        this.stockStatus = stockStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public StockStatus getStockStatus() {
        return StockStatus.valueOf(stockStatus);
    }

    public void setStockStatus(StockStatus stockStatus) {

        if (stockStatus == null) {
            throw new IllegalArgumentException("The stock item status cannot be null");
        }

        this.stockStatus = stockStatus.getCode();
    }
}
