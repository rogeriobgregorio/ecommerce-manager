package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.InsufficientQuantityInStockException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.InventoryItemRepository;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class InventoryItemServiceImpl extends ErrorHandlerTemplateImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;
    private final Converter converter;

    @Autowired
    public InventoryItemServiceImpl(InventoryItemRepository inventoryItemRepository,
                                    StockMovementRepository stockMovementRepository,
                                    ProductService productService,
                                    Converter converter) {

        this.inventoryItemRepository = inventoryItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.productService = productService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<InventoryItemResponse> findAllInventoryItems(Pageable pageable) {

        return handleError(() -> inventoryItemRepository.findAll(pageable),
                "Error while trying to fetch all inventory items: ")
                .map(inventoryItem -> converter.toResponse(inventoryItem, InventoryItemResponse.class));
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest) {

        inventoryItemRequest.setId(null);
        InventoryItem inventoryItem = buildInventoryItem(inventoryItemRequest);

        handleError(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to create the inventory item: ");
        logger.info("Inventory item created: {}", inventoryItem);

        updateStockMovementEntrance(inventoryItem);
        return converter.toResponse(inventoryItem, InventoryItemResponse.class);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findInventoryItemResponseById(Long id) {

        return handleError(() -> inventoryItemRepository.findById(id),
                "Error while trying to find the inventory item by ID: ")
                .map(inventoryItem -> converter.toResponse(inventoryItem, InventoryItemResponse.class))
                .orElseThrow(() -> new NotFoundException("Inventory item not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse updateInventoryItem(InventoryItemRequest inventoryItemRequest) {

        InventoryItem inventoryItem = buildInventoryItem(inventoryItemRequest);

        handleError(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to update the inventory item: ");
        logger.info("Inventory item updated: {}", inventoryItem);

        return converter.toResponse(inventoryItem, InventoryItemResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteInventoryItem(Long id) {

        InventoryItem inventoryItem = findInventoryItemById(id);

        handleError(() -> {
            inventoryItemRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the inventory item: ");
        logger.warn("Inventory item removed: {}", inventoryItem);
    }

    public InventoryItem findInventoryItemById(Long id) {

        return handleError(() -> inventoryItemRepository.findById(id),
                "Error while trying to find the inventory item by ID: ")
                .orElseThrow(() -> new NotFoundException("Inventory item not found with ID: " + id + "."));
    }

    public InventoryItem findInventoryItemByProduct(Product product) {

        return handleError(() -> inventoryItemRepository.findByProduct(product),
                "Error while trying to find the inventory item: ")
                .orElseThrow(() -> new NotFoundException("Item not found in the inventory: " + product + "."));
    }

    public void saveInventoryItem(InventoryItem inventoryItem) {

        handleError(() -> inventoryItemRepository.save(inventoryItem),
                "Error while trying to save the inventory item: ");
        logger.info("Inventory item quantity updated: {}", inventoryItem);
    }

    public void validateItemInventory(InventoryItemRequest inventoryItemRequest) {

        Long inventoryItemId = inventoryItemRequest.getId();
        Long productId = inventoryItemRequest.getProductId();

        if (inventoryItemId == null && isProductPresent(productId)) {
            throw new IllegalStateException("Cannot add the item to the inventory: product already added.");
        }

        if (inventoryItemId != null) {
            findInventoryItemById(inventoryItemId);
        }
    }

    public boolean isProductPresent(Long productId) {

        return handleError(() -> inventoryItemRepository.findByProduct_Id(productId) != null,
                "Error while trying to check the presence of the item in the inventory: ");
    }

    public boolean isListItemsAvailable(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);
            StockStatus itemStatus = inventoryItem.getStockStatus();

            if (itemStatus == StockStatus.OUT_OF_STOCK) {
                throw new InsufficientQuantityInStockException("The item " + product.getName() + " is out of stock.");
            }

            int quantityInStock = inventoryItem.getQuantityInStock();
            int quantityRequired = orderItem.getQuantity();

            if (quantityRequired > quantityInStock) {
                throw new InsufficientQuantityInStockException("Insufficient"
                        + " quantity of " + product.getName() + " in stock."
                        + " Required quantity: " + quantityRequired
                        + ", available quantity: " + quantityInStock + "."
                );
            }
        }
        return true;
    }

    public boolean isItemAvailable(OrderItem orderItem) {

        Product product = orderItem.getProduct();
        InventoryItem inventoryItem = findInventoryItemByProduct(product);
        StockStatus itemStatus = inventoryItem.getStockStatus();

        if (itemStatus == StockStatus.OUT_OF_STOCK) {
            throw new InsufficientQuantityInStockException("The item " + product.getName() + " is out of stock.");
        }

        int quantityRequired = orderItem.getQuantity();
        int quantityInStock = inventoryItem.getQuantityInStock();

        if (quantityRequired > quantityInStock) {
            throw new InsufficientQuantityInStockException("Insufficient"
                    + " quantity of " + product.getName() + " in stock."
                    + " Required quantity: " + quantityRequired
                    + ", available quantity: " + quantityInStock + "."
            );
        }
        return true;
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

    public void updateStockMovementEntrance(InventoryItem inventoryItem) {

        Instant moment = Instant.now();
        int quantity = inventoryItem.getQuantityInStock();
        StockMovement stockMovement = new StockMovement(moment, inventoryItem, MovementType.ENTRANCE, quantity);

        handleError(() -> stockMovementRepository.save(stockMovement),
                "Error while trying to create the inventory movement: ");
        logger.info("Inventory movement created: {}", stockMovement);
    }

    public InventoryItem buildInventoryItem(InventoryItemRequest inventoryItemRequest) {

        Long productId = inventoryItemRequest.getProductId();
        Product product = productService.findProductById(productId);

        validateItemInventory(inventoryItemRequest);

        Long id = inventoryItemRequest.getId();
        Integer quantityInStock = inventoryItemRequest.getQuantityInStock();
        StockStatus stockStatus = inventoryItemRequest.getStockStatus();

        return new InventoryItem(id, product, quantityInStock, 0, stockStatus);
    }
}
