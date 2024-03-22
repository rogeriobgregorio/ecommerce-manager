package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface InventoryItemService {

    List<InventoryItemResponse> findAllInventoryItems();

    InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest);

    InventoryItemResponse findInventoryItemById(Long id);

    InventoryItemEntity findInventoryItemEntityById(Long id);

    InventoryItemResponse updateInventoryItem(InventoryItemRequest inventoryItemRequest);

    void deleteInventoryItem(Long id);

    InventoryItemEntity buildAddressFromRequest(InventoryItemRequest inventoryItemRequest);
}
