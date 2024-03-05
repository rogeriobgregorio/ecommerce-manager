package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderItemService {

    List<OrderItemResponse> findAllOrderItems();

    OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest);

    OrderItemResponse findOrderItemById(Long orderId, Long productId);

    OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest);

    void deleteOrderItem(Long orderId, Long productId);
}
