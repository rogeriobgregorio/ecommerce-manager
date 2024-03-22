package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.StockMovementEntity;
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


    public List<StockMovementResponse> findAllStockMovements() {

        try {
            return stockMovementRepository
                    .findAll()
                    .stream()
                    .map(stockMovementEntity -> converter.toResponse(stockMovementEntity, StockMovementResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todas as movimentações do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todas as movimentações do estoque: " + exception);
        }
    }

    public StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest) {

        stockMovementRequest.setId(null);

        StockMovementEntity stockMovementEntity = buildStockMovementFromRequest(stockMovementRequest);

        try {
            stockMovementRepository.save(stockMovementEntity);
            logger.info("Movimentação do estoque criada: {}", stockMovementEntity);
            return converter.toResponse(stockMovementEntity, StockMovementResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar a movimentação do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar a movimentação do estoque: " + exception);
        }
    }

    public StockMovementResponse findStockMovementById(Long id) {

        return stockMovementRepository
                .findById(id)
                .map(stockMovementEntity -> converter.toResponse(stockMovementEntity, StockMovementResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Movimentação do estoque não encontrado com o ID: {}", id);
                    return new NotFoundException("Movimentação do estoque não encontrado com o ID: " + id + ".");
                });
    }

    public StockMovementEntity findStockMovementEntityById(Long id) {

        return stockMovementRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Movimentação do estoque não encontrado com o ID: {}", id);
                    return new NotFoundException("Movimentação do estoque não encontrado com o ID: " + id + ".");
                });
    }

    public StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest) {

        StockMovementEntity stockMovementEntity = buildStockMovementFromRequest(stockMovementRequest);

        try {
            stockMovementRepository.save(stockMovementEntity);
            logger.info("Movimentação do estoque atualizada: {}", stockMovementEntity);
            return converter.toResponse(stockMovementEntity, StockMovementResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar a movimentação do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar a movimentação do estoque: " + exception);
        }
    }

    public void deleteStockMovement(Long id) {

        findStockMovementEntityById(id);

        try {
            stockMovementRepository.deleteById(id);
            logger.info("Movimentação do estoque removida: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir a movimentação do estoque: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir a movimentação do estoque: " + exception);
        }
    }

    public StockMovementEntity buildStockMovementFromRequest(StockMovementRequest stockMovementRequest) {

        InventoryItemEntity inventoryItem = inventoryItemService.findInventoryItemEntityById(stockMovementRequest.getId());

        if (stockMovementRequest.getId() == null) {

            return new StockMovementEntity(inventoryItem, stockMovementRequest.getMovementType(), stockMovementRequest.getQuantityMoved());

        } else {
            findStockMovementEntityById(stockMovementRequest.getId());

            return new StockMovementEntity(stockMovementRequest.getId(), inventoryItem, stockMovementRequest.getMovementType(), stockMovementRequest.getQuantityMoved());
        }
    }
}
