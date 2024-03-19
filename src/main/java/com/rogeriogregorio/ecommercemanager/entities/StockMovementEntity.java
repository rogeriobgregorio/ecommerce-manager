package com.rogeriogregorio.ecommercemanager.entities;

import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_stock_movements")
public class StockMovementEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id")
    private InventoryItemEntity inventoryItem;

    @NotNull(message = "O tipo de movimentação não pode ser nulo")
    @Column(name = "movement_type")
    private Integer movementType;

    @NotNull(message = "A quantidade de itens movimentados não pode ser nula")
    @Column(name = "quantity_moved")
    private Integer quantityMoved;

    public StockMovementEntity() {
    }

    public StockMovementEntity(InventoryItemEntity inventoryItem, MovementType movementType, Integer quantityMoved) {
        this.inventoryItem = inventoryItem;
        setMovementType(movementType);
        this.quantityMoved = quantityMoved;
    }

    public StockMovementEntity(Long id, InventoryItemEntity inventoryItem, MovementType movementType, Integer quantityMoved) {
        this.id = id;
        this.inventoryItem = inventoryItem;
        setMovementType(movementType);
        this.quantityMoved = quantityMoved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventoryItemEntity getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItemEntity inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public MovementType getMovementType() {
        return MovementType.valueOf(movementType);
    }

    public void setMovementType(MovementType movementType) {

        if (movementType == null) {
            throw new IllegalArgumentException("O status de movimentação do item do estoque não pode ser nulo");
        }

        this.movementType = movementType.getCode();
    }

    public Integer getQuantityMoved() {
        return quantityMoved;
    }

    public void setQuantityMoved(Integer quantityMoved) {
        this.quantityMoved = quantityMoved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockMovementEntity that = (StockMovementEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Movimentação do estoque: id= " + id + ", inventoryItem= " + inventoryItem +
                ", movementType= " + movementType + ", quantityMoved= " + quantityMoved +"]";
    }
}
