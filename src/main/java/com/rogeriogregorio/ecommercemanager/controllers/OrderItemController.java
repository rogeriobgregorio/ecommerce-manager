package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.dto.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
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

    @GetMapping(value = "/order-itens")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItens() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.findAllOrderItem());
    }

    @PostMapping(value = "/order-itens")
    public ResponseEntity<OrderItemResponse> createOrderItem(@Valid @RequestBody OrderItemRequest orderItemRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderItemService.createOrderItem(orderItemRequest));
    }

    @GetMapping(value = "/order-itens/{id}")
    public ResponseEntity<OrderItemResponse> getOrderItemById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.findOrderItemById(id));
    }

    @PutMapping(value = "/order-itens")
    public ResponseEntity<OrderItemResponse> updateOrderItem(@Valid @RequestBody OrderItemRequest orderItemRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.updateOrderItem(orderItemRequest));
    }

    @DeleteMapping(value = "/order-itens/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {

        orderItemService.deleteOrderItem(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
