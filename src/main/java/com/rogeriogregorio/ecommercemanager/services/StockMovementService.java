package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface StockMovementService {

    Page<StockMovementResponse> findAllStockMovements(int page, int size);

    StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest);

    StockMovementResponse findStockMovementResponseById(Long id);

    StockMovement findStockMovementById(Long id);

    StockMovementResponse updateStockMovement(StockMovementRequest stockMovementRequest);

    void deleteStockMovement(Long id);

    void updateStockMovementExit(Order order);
}
