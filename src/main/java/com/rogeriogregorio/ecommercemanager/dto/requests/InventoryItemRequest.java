package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;

import java.io.Serial;
import java.io.Serializable;

public class InventoryItemRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private Integer quantityInStock;
    private Integer stockStatus;

    public InventoryItemRequest() {
    }

    public InventoryItemRequest(Long productId, Integer quantityInStock,
                                StockStatus stockStatus) {

        this.productId = productId;
        this.quantityInStock = quantityInStock;
        setStockStatus(stockStatus);
    }

    public InventoryItemRequest(Long id, Long productId, Integer quantityInStock,
                                StockStatus stockStatus) {

        this.id = id;
        this.productId = productId;
        this.quantityInStock = quantityInStock;
        setStockStatus(stockStatus);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
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
