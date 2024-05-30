package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.rogeriogregorio.ecommercemanager.entities.InventoryItem;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class StockMovementResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private InventoryItem inventoryItem;
    private Integer movementType;
    private Integer quantityMoved;

    public StockMovementResponse() {
    }

    public StockMovementResponse(Long id, Instant moment,
                                 InventoryItem inventoryItem,
                                 MovementType movementType,
                                 Integer quantityMoved) {

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
            throw new IllegalArgumentException("The stock item movement status cannot be null");
        }

        this.movementType = movementType.getCode();
    }

    public Integer getQuantityMoved() {
        return quantityMoved;
    }

    public void setQuantityMoved(Integer quantityMoved) {
        this.quantityMoved = quantityMoved;
    }
}
