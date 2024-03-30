package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItem;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface InventoryItemService {

    List<InventoryItemResponse> findAllInventoryItems();

    InventoryItemResponse createInventoryItem(InventoryItemRequest inventoryItemRequest);

    InventoryItemResponse findInventoryItemResponseById(Long id);

    InventoryItem findInventoryItemById(Long id);

    InventoryItemResponse updateInventoryItem(InventoryItemRequest inventoryItemRequest);

    void deleteInventoryItem(Long id);

    InventoryItem findInventoryItemByProduct(Product product);

    InventoryItem buildInventoryItem(InventoryItemRequest inventoryItemRequest);

    boolean isListItemsAvailable(Order order);

    void updateInventoryItemQuantity(Order order);

    boolean isItemAvailable(OrderItem orderItem);
}
