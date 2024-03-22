package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @Autowired
    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping(value = "/inventoryItems")
    public ResponseEntity<List<InventoryItemResponse>> getAllInventoryItem() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(inventoryItemService.findAllInventoryItems());
    }

    @PostMapping(value = "/inventoryItems")
    public ResponseEntity<InventoryItemResponse> createInventoryItem(@Valid @RequestBody InventoryItemRequest inventoryItemRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryItemService.createInventoryItem(inventoryItemRequest));
    }

    @GetMapping(value = "/inventoryItems/{id}")
    public ResponseEntity<InventoryItemResponse> getInventoryItemById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(inventoryItemService.findInventoryItemById(id));
    }

    @PutMapping(value = "/inventoryItems")
    public ResponseEntity<InventoryItemResponse> updateInventoryItem(@Valid @RequestBody InventoryItemRequest inventoryItemRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(inventoryItemService.updateInventoryItem(inventoryItemRequest));
    }

    @DeleteMapping(value = "/inventoryItems/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Long id) {

        inventoryItemService.deleteInventoryItem(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
