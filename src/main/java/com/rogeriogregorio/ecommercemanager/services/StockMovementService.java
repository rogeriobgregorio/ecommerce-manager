package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface StockMovementService {

    Page<StockMovementResponse> findAllStockMovements(Pageable pageable);

    StockMovementResponse createStockMovement(StockMovementRequest stockMovementRequest);

    StockMovementResponse findStockMovementById(Long id);

    StockMovementResponse updateStockMovement(Long id, StockMovementRequest stockMovementRequest);

    void deleteStockMovement(Long id);

    void updateStockMovementExit(Order order);
}
