package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.StockMovement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StockMovementService {

    List<StockMovementResponse> findAllStockMovements();

    StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest);

    StockMovementResponse findStockMovementResponseById(Long id);

    StockMovement findStockMovementById(Long id);

    StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest);

    void deleteStockMovement(Long id);
}
