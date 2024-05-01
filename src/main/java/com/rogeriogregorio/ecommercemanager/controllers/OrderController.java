package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping(value = "/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findAllOrders(pageable).getContent());
    }

    @PostMapping(value = "/orders")
    public ResponseEntity<OrderResponse> postOrder(
            @Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequest));
    }

    @GetMapping(value = "/orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findOrderResponseById(id));
    }

    @PutMapping(value = "/orders")
    public ResponseEntity<OrderResponse> putOrder(
            @Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrder(orderRequest));
    }

    @PatchMapping(value = "/orders/status")
    public ResponseEntity<OrderResponse> patchOrderStatus(
            @Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(orderRequest));
    }

    @DeleteMapping(value = "/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(value = "/clients/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getOrdersByClientId(
            @PathVariable Long id, Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findOrderByClientId(id, pageable).getContent());
    }
}
