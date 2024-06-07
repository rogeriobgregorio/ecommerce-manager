package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.StockException;
import com.rogeriogregorio.ecommercemanager.repositories.InventoryItemRepository;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(InventoryItemServiceImpl.class);

    @Autowired
    public InventoryItemServiceImpl(InventoryItemRepository inventoryItemRepository,
                                    StockMovementRepository stockMovementRepository,
                                    ProductService productService,
                                    ErrorHandler errorHandler,
                                    DataMapper dataMapper) {

        this.inventoryItemRepository = inventoryItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productService = productService;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<InventoryItemResponse> findAllInventoryItems(Pageable pageable) {

        return errorHandler.catchException(() -> inventoryItemRepository.findAll(pageable),
                        "Error while trying to fetch all inventory items: ")
                .map(inventoryItem -> dataMapper.toResponse(inventoryItem, InventoryItemResponse.class));
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest) {

        Product product = validateProductForInventory(inventoryItemRequest);
        InventoryItem inventoryItem = InventoryItem.newBuilder()
                .withProduct(product)
                .withQuantityInStock(inventoryItemRequest.getQuantityInStock())
                .withQuantitySold(0)
                .withStockStatus(inventoryItemRequest.getStockStatus())
                .build();

        errorHandler.catchException(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to create the inventory item: ");
        logger.info("Inventory item created: {}", inventoryItem);

        updateStockMovementEntrance(inventoryItem);
        return dataMapper.toResponse(inventoryItem, InventoryItemResponse.class);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findInventoryItemById(Long id) {

        return errorHandler.catchException(() -> inventoryItemRepository.findById(id),
                        "Error while trying to find the inventory item by ID: ")
                .map(inventoryItem -> dataMapper.toResponse(inventoryItem, InventoryItemResponse.class))
                .orElseThrow(() -> new NotFoundException("Inventory item response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse updateInventoryItem(Long id, InventoryItemRequest inventoryItemRequest) {

        InventoryItem inventoryItem = getInventoryItemIfExists(id).toBuilder()
                .withStockStatus(inventoryItemRequest.getStockStatus())
                .withQuantityInStock(inventoryItemRequest.getQuantityInStock())
                .build();

        errorHandler.catchException(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to update the inventory item: ");
        logger.info("Inventory item updated: {}", inventoryItem);

        return dataMapper.toResponse(inventoryItem, InventoryItemResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteInventoryItem(Long id) {

        InventoryItem inventoryItem = getInventoryItemIfExists(id);

        errorHandler.catchException(() -> {
            inventoryItemRepository.delete(inventoryItem);
            return null;
        }, "Error while trying to delete the inventory item: ");
        logger.warn("Inventory item deleted: {}", inventoryItem);
    }

    public InventoryItem findInventoryItemByProduct(Product product) {

        return errorHandler.catchException(() -> inventoryItemRepository.findByProduct(product),
                        "Error while trying to find the inventory item: ")
                .orElseThrow(() -> new NotFoundException("Item not found in the inventory: " + product + "."));
    }

    public void validateItemListAvailability(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);
            StockStatus inventoryItemStatus = inventoryItem.getStockStatus();

            if (inventoryItemStatus == StockStatus.OUT_OF_STOCK) {
                throw new StockException("The item " + product.getName() + " is out of stock.");
            }

            int quantityInStock = inventoryItem.getQuantityInStock();
            int quantityRequired = orderItem.getQuantity();

            if (quantityRequired > quantityInStock) {
                throw new StockException("Insufficient"
                        + " quantity of " + product.getName() + " in stock."
                        + " Required quantity: " + quantityRequired
                        + ", available quantity: " + quantityInStock + "."
                );
            }
        }
    }

    public OrderItem validateItemAvailability(OrderItem orderItem) {

        Product product = orderItem.getProduct();
        InventoryItem inventoryItem = findInventoryItemByProduct(product);
        StockStatus inventoryItemStatus = inventoryItem.getStockStatus();

        if (inventoryItemStatus == StockStatus.OUT_OF_STOCK) {
            throw new StockException("The item " + product.getName() + " is out of stock.");
        }

        int quantityRequired = orderItem.getQuantity();
        int quantityInStock = inventoryItem.getQuantityInStock();

        if (quantityRequired > quantityInStock) {
            throw new StockException("Insufficient"
                    + " quantity of " + product.getName() + " in stock."
                    + " Required quantity: " + quantityRequired
                    + ", available quantity: " + quantityInStock + "."
            );
        }

        return orderItem;
    }

    public void updateInventoryItemQuantity(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);

            int orderItemQuantity = orderItem.getQuantity();
            int quantityInStockUpdated = inventoryItem.getQuantityInStock() - orderItemQuantity;
            int quantitySoldUpdated = inventoryItem.getQuantitySold() + orderItemQuantity;

            if (quantityInStockUpdated == 0) {
                inventoryItem.setStockStatus(StockStatus.OUT_OF_STOCK);
            }

            inventoryItem.setQuantityInStock(quantityInStockUpdated);
            inventoryItem.setQuantitySold(quantitySoldUpdated);

            errorHandler.catchException(() -> inventoryItemRepository.save(inventoryItem),
                    "Error while trying to save the inventory item: ");
            logger.info("Inventory item quantity updated: {}", inventoryItem);
        }
    }

    private Product validateProductForInventory(InventoryItemRequest inventoryItemRequest) {

        Long productId = inventoryItemRequest.getProductId();
        Product product = productService.getProductIfExists(productId);

        boolean isItemAlreadyAdded = errorHandler.catchException(() -> inventoryItemRepository.existsByProduct(product),
                "Error while trying to check the presence of the item in the inventory: ");

        if (isItemAlreadyAdded) {
            throw new IllegalStateException("Cannot add the item to the inventory: item already added.");
        }

        return product;
    }

    public InventoryItem getInventoryItemIfExists(Long id) {

        return errorHandler.catchException(() -> {

            if (!inventoryItemRepository.existsById(id)) {
                throw new NotFoundException("Inventory item not exists with ID: " + id + ".");
            }

            return dataMapper.toEntity(inventoryItemRepository.findById(id), InventoryItem.class);
        }, "Error while trying to verify the existence of the inventory item by ID: ");
    }

    private void updateStockMovementEntrance(InventoryItem inventoryItem) {

        StockMovement stockMovement = StockMovement.newBuilder()
                .withMoment(Instant.now())
                .withInventoryItem(inventoryItem)
                .withMovementType(MovementType.ENTRANCE)
                .withQuantityMoved(inventoryItem.getQuantityInStock())
                .build();

        errorHandler.catchException(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to create the inventory movement: ");
        logger.info("Inventory movement entrance: {}", stockMovement);
    }
}
