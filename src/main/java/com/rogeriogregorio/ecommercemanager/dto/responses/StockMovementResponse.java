package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.rogeriogregorio.ecommercemanager.entities.InventoryItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;

import java.io.Serial;
import java.io.Serializable;

public class StockMovementResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private InventoryItemEntity inventoryItem;
    private Integer movementType;
    private Integer quantityMoved;

    public StockMovementResponse() {
    }

    public StockMovementResponse(Long id, InventoryItemEntity inventoryItem, Integer movementType, Integer quantityMoved) {
        this.id = id;
        this.inventoryItem = inventoryItem;
        this.movementType = movementType;
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
}
