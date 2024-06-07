package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItem;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface InventoryItemService {

    Page<InventoryItemResponse> findAllInventoryItems(Pageable pageable);

    InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest);

    InventoryItemResponse findInventoryItemById(Long id);

    InventoryItem getInventoryItemIfExists(Long id);

    InventoryItemResponse updateInventoryItem(Long id, InventoryItemRequest inventoryItemRequest);

    void deleteInventoryItem(Long id);

    InventoryItem findInventoryItemByProduct(Product product);

    void validateItemListAvailability(Order order);

    void updateInventoryItemQuantity(Order order);

    OrderItem validateItemAvailability(OrderItem orderItem);
}
