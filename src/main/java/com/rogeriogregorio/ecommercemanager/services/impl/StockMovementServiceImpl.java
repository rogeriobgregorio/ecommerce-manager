package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.catchError;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
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
    private final catchError catchError;
    private final DataMapper dataMapper;
    private static final Logger logger = LogManager.getLogger(StockMovementServiceImpl.class);

    @Autowired
    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                    InventoryItemService inventoryItemService,
                                    catchError catchError, DataMapper dataMapper) {

        this.stockMovementRepository = stockMovementRepository;
        this.inventoryItemService = inventoryItemService;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> findAllStockMovements(Pageable pageable) {

        return catchError.run(
                () -> stockMovementRepository.findAll(pageable)
                        .map(stockMovement -> dataMapper.map(stockMovement, StockMovementResponse.class)),
                "Error while trying to fetch all inventory movements: "
        );
    }

    @Transactional
    public StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest) {

        Long inventoryItemId = stockMovementRequest.getInventoryItemId();
        InventoryItem inventoryItem = inventoryItemService.getInventoryItemIfExists(inventoryItemId);

        StockMovement stockMovement = StockMovement.newBuilder()
                .withMoment(Instant.now())
                .withInventoryItem(inventoryItem)
                .withMovementType(stockMovementRequest.getMovementType())
                .withQuantityMoved(stockMovementRequest.getQuantityMoved())
                .build();

        StockMovement savedStockMovement = catchError.run(
                () -> stockMovementRepository.save(stockMovement),
                "Error while trying to create the inventory movement:"
        );

        logger.info("Inventory movement created: {}", savedStockMovement);
        return dataMapper.map(savedStockMovement, StockMovementResponse.class);
    }

    @Transactional(readOnly = true)
    public StockMovementResponse findStockMovementById(Long id) {

        return catchError.run(
                () -> stockMovementRepository.findById(id)
                        .map(stockMovement -> dataMapper.map(stockMovement, StockMovementResponse.class))
                        .orElseThrow(() -> new NotFoundException("Inventory movement not found with ID: " + id + ".")),
                "Error while trying to fetch inventory movement by ID: "
        );
    }

    @Transactional
    public StockMovementResponse updateStockMovement(Long id, StockMovementRequest stockMovementRequest) {

        Long inventoryItemId = stockMovementRequest.getInventoryItemId();
        InventoryItem inventoryItem = inventoryItemService.getInventoryItemIfExists(inventoryItemId);

        StockMovement currentStockMovement = getUserIfExists(id).toBuilder()
                .withMoment(Instant.now())
                .withInventoryItem(inventoryItem)
                .withMovementType(stockMovementRequest.getMovementType())
                .withQuantityMoved(stockMovementRequest.getQuantityMoved())
                .build();

        StockMovement updateStockMovement = catchError.run(
                () -> stockMovementRepository.save(currentStockMovement),
                "Error while trying to update inventory movement: "
        );

        logger.info("Inventory movement updated: {}", updateStockMovement);
        return dataMapper.map(updateStockMovement, StockMovementResponse.class);
    }

    @Transactional
    public void deleteStockMovement(Long id) {

        StockMovement stockMovement = getUserIfExists(id);

        catchError.run(() -> {
            stockMovementRepository.delete(stockMovement);
            return null;
        }, "Error while trying to delete stock movement: ");

        logger.info("Stock movement deleted: {}", stockMovement);
    }

    public StockMovement getUserIfExists(Long id) {

        return catchError.run(
                () -> stockMovementRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Inventory movement not found with ID: " + id + ".")),
                "Error while trying to verify the existence of the stock movement by ID: "
        );
    }

    public void updateStockMovementExit(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = inventoryItemService.findInventoryItemByProduct(product);

            StockMovement stockMovement = StockMovement.newBuilder()
                    .withMoment(Instant.now())
                    .withInventoryItem(inventoryItem)
                    .withMovementType(MovementType.EXIT)
                    .withQuantityMoved(orderItem.getQuantity())
                    .build();

            StockMovement stockMovementExit = catchError.run(
                    () -> stockMovementRepository.save(stockMovement),
                    "Error while trying to save inventory movement: "
            );

            logger.info("Inventory movement exit: {}", stockMovementExit);
        }
    }
}
