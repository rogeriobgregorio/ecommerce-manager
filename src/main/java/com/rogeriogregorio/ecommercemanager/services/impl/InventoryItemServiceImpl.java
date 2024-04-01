package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.InsufficientQuantityInStockException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.InventoryItemRepository;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(InventoryItemServiceImpl.class);

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
    public Page<InventoryItemResponse> findAllInventoryItems(int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<InventoryItem> inventoryItemsPage = inventoryItemRepository.findAll(pageable);
            return inventoryItemsPage
                    .map(inventoryItem -> converter
                    .toResponse(inventoryItem, InventoryItemResponse.class));

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar buscar todos os itens do inventário: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar buscar todos os itens do inventário: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest) {

        inventoryItemRequest.setId(null);
        InventoryItem inventoryItem = buildInventoryItem(inventoryItemRequest);

        try {
            inventoryItemRepository.save(inventoryItem);
            updateStockMovementEntrance(inventoryItem);
            logger.info("Item do inventário criado: {}", inventoryItem);
            return converter.toResponse(inventoryItem, InventoryItemResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar criar o item do inventário: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar criar o item do inventário: " + ex);
        }
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse findInventoryItemResponseById(Long id) {

        return inventoryItemRepository
                .findById(id)
                .map(inventoryItem -> converter.toResponse(inventoryItem, InventoryItemResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Item do inventário não encontrado com o ID: {}", id);
                    return new NotFoundException("Item do inventário não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public InventoryItemResponse updateInventoryItem(InventoryItemRequest inventoryItemRequest) {

        InventoryItem inventoryItem = buildInventoryItem(inventoryItemRequest);

        try {
            inventoryItemRepository.save(inventoryItem);
            logger.info("Item do inventário atualizado: {}", inventoryItem);
            return converter.toResponse(inventoryItem, InventoryItemResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar atualizar o item do inventário: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar atualizar o item do inventário: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public void deleteInventoryItem(Long id) {

        InventoryItem inventoryItem = findInventoryItemById(id);

        try {
            inventoryItemRepository.deleteById(id);
            logger.warn("Item do inventário removido: {}", inventoryItem);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar excluir o Item do inventário: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar excluir o Item do inventário: " + ex);
        }
    }

    public InventoryItem findInventoryItemById(Long id) {

        return inventoryItemRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Item do inventário não encontrado com o ID: {}", id);
                    return new NotFoundException("Item do inventário não encontrado com o ID: " + id + ".");
                });
    }

    public InventoryItem findInventoryItemByProduct(Product product) {

        return inventoryItemRepository
                .findByProduct(product)
                .orElseThrow(() -> {
                    logger.warn("Item não encontrado no inventário: {}", product);
                    return new NotFoundException("Item não encontrado no inventário: " + product + ".");
                });
    }

    public void saveInventoryItem(InventoryItem inventoryItem) {

        try {
            inventoryItemRepository.save(inventoryItem);
            logger.info("Quantidade do item no inventário atualizada: {}", inventoryItem);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar salvar o item do inventário: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar salvar o item do inventário: " + ex.getMessage(), ex);
        }
    }

    public void validateItemInventory(InventoryItemRequest inventoryItemRequest) {

        Long inventoryItemId = inventoryItemRequest.getId();
        Long productId = inventoryItemRequest.getProductId();

        if (inventoryItemId == null && isProductPresent(productId)) {
            throw new IllegalStateException("Não é possível incluir o item ao inventário: produto já adicionado.");
        }

        if (inventoryItemId != null) {
            findInventoryItemById(inventoryItemId);
        }
    }

    public boolean isProductPresent(Long productId) {

        try {
            return inventoryItemRepository.findByProduct_Id(productId) != null;

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar verificar a presença do item no inventário: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar verificar a presença do item no inventário: " + ex);
        }
    }

    public boolean isListItemsAvailable(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);
            StockStatus itemStatus = inventoryItem.getStockStatus();

            if (itemStatus == StockStatus.OUT_OF_STOCK) {
                throw new InsufficientQuantityInStockException("O item " + product.getName() + " está esgotado.");
            }

            int quantityInStock = inventoryItem.getQuantityInStock();
            int quantityRequired = orderItem.getQuantity();

            if (quantityRequired > quantityInStock) {
                throw new InsufficientQuantityInStockException("Quantidade de "
                        + product.getName() + " em estoque insuficiente."
                        + " Quantidade requerida: " + quantityRequired
                        + ", quantidade em estoque: " + quantityInStock + "."
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
            throw new InsufficientQuantityInStockException("O item " + product.getName() + " está esgotado.");
        }

        int quantityRequired = orderItem.getQuantity();
        int quantityInStock = inventoryItem.getQuantityInStock();

        if (quantityRequired > quantityInStock) {
            throw new InsufficientQuantityInStockException("Quantidade de "
                    + product.getName() + " em estoque insuficiente."
                    + " Quantidade requerida: " + quantityRequired
                    + ", quantidade em estoque: " + quantityInStock + "."
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

        try {
            stockMovementRepository.save(stockMovement);
            logger.info("Movimentação do estoque criada: {}", stockMovement);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar criar a movimentação do estoque: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar criar a movimentação do estoque: " + ex);
        }
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
