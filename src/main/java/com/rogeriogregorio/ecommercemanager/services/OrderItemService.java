package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderItemService {

    List<OrderItemResponse> findAllOrderItems();

    OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest);

    OrderItemResponse findOrderItemById(Long orderId, Long productId);

    OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest);

    void deleteOrderItem(Long orderId, Long productId);

    OrderItemPK buildOrderItemPK(Long orderId, Long itemId);

    OrderItemEntity buildOrderItemFromRequest(OrderItemRequest orderItemRequest);
}
