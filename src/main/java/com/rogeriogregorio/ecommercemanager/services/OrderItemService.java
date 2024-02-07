package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.OrderItemResponse;

import java.util.List;

public interface OrderItemService {

    public List<OrderItemResponse> findAllOrderItem();

    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest);

    public OrderItemResponse findOrderItemById(Long id);

    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest);

    public void deleteOrderItem(Long id);
}
