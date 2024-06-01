package com.rogeriogregorio.ecommercemanager.entities;

import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_inventory_items")
public class InventoryItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull(message = "The quantity in stock cannot be null.")
    @PositiveOrZero(message = "The stock quantity must be a positive number or zero.")
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @NotNull(message = "The quantity sold cannot be null.")
    @PositiveOrZero(message = "The quantity sold must be a positive number or zero.")
    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @NotNull(message = "The inventory item status cannot be null.")
    @Column(name = "stock_status")
    private Integer stockStatus;

    public InventoryItem() {
    }

    private InventoryItem(Builder builder) {
        setId(builder.id);
        setProduct(builder.product);
        setQuantityInStock(builder.quantityInStock);
        setQuantitySold(builder.quantitySold);
        stockStatus = builder.stockStatus;
    }

    public static Builder newBuilder() {
        return new Builder();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Inventory Item: id= " + id
                + ", product= " + product
                + ", quantityInStock= " + quantityInStock
                + ", quantitySold= " + quantitySold
                + ", stockStatus= " + stockStatus + "]";
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withProduct(this.product)
                .withQuantityInStock(this.quantityInStock)
                .withStockStatus(StockStatus.valueOf(this.stockStatus))
                .withQuantitySold(this.quantitySold);
    }

    public static final class Builder {

        private Long id;
        private Product product;
        private Integer quantityInStock;
        private  Integer quantitySold;
        private Integer stockStatus;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public Builder withQuantityInStock(Integer quantityInStock) {
            this.quantityInStock = quantityInStock;
            return this;
        }

        public Builder withQuantitySold(Integer quantitySold) {
            this.quantitySold = quantitySold;
            return this;
        }

        public Builder withStockStatus(StockStatus stockStatus) {
            this.stockStatus = stockStatus.getCode();
            return this;
        }

        public InventoryItem build() {
            return new InventoryItem(this);
        }
    }
}