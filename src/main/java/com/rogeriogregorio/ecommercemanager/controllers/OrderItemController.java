package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.services.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {

        this.orderItemService = orderItemService;
    }

    @GetMapping(value = "/order-items")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.findAllOrderItems());
    }

    @PostMapping(value = "/order-items")
    public ResponseEntity<OrderItemResponse> createOrderItem(@Valid @RequestBody OrderItemRequest orderItemRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderItemService.createOrderItem(orderItemRequest));
    }

    @GetMapping(value = "/order-items/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.findOrderItemById(id));
    }

    @PutMapping(value = "/order-items")
    public ResponseEntity<OrderItemResponse> updateOrderItem(@Valid @RequestBody OrderItemRequest orderItemRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.updateOrderItem(orderItemRequest));
    }

    @DeleteMapping(value = "/order-items/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {

        orderItemService.deleteOrderItem(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
