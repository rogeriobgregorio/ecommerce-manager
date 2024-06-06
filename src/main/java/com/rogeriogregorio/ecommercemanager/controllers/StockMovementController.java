package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @Autowired
    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @GetMapping
    public ResponseEntity<List<StockMovementResponse>> getAllStockMovement(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockMovementService.findAllStockMovements(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<StockMovementResponse> postStockMovement(
            @Valid @RequestBody StockMovementRequest stockMovementRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(stockMovementService.createStockMovement(stockMovementRequest));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<StockMovementResponse> getStockMovementById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockMovementService.findStockMovementById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<StockMovementResponse> putStockMovement(@PathVariable Long id,
            @Valid @RequestBody StockMovementRequest stockMovementRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stockMovementService.updateStockMovement(id, stockMovementRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteStockMovement(@PathVariable Long id) {

        stockMovementService.deleteStockMovement(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
