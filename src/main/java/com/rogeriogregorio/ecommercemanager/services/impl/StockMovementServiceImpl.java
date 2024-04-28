package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplate;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final InventoryItemService inventoryItemService;
    private final ErrorHandlerTemplate errorHandler;
    private final Converter converter;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                    InventoryItemService inventoryItemService,
                                    ErrorHandlerTemplate errorHandler, Converter converter) {

        this.stockMovementRepository = stockMovementRepository;
        this.inventoryItemService = inventoryItemService;
        this.errorHandler = errorHandler;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> findAllStockMovements(Pageable pageable) {

        return errorHandler.catchException(() -> stockMovementRepository.findAll(pageable),
                "Error while trying to fetch all inventory movements: ")
                .map(stockMovement -> converter.toResponse(stockMovement, StockMovementResponse.class));
    }

    @Transactional(readOnly = false)
    public StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest) {

        stockMovementRequest.setId(null);
        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        errorHandler.catchException(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to create the inventory movement:");
        logger.info("Inventory movement created: {}", stockMovement);

        return converter.toResponse(stockMovement, StockMovementResponse.class);
    }

    @Transactional(readOnly = true)
    public StockMovementResponse findStockMovementById(Long id) {

        return errorHandler.catchException(() -> stockMovementRepository.findById(id),
                "Error while trying to fetch inventory movement by ID: " + id)
                .map(stockMovement -> converter.toResponse(stockMovement, StockMovementResponse.class))
                .orElseThrow(() -> new NotFoundException("Inventory movement not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest) {

        isStockMovementExists(stockMovementRequest.getId());
        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        errorHandler.catchException(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to update inventory movement: ");
        logger.info("Inventory movement updated: {}", stockMovement);

        return converter.toResponse(stockMovement, StockMovementResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteStockMovement(Long id) {

        isStockMovementExists(id);

        errorHandler.catchException(() -> {
            stockMovementRepository.deleteById(id);
            return null;
        }, "Error while trying to delete inventory movement: ");
        logger.info("Inventory movement removed: {}", id);
    }

    private void isStockMovementExists(Long id) {

        boolean isStockMovementExists = errorHandler.catchException(() -> stockMovementRepository.existsById(id),
                "Error while trying to check the presence of the stock movement: ");

        if (!isStockMovementExists) {
            throw new NotFoundException("Stock Movement not found with ID: " + id + ".");
        }
    }

    public void updateStockMovementExit(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = inventoryItemService.findInventoryItemByProduct(product);

            int outputQuantity = orderItem.getQuantity();

            Instant moment = Instant.now();
            StockMovement stockMovement = new StockMovement(moment, inventoryItem, MovementType.EXIT, outputQuantity);

            saveStockMovement(stockMovement);
            logger.info("Inventory movement exit: {}", stockMovement);
        }
    }

    private void saveStockMovement(StockMovement stockMovement) {

        errorHandler.catchException(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to save inventory movement: ");
    }

    private StockMovement buildStockMovement(StockMovementRequest stockMovementRequest) {

        Long id = stockMovementRequest.getId();
        Long inventoryItemId = stockMovementRequest.getInventoryItemId();
        InventoryItem inventoryItem = inventoryItemService.findInventoryItemById(inventoryItemId);
        Instant moment = Instant.now();
        MovementType movementType = stockMovementRequest.getMovementType();
        int quantityMoved = stockMovementRequest.getQuantityMoved();

        return new StockMovement(id, moment, inventoryItem, movementType, quantityMoved);
    }
}
