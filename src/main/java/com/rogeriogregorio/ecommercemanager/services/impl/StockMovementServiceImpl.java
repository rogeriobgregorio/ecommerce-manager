package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.StockMovementService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class StockMovementServiceImpl extends ErrorHandlerTemplateImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final InventoryItemService inventoryItemService;
    private final Converter converter;

    @Autowired
    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                    InventoryItemService inventoryItemService,
                                    Converter converter) {

        this.stockMovementRepository = stockMovementRepository;
        this.inventoryItemService = inventoryItemService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> findAllStockMovements(Pageable pageable) {

        return handleError(() -> stockMovementRepository.findAll(pageable),
                "Erro ao tentar buscar todas as movimentações do estoque: ")
                .map(stockMovement -> converter
                .toResponse(stockMovement, StockMovementResponse.class));
    }

    @Transactional(readOnly = false)
    public StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest) {

        stockMovementRequest.setId(null);
        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        handleError(() -> stockMovementRepository.save(stockMovement),
                "Erro ao tentar criar a movimentação do estoque: {}");
        logger.info("Movimentação do estoque criada: {}", stockMovement);

        return converter.toResponse(stockMovement, StockMovementResponse.class);
    }

    @Transactional(readOnly = true)
    public StockMovementResponse findStockMovementResponseById(Long id) {

        return handleError(() -> stockMovementRepository.findById(id),
                "Erro ao tentar buscar a movimentação do estoque pelo id: " + id)
                .map(stockMovement -> converter.toResponse(stockMovement, StockMovementResponse.class))
                .orElseThrow(() -> new NotFoundException("Movimentação do estoque não encontrado com o ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest) {

        StockMovement stockMovement = buildStockMovement(stockMovementRequest);

        handleError(() -> stockMovementRepository.save(stockMovement),
                "Erro ao tentar atualizar a movimentação do estoque: {}");
        logger.info("Movimentação do estoque atualizada: {}", stockMovement);

        return converter.toResponse(stockMovement, StockMovementResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteStockMovement(Long id) {

        findStockMovementById(id);

        handleError(() -> {
            stockMovementRepository.deleteById(id);
            return null;
        }, "Erro ao tentar excluir a movimentação do estoque: ");
        logger.info("Movimentação do estoque removida: {}", id);
    }

    public StockMovement findStockMovementById(Long id) {

        return handleError(() -> stockMovementRepository.findById(id),
                "Erro ao tentar buscar movimentação do estoque pelo id: ")
                .orElseThrow(() -> new NotFoundException("Movimentação do estoque não encontrado com o ID: " + id + "."));
    }

    public void saveStockMovement(StockMovement stockMovement) {

        handleError(() -> stockMovementRepository.save(stockMovement),
                "Erro ao tentar salvar a movimentação do estoque: ");
        logger.info("Movimentação do estoque salva: {}", stockMovement);
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

        Long id = stockMovementRequest.getId();
        InventoryItem inventoryItem = inventoryItemService.findInventoryItemById(id);
        Instant moment = Instant.now();
        MovementType movementType = stockMovementRequest.getMovementType();
        int quantityMoved = stockMovementRequest.getQuantityMoved();

        return new StockMovement(id, moment, inventoryItem, movementType, quantityMoved);
    }
}
