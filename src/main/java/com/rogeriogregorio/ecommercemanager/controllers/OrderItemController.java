package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.services.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/order-items")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.findAllOrderItems(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> postOrderItem(
            @Valid @RequestBody OrderItemRequest orderItemRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderItemService.createOrderItem(orderItemRequest));
    }

    @GetMapping(value = "/{orderId}/{itemId}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(
            @PathVariable Long orderId, @PathVariable Long itemId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.findOrderItemById(orderId, itemId));
    }

    @PutMapping
    public ResponseEntity<OrderItemResponse> putOrderItem(
            @Valid @RequestBody OrderItemRequest orderItemRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.updateOrderItem(orderItemRequest));
    }

    @DeleteMapping(value = "/{orderId}/{itemId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Long orderId, @PathVariable Long itemId) {

        orderItemService.deleteOrderItem(orderId, itemId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
