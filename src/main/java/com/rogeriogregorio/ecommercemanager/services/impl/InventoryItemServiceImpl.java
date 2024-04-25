package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.InsufficientStockException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.InventoryItemRepository;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplate;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
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
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;
    private final ErrorHandlerTemplate errorHandler;
    private final Converter converter;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public InventoryItemServiceImpl(InventoryItemRepository inventoryItemRepository,
                                    StockMovementRepository stockMovementRepository,
                                    ProductService productService,
                                    ErrorHandlerTemplate errorHandler, Converter converter) {

        this.inventoryItemRepository = inventoryItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productService = productService;
        this.errorHandler = errorHandler;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<InventoryItemResponse> findAllInventoryItems(Pageable pageable) {

        return errorHandler.catchException(() -> inventoryItemRepository.findAll(pageable),
                        "Error while trying to fetch all inventory items: ")
                .map(inventoryItem -> converter.toResponse(inventoryItem, InventoryItemResponse.class));
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest) {

        inventoryItemRequest.setId(null);
        InventoryItem inventoryItem = buildCreateInventoryItem(inventoryItemRequest);

        errorHandler.catchException(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to create the inventory item: ");
        logger.info("Inventory item created: {}", inventoryItem);

        updateStockMovementEntrance(inventoryItem);
        return converter.toResponse(inventoryItem, InventoryItemResponse.class);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findInventoryItemResponseById(Long id) {

        return errorHandler.catchException(() -> inventoryItemRepository.findById(id),
                        "Error while trying to find the inventory item by ID: ")
                .map(inventoryItem -> converter.toResponse(inventoryItem, InventoryItemResponse.class))
                .orElseThrow(() -> new NotFoundException("Inventory item response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse updateInventoryItem(InventoryItemRequest inventoryItemRequest) {

        InventoryItem inventoryItem = buildUpdateInventoryItem(inventoryItemRequest);

        errorHandler.catchException(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to update the inventory item: ");
        logger.info("Inventory item updated: {}", inventoryItem);

        return converter.toResponse(inventoryItem, InventoryItemResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteInventoryItem(Long id) {

        isInventoryItemExists(id);

        errorHandler.catchException(() -> {
            inventoryItemRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the inventory item: ");
        logger.warn("Inventory item removed: {}", id);
    }

    public InventoryItem findInventoryItemById(Long id) {

        return errorHandler.catchException(() -> inventoryItemRepository.findById(id),
                        "Error while trying to find the inventory item by ID: ")
                .orElseThrow(() -> new NotFoundException("Inventory item not found with ID: " + id + "."));
    }

    public InventoryItem findInventoryItemByProduct(Product product) {

        return errorHandler.catchException(() -> inventoryItemRepository.findByProduct(product),
                        "Error while trying to find the inventory item: ")
                .orElseThrow(() -> new NotFoundException("Item not found in the inventory: " + product + "."));
    }

    public void isListItemsAvailable(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);
            StockStatus inventoryItemStatus = inventoryItem.getStockStatus();

            if (inventoryItemStatus == StockStatus.OUT_OF_STOCK) {
                throw new InsufficientStockException("The item " + product.getName() + " is out of stock.");
            }

            int quantityInStock = inventoryItem.getQuantityInStock();
            int quantityRequired = orderItem.getQuantity();

            if (quantityRequired > quantityInStock) {
                throw new InsufficientStockException("Insufficient"
                        + " quantity of " + product.getName() + " in stock."
                        + " Required quantity: " + quantityRequired
                        + ", available quantity: " + quantityInStock + "."
                );
            }
        }
    }

    public void isItemAvailable(OrderItem orderItem) {

        Product product = orderItem.getProduct();
        InventoryItem inventoryItem = findInventoryItemByProduct(product);
        StockStatus inventoryItemStatus = inventoryItem.getStockStatus();

        if (inventoryItemStatus == StockStatus.OUT_OF_STOCK) {
            throw new InsufficientStockException("The item " + product.getName() + " is out of stock.");
        }

        int quantityRequired = orderItem.getQuantity();
        int quantityInStock = inventoryItem.getQuantityInStock();

        if (quantityRequired > quantityInStock) {
            throw new InsufficientStockException("Insufficient"
                    + " quantity of " + product.getName() + " in stock."
                    + " Required quantity: " + quantityRequired
                    + ", available quantity: " + quantityInStock + "."
            );
        }
    }

    public void updateInventoryItemQuantity(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);

            int purchasedQuantity = orderItem.getQuantity();
            int remainingQuantity = inventoryItem.getQuantityInStock() - purchasedQuantity;

            if (remainingQuantity == 0) {
                inventoryItem.setStockStatus(StockStatus.OUT_OF_STOCK);
            }

            inventoryItem.setQuantityInStock(remainingQuantity);
            inventoryItem.setQuantitySold(inventoryItem.getQuantitySold() + purchasedQuantity);

            saveInventoryItem(inventoryItem);
        }
    }

    private Product validateItemInventory(InventoryItemRequest inventoryItemRequest) {

        Long productId = inventoryItemRequest.getProductId();
        Product product = productService.findProductById(productId);

        isProductInInventory(product);

        return product;
    }

    private void isProductInInventory(Product product) {

        boolean isProductInInventory = errorHandler.catchException(() -> inventoryItemRepository.existsByProduct(product),
                "Error while trying to check the presence of the item in the inventory: ");

        if (isProductInInventory) {
            throw new IllegalStateException("Cannot add the item to the inventory: product already added.");
        }
    }

    private void isInventoryItemExists(Long id) {

        boolean isInventoryItemExists = errorHandler.catchException(() -> inventoryItemRepository.existsById(id),
                "Error while trying to check the presence of the inventory item: ");

        if (!isInventoryItemExists) {
            throw new NotFoundException("Inventory item not found with ID: " + id + ".");
        }
    }

    private void saveInventoryItem(InventoryItem inventoryItem) {

        errorHandler.catchException(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to save the inventory item: ");
        logger.info("Inventory item quantity updated: {}", inventoryItem);
    }

    private void updateStockMovementEntrance(InventoryItem inventoryItem) {

        Instant moment = Instant.now();
        int quantity = inventoryItem.getQuantityInStock();
        StockMovement stockMovement = new StockMovement(moment, inventoryItem, MovementType.ENTRANCE, quantity);

        errorHandler.catchException(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to create the inventory movement: ");
        logger.info("Inventory movement created: {}", stockMovement);
    }

    private InventoryItem buildCreateInventoryItem(InventoryItemRequest inventoryItemRequest) {

        Product product = validateItemInventory(inventoryItemRequest);
        Integer quantityInStock = inventoryItemRequest.getQuantityInStock();
        StockStatus stockStatus = inventoryItemRequest.getStockStatus();

        return new InventoryItem(product, quantityInStock, 0, stockStatus);
    }

    private InventoryItem buildUpdateInventoryItem(InventoryItemRequest inventoryItemRequest) {

        InventoryItem inventoryItem = findInventoryItemById(inventoryItemRequest.getId());
        inventoryItem.setStockStatus(inventoryItemRequest.getStockStatus());
        inventoryItem.setQuantityInStock(inventoryItemRequest.getQuantityInStock());

        return inventoryItem;
    }
}
