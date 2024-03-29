package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItem;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.InventoryItemRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ProductService productService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(InventoryItemServiceImpl.class);

    @Autowired
    public InventoryItemServiceImpl(InventoryItemRepository inventoryItemRepository, ProductService productService, Converter converter) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.productService = productService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> findAllInventoryItems() {

        try {
            return inventoryItemRepository
                    .findAll()
                    .stream()
                    .map(inventoryItemEntity -> converter.toResponse(inventoryItemEntity, InventoryItemResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todos os itens do inventário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todos os itens do inventário: " + exception);
        }
    }

    public InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest) {

        inventoryItemRequest.setId(null);

        InventoryItem inventoryItem = buildInventoryItem(inventoryItemRequest);

        try {
            inventoryItemRepository.save(inventoryItem);
            logger.info("Item do inventário criado: {}", inventoryItem);
            return converter.toResponse(inventoryItem, InventoryItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o item do inventário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o item do inventário: " + exception);
        }
    }

    public InventoryItemResponse findInventoryItemResponseById(Long id) {

        return inventoryItemRepository
                .findById(id)
                .map(inventoryItemEntity -> converter.toResponse(inventoryItemEntity, InventoryItemResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Item do inventário não encontrado com o ID: {}", id);
                    return new NotFoundException("Item do inventário não encontrado com o ID: " + id + ".");
                });
    }

    public InventoryItemResponse updateInventoryItem(InventoryItemRequest inventoryItemRequest) {

        InventoryItem inventoryItem = buildInventoryItem(inventoryItemRequest);

        try {
            inventoryItemRepository.save(inventoryItem);
            logger.info("Item do inventário atualizado: {}", inventoryItem);
            return converter.toResponse(inventoryItem, InventoryItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o item do inventário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o item do inventário: " + exception);
        }
    }

    public void deleteInventoryItem(Long id) {

        findInventoryItemById(id);

        try {
            inventoryItemRepository.deleteById(id);
            logger.warn("Item do inventário removido: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o Item do inventário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o Item do inventário: " + exception);
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

    public InventoryItem buildInventoryItem(InventoryItemRequest inventoryItemRequest) {

        Product product = productService.findProductById(inventoryItemRequest.getProductId());

        validateItemInventory(inventoryItemRequest);

        Long id = inventoryItemRequest.getId();
        Integer quantityInStock = inventoryItemRequest.getQuantityInStock();
        StockStatus stockStatus = inventoryItemRequest.getStockStatus();

        return id == null ?
                new InventoryItem(product, quantityInStock, 0, stockStatus) :
                new InventoryItem(id, product, quantityInStock, 0, stockStatus);
    }

    public boolean isProductPresent(Long productId) {
        return inventoryItemRepository.findByProduct_Id(productId) != null;
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

    public InventoryItem findInventoryItemByProduct(Product product) {

        return inventoryItemRepository
                .findByProduct(product)
                .orElseThrow(() -> {
                    logger.warn("Item não encontrado no inventário: {}", product);
                    return new NotFoundException("Item não encontrado no inventário: " + product + ".");
                });
    }

    public boolean isItemsAvailable(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);

            int quantityInStock = inventoryItem.getQuantityInStock();
            int quantityRequired = orderItem.getQuantity();

            if (quantityRequired > quantityInStock) {
                throw new NotFoundException("""
                        Quantidade de %s em estoque insuficiente.
                        Quantidade requerida: %d, quantidade em estoque: %d.
                        """.formatted(product.getName(), quantityRequired, quantityInStock)
                );
            }
        }
        return true;
    }

    public void saveInventoryItem(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            InventoryItem inventoryItem = findInventoryItemByProduct(product);

            if (inventoryItem != null) {
                int purchasedQuantity = orderItem.getQuantity();
                int remainingQuantity = inventoryItem.getQuantityInStock() - purchasedQuantity;

                if (remainingQuantity == 0) {
                    inventoryItem.setStockStatus(StockStatus.OUT_OF_STOCK);
                }

                inventoryItem.setQuantityInStock(remainingQuantity);
                inventoryItem.setQuantitySold(inventoryItem.getQuantitySold() + purchasedQuantity);

                try {
                    inventoryItemRepository.save(inventoryItem);
                } catch (PersistenceException exception) {
                    logger.error("Erro ao tentar salvar o item do inventário: {}", exception.getMessage(), exception);
                    throw new RepositoryException("Erro ao tentar salvar o item do inventário: " + exception.getMessage(), exception);
                }
            }
        }
    }

}
