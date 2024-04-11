package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class StockMovementServiceImpl extends ErrorHandlerTemplateImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final InventoryItemService inventoryItemService;
    private final Converter converter;

    @Autowired
    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                    InventoryItemService inventoryItemService,
                                    Converter converter) {

        this.stockMovementRepository = stockMovementRepository;
        this.inventoryItemService = inventoryItemService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> findAllStockMovements(Pageable pageable) {

        return handleError(() -> stockMovementRepository.findAll(pageable),
                "Error while trying to fetch all inventory movements: ")
                .map(stockMovement -> converter
                .toResponse(stockMovement, StockMovementResponse.class));
    }

    @Transactional(readOnly = false)
    public StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest) {

        stockMovementRequest.setId(null);
        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        handleError(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to create the inventory movement:");
        logger.info("Inventory movement created: {}", stockMovement);

        return converter.toResponse(stockMovement, StockMovementResponse.class);
    }

    @Transactional(readOnly = true)
    public StockMovementResponse findStockMovementResponseById(Long id) {

        return handleError(() -> stockMovementRepository.findById(id),
                "Error while trying to fetch inventory movement by ID: " + id)
                .map(stockMovement -> converter.toResponse(stockMovement, StockMovementResponse.class))
                .orElseThrow(() -> new NotFoundException("Inventory movement not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest) {

        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        handleError(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to update inventory movement: ");
        logger.info("Inventory movement updated: {}", stockMovement);

        return converter.toResponse(stockMovement, StockMovementResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteStockMovement(Long id) {

        findStockMovementById(id);

        handleError(() -> {
            stockMovementRepository.deleteById(id);
            return null;
        }, "Error while trying to delete inventory movement: ");
        logger.info("Inventory movement removed: {}", id);
    }

    public StockMovement findStockMovementById(Long id) {

        return handleError(() -> stockMovementRepository.findById(id),
                "Error while trying to fetch inventory movement by ID: ")
                .orElseThrow(() -> new NotFoundException("Inventory movement not found with ID: " + id + "."));
    }

    public void saveStockMovement(StockMovement stockMovement) {

        handleError(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to save inventory movement: ");
        logger.info("Inventory movement saved: {}", stockMovement);
    }

    public void updateStockMovementExit(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = inventoryItemService.findInventoryItemByProduct(product);

            int outputQuantity = orderItem.getQuantity();

            Instant moment = Instant.now();
            StockMovement stockMovement = new StockMovement(moment, inventoryItem, MovementType.EXIT, outputQuantity);

            saveStockMovement(stockMovement);
        }
    }

    public StockMovement buildStockMovement(StockMovementRequest stockMovementRequest) {

        Long id = stockMovementRequest.getId();
        InventoryItem inventoryItem = inventoryItemService.findInventoryItemById(id);
        Instant moment = Instant.now();
        MovementType movementType = stockMovementRequest.getMovementType();
        int quantityMoved = stockMovementRequest.getQuantityMoved();

        return new StockMovement(id, moment, inventoryItem, movementType, quantityMoved);
    }
}
