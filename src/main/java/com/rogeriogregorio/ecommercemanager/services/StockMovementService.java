package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.StockMovementEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StockMovementService {

    List<StockMovementResponse> findAllStockMovement();

    StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest);

    StockMovementResponse findStockMovementById(Long id);

    StockMovementEntity findStockMovementEntityById(Long id);

    StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest);

    void deleteStockMovement(Long id);

    StockMovementEntity buildStockMovementFromRequest(StockMovementRequest stockMovementRequest);
}
