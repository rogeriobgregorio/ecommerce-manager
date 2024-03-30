package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_stock_movements")
public class StockMovement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "moment")
    @NotNull(message = "O momento do pedido não pode ser nulo")
    private Instant moment;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;

    @NotNull(message = "O tipo de movimentação não pode ser nulo")
    @Column(name = "movement_type")
    private Integer movementType;

    @NotNull(message = "A quantidade de itens movimentados não pode ser nula")
    @Column(name = "quantity_moved")
    private Integer quantityMoved;

    public StockMovement() {
    }

    public StockMovement(Instant moment, InventoryItem inventoryItem, MovementType movementType, Integer quantityMoved) {
        this.moment = moment;
        this.inventoryItem = inventoryItem;
        setMovementType(movementType);
        this.quantityMoved = quantityMoved;
    }

    public StockMovement(Long id, Instant moment, InventoryItem inventoryItem, MovementType movementType, Integer quantityMoved) {
        this.id = id;
        this.moment = moment;
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

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
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
        StockMovement that = (StockMovement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Movimentação do estoque: id= " + id + ", moment= " + moment + ", inventoryItem= " + inventoryItem +
                ", movementType= " + movementType + ", quantityMoved= " + quantityMoved +"]";
    }
}
