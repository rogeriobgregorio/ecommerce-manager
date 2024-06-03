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
    @NotNull(message = "The stock movement timestamp cannot be null.")
    private Instant moment;

    @ManyToOne
    @JoinColumn(name = "inventory_item_id")
    private InventoryItem inventoryItem;

    @NotNull(message = "The movement type cannot be null.")
    @Column(name = "movement_type")
    private Integer movementType;

    @NotNull(message = "The quantity of items moved cannot be null.")
    @Column(name = "quantity_moved")
    private Integer quantityMoved;

    public StockMovement() {
    }

    private StockMovement(Builder builder) {
        setId(builder.id);
        setMoment(builder.moment);
        setInventoryItem(builder.inventoryItem);
        movementType = builder.movementType;
        setQuantityMoved(builder.quantityMoved);
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
            throw new IllegalArgumentException("The stock item movement status cannot be null.");
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
        return "[Stock Movement: id= " + id
                + ", moment= " + moment
                + ", inventoryItem= " + inventoryItem
                + ", movementType= " + movementType
                + ", quantityMoved= " + quantityMoved +"]";
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withMoment(this.moment)
                .withInventoryItem(this.inventoryItem)
                .withMovementType(MovementType.valueOf(this.movementType))
                .withQuantityMoved(this.quantityMoved);
    }

    public static final class Builder {

        private Long id;
        private Instant moment;
        private InventoryItem inventoryItem;
        private Integer movementType;
        private Integer quantityMoved;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withMoment(Instant moment) {
            this.moment = moment;
            return this;
        }

        public Builder withInventoryItem(InventoryItem inventoryItem) {
            this.inventoryItem = inventoryItem;
            return this;
        }

        public Builder withMovementType(MovementType movementType) {
            this.movementType = movementType.getCode();
            return this;
        }

        public Builder withQuantityMoved(Integer quantityMoved) {
            this.quantityMoved = quantityMoved;
            return this;
        }

        public StockMovement build() {
            return new StockMovement(this);
        }
    }
}
