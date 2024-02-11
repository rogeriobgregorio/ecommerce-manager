package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {

        this.orderService = orderService;
    }

    @GetMapping(value = "/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findAllOrders());
    }

    @PostMapping(value = "/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequest));
    }

    @GetMapping(value = "/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findOrderById(id));
    }

    @PutMapping(value = "/orders")
    public ResponseEntity<OrderResponse> updateOrder(@Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrder(orderRequest));
    }

    @DeleteMapping(value = "/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(value = "/clients/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByClientId(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findOrderByClientId(id));
    }
}
