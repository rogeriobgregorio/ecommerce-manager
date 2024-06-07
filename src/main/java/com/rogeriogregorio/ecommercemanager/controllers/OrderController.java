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
@RequestMapping(value = "/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findAllOrders(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<OrderResponse> postOrder(
            @Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequest));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findOrderById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<OrderResponse> putOrder(@PathVariable Long id,
            @Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrder(id, orderRequest));
    }

    @PatchMapping(value = "/status/{id}")
    public ResponseEntity<OrderResponse> patchOrderStatus(@PathVariable Long id,
            @Valid @RequestBody OrderRequest orderRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(id, orderRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {

        orderService.deleteOrder(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(value = "/{client-id}")
    public ResponseEntity<List<OrderResponse>> getOrdersByClientId(
            @PathVariable Long id, Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.findOrderByClientId(id, pageable).getContent());
    }
}
