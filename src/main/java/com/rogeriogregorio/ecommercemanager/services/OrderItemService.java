package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface OrderItemService {

    Page<OrderItemResponse> findAllOrderItems(Pageable pageable);

    OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest);

    OrderItemResponse findOrderItemById(Long orderId, Long productId);

    OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest);

    void deleteOrderItem(Long orderId, Long productId);
}
