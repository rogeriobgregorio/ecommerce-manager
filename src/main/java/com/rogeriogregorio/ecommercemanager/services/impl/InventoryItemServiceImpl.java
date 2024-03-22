package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
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

        InventoryItemEntity inventoryItemEntity = buildInventoryItemFromRequest(inventoryItemRequest);

        try {
            inventoryItemRepository.save(inventoryItemEntity);
            logger.info("Item do inventário criado: {}", inventoryItemEntity);
            return converter.toResponse(inventoryItemEntity, InventoryItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o item do inventário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o item do inventário: " + exception);
        }
    }

    public InventoryItemResponse findInventoryItemById(Long id) {

        return inventoryItemRepository
                .findById(id)
                .map(inventoryItemEntity -> converter.toResponse(inventoryItemEntity, InventoryItemResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Item do inventário não encontrado com o ID: {}", id);
                    return new NotFoundException("Item do inventário não encontrado com o ID: " + id + ".");
                });
    }

    public InventoryItemEntity findInventoryItemEntityById(Long id) {

        return inventoryItemRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Item do inventário não encontrado com o ID: {}", id);
                    return new NotFoundException("Item do inventário não encontrado com o ID: " + id + ".");
                });
    }

    public InventoryItemResponse updateInventoryItem(InventoryItemRequest inventoryItemRequest) {

        InventoryItemEntity inventoryItemEntity = buildInventoryItemFromRequest(inventoryItemRequest);

        try {
            inventoryItemRepository.save(inventoryItemEntity);
            logger.info("Item do inventário atualizado: {}", inventoryItemEntity);
            return converter.toResponse(inventoryItemEntity, InventoryItemResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o item do inventário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o item do inventário: " + exception);
        }
    }

    public void deleteInventoryItem(Long id) {

        findInventoryItemEntityById(id);

        try {
            inventoryItemRepository.deleteById(id);
            logger.warn("Item do inventário removido: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o Item do inventário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o Item do inventário: " + exception);
        }

    }

    public InventoryItemEntity buildInventoryItemFromRequest(InventoryItemRequest inventoryItemRequest) {

        ProductEntity product = productService.findProductEntityById(inventoryItemRequest.getProductId());

        if (inventoryItemRequest.getId() == null) {

            return new InventoryItemEntity(product, inventoryItemRequest.getQuantityInStock(), inventoryItemRequest.getStockStatus());

        } else {

            findInventoryItemEntityById(inventoryItemRequest.getId());

            return new InventoryItemEntity(inventoryItemRequest.getId(), product, inventoryItemRequest.getQuantityInStock(), inventoryItemRequest.getStockStatus());
        }
    }
}
