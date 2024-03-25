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
public class InventoryItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @NotNull(message = "A quantidade em estoque não pode ser nula")
    @PositiveOrZero(message = "A quantidade do estoque deve ser um número positivo ou zero")
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @NotNull(message = "A quantidade vendida não pode ser nula")
    @PositiveOrZero(message = "A quantidade vendida deve ser um número positivo ou zero")
    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @NotNull(message = "O status do item do inventário não pode ser nulo")
    @Column(name = "stock_status")
    private Integer stockStatus;

    public InventoryItemEntity() {
    }

    public InventoryItemEntity(Long id, ProductEntity product, Integer quantityInStock, StockStatus stockStatus) {
        this.id = id;
        this.product = product;
        this.quantityInStock = quantityInStock;
        setStockStatus(stockStatus);
    }

    public InventoryItemEntity(ProductEntity product, Integer quantityInStock, Integer quantitySold, StockStatus stockStatus) {
        this.product = product;
        this.quantityInStock = quantityInStock;
        this.quantitySold = quantitySold;
        setStockStatus(stockStatus);
    }

    public InventoryItemEntity(Long id, ProductEntity product, Integer quantityInStock, Integer quantitySold, StockStatus stockStatus) {
        this.id = id;
        this.product = product;
        this.quantityInStock = quantityInStock;
        this.quantitySold = quantitySold;
        setStockStatus(stockStatus);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
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
            throw new IllegalArgumentException("O status do item do estoque não pode ser nulo");
        }

        this.stockStatus = stockStatus.getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItemEntity that = (InventoryItemEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Item do inventário: id= " + id + ", product= " + product + ", quantityInStock= " +
                quantityInStock + ", quantitySold= " + quantitySold + ", stockStatus= " + stockStatus + "]";
    }
}