package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @Autowired
    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping(value = "/stockMovements")
    public ResponseEntity<List<StockMovementResponse>> getAllStockMovement() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockMovementService.findAllStockMovements());
    }

    @PostMapping(value = "/stockMovements")
    public ResponseEntity<StockMovementResponse> createStockMovement(@Valid @RequestBody StockMovementRequest stockMovementRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stockMovementService.createStockMovement(stockMovementRequest));
    }

    @GetMapping(value = "/stockMovements/{id}")
    public ResponseEntity<StockMovementResponse> getStockMovementById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockMovementService.findStockMovementById(id));
    }

    @PutMapping(value = "/stockMovements")
    public ResponseEntity<StockMovementResponse> updateStockMovement(@Valid @RequestBody StockMovementRequest stockMovementRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockMovementService.updateStockMovement(stockMovementRequest));
    }

    @DeleteMapping(value = "/stockMovements/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {

        stockMovementService.deleteStockMovement(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
