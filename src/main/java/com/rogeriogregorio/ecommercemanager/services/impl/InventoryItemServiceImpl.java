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
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(InventoryItemServiceImpl.class);

    @Autowired
    public InventoryItemServiceImpl(InventoryItemRepository inventoryItemRepository,
                                    StockMovementRepository stockMovementRepository,
                                    ProductService productService,
                                    CatchError catchError,
                                    DataMapper dataMapper) {

        this.inventoryItemRepository = inventoryItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productService = productService;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<InventoryItemResponse> findAllInventoryItems(Pageable pageable) {

        return catchError.run(() -> inventoryItemRepository.findAll(pageable)
                .map(inventoryItem -> dataMapper.map(inventoryItem, InventoryItemResponse.class)));
    }

    @Transactional
    public InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest) {

        Product product = validateProductForInventory(inventoryItemRequest);
        InventoryItem inventoryItem = InventoryItem.newBuilder()
                .withProduct(product)
                .withQuantityInStock(inventoryItemRequest.getQuantityInStock())
                .withQuantitySold(0)
                .withStockStatus(inventoryItemRequest.getStockStatus())
                .build();

        InventoryItem savedInventoryItem = catchError.run(() -> inventoryItemRepository.save(inventoryItem));
        LOGGER.info("Inventory item created: {}", savedInventoryItem);
        updateStockMovementEntrance(savedInventoryItem);
        return dataMapper.map(savedInventoryItem, InventoryItemResponse.class);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findInventoryItemById(Long id) {

        return catchError.run(() -> inventoryItemRepository.findById(id)
                .map(inventoryItem -> dataMapper.map(inventoryItem, InventoryItemResponse.class))
                .orElseThrow(() -> new NotFoundException("Inventory item not found with ID: " + id + ".")));
    }

    @Transactional
    public InventoryItemResponse updateInventoryItem(Long id, InventoryItemRequest inventoryItemRequest) {

        InventoryItem inventoryItem = getInventoryItemIfExists(id).toBuilder()
                .withStockStatus(inventoryItemRequest.getStockStatus())
                .withQuantityInStock(inventoryItemRequest.getQuantityInStock())
                .build();

        InventoryItem updatedInventoryItem = catchError.run(() -> inventoryItemRepository.save(inventoryItem));
        LOGGER.info("Inventory item updated: {}", updatedInventoryItem);
        return dataMapper.map(updatedInventoryItem, InventoryItemResponse.class);
    }

    @Transactional
    public void deleteInventoryItem(Long id) {

        InventoryItem inventoryItem = getInventoryItemIfExists(id);

        catchError.run(() -> inventoryItemRepository.delete(inventoryItem));
        LOGGER.warn("Inventory item deleted: {}", inventoryItem);
    }

    public InventoryItem findInventoryItemByProduct(Product product) {

        return catchError.run(() -> inventoryItemRepository.findByProduct(product)
                .orElseThrow(() -> new NotFoundException("Item not found in the inventory: " + product + ".")));
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

            InventoryItem updatedInventoryItem = catchError.run(() -> inventoryItemRepository.save(inventoryItem));
            LOGGER.info("Inventory item quantity updated: {}", updatedInventoryItem);
        }
    }

    private Product validateProductForInventory(InventoryItemRequest inventoryItemRequest) {

        Long productId = inventoryItemRequest.getProductId();
        Product product = productService.getProductIfExists(productId);

        boolean isItemAlreadyAdded = catchError.run(() -> inventoryItemRepository.existsByProduct(product));

        if (isItemAlreadyAdded) {
            throw new IllegalStateException("Cannot add the item to the inventory: item already added.");
        }

        return product;
    }

    public InventoryItem getInventoryItemIfExists(Long id) {

        return catchError.run(() -> inventoryItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inventory item not found with ID: " + id + ".")));
    }

    private void updateStockMovementEntrance(InventoryItem inventoryItem) {

        StockMovement stockMovement = StockMovement.newBuilder()
                .withMoment(Instant.now())
                .withInventoryItem(inventoryItem)
                .withMovementType(MovementType.ENTRANCE)
                .withQuantityMoved(inventoryItem.getQuantityInStock())
                .build();

        StockMovement stockMovementEntrance = catchError.run(() -> stockMovementRepository.save(stockMovement));
        LOGGER.info("Inventory movement entrance: {}", stockMovementEntrance);
    }
}
