package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;

import java.io.Serial;
import java.io.Serializable;

public class StockMovementRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long inventoryItemId;
    private Integer movementType;
    private Integer quantityMoved;

    public StockMovementRequest() {
    }

    public StockMovementRequest(Long inventoryItemId, MovementType movementType, Integer quantityMoved) {
        this.inventoryItemId = inventoryItemId;
        setMovementType(movementType);
        this.quantityMoved = quantityMoved;
    }

    public StockMovementRequest(Long id, Long inventoryItemId, MovementType movementType, Integer quantityMoved) {
        this.id = id;
        this.inventoryItemId = inventoryItemId;
        setMovementType(movementType);
        this.quantityMoved = quantityMoved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
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
