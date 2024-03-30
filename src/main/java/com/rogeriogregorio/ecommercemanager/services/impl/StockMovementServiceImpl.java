package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final InventoryItemService inventoryItemService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(StockMovementServiceImpl.class);

    @Autowired
    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository, InventoryItemService inventoryItemService, Converter converter) {
        this.stockMovementRepository = stockMovementRepository;
        this.inventoryItemService = inventoryItemService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse> findAllStockMovements() {

        try {
            return stockMovementRepository
                    .findAll()
                    .stream()
                    .map(stockMovement -> converter.toResponse(stockMovement, StockMovementResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todas as movimentações do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todas as movimentações do estoque: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest) {

        stockMovementRequest.setId(null);

        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        try {
            stockMovementRepository.save(stockMovement);
            logger.info("Movimentação do estoque criada: {}", stockMovement);
            return converter.toResponse(stockMovement, StockMovementResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar a movimentação do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar a movimentação do estoque: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public StockMovementResponse findStockMovementResponseById(Long id) {

        return stockMovementRepository
                .findById(id)
                .map(stockMovement -> converter.toResponse(stockMovement, StockMovementResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Movimentação do estoque não encontrado com o ID: {}", id);
                    return new NotFoundException("Movimentação do estoque não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest) {

        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        try {
            stockMovementRepository.save(stockMovement);
            logger.info("Movimentação do estoque atualizada: {}", stockMovement);
            return converter.toResponse(stockMovement, StockMovementResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar a movimentação do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar a movimentação do estoque: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteStockMovement(Long id) {

        findStockMovementById(id);

        try {
            stockMovementRepository.deleteById(id);
            logger.info("Movimentação do estoque removida: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir a movimentação do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir a movimentação do estoque: " + exception);
        }
    }

    public StockMovement findStockMovementById(Long id) {

        return stockMovementRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Movimentação do estoque não encontrado com o ID: {}", id);
                    return new NotFoundException("Movimentação do estoque não encontrado com o ID: " + id + ".");
                });
    }

    public void saveStockMovement(StockMovement stockMovement) {

        try {
            stockMovementRepository.save(stockMovement);
            logger.info("Movimentação do estoque salva: {}", stockMovement);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar salvar a movimentação do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao salvar criar a movimentação do estoque: " + exception);
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
        }
    }

    public StockMovement buildStockMovement(StockMovementRequest stockMovementRequest) {

        InventoryItem inventoryItem = inventoryItemService.findInventoryItemById(stockMovementRequest.getId());
        Long id = stockMovementRequest.getId();
        Instant moment = Instant.now();
        MovementType movementType = stockMovementRequest.getMovementType();
        int quantityMoved = stockMovementRequest.getQuantityMoved();

        return new StockMovement(id, moment, inventoryItem, movementType, quantityMoved);
    }
}
