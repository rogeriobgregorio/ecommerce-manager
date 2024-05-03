package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.OrderItemPK;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.*;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandler;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final InventoryItemService inventoryItemService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ErrorHandler errorHandler;
    private final Converter converter;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                                InventoryItemService inventoryItemService,
                                ProductService productService,
                                OrderService orderService,
                                ErrorHandler errorHandler, Converter converter) {

        this.orderItemRepository = orderItemRepository;
        this.inventoryItemService = inventoryItemService;
        this.productService = productService;
        this.orderService = orderService;
        this.errorHandler = errorHandler;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<OrderItemResponse> findAllOrderItems(Pageable pageable) {

        return errorHandler.catchException(() -> orderItemRepository.findAll(pageable),
                "Error while trying to fetch all items of the order: ")
                .map(orderItem -> converter.toResponse(orderItem, OrderItemResponse.class));
    }

    @Transactional(readOnly = true)
    public OrderItemResponse findOrderItemById(Long orderId, Long itemId) {

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        return errorHandler.catchException(() -> orderItemRepository.findById(id),
                "Error while trying to fetch the order item by ID: ")
                .map(orderItem -> converter.toResponse(orderItem, OrderItemResponse.class))
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        errorHandler.catchException(() -> orderItemRepository.save(orderItem),
                "Error while trying to create order item: ");
        logger.info("Order item created: {}", orderItem);

        return converter.toResponse(orderItem, OrderItemResponse.class);
    }

    @Transactional(readOnly = false)
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        errorHandler.catchException(() -> orderItemRepository.save(orderItem),
                "Error while trying to update the order item: ");
        logger.info("Order item updated: {}", orderItem);

        return converter.toResponse(orderItem, OrderItemResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteOrderItem(Long orderId, Long itemId) {

        validateOrderChangeEligibility(orderId);

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        errorHandler.catchException(() -> {
            orderItemRepository.deleteById(id);
            return null;
        }, "Error while trying to delete order item: ");
        logger.warn("Order item removed: {}", id.getProduct());
    }

    private Order validateOrderChangeEligibility(Long orderId) {

        Order order = orderService.findOrderById(orderId);

        if (order.isOrderPaid()) {
            throw new IllegalStateException("It's not possible to modify the list of items: the order has already been paid for.");
        }

        return order;
    }

    private OrderItemPK buildOrderItemPK(Long orderId, Long itemId) {

        Order order = orderService.findOrderById(orderId);
        Product product = productService.findProductById(itemId);

        OrderItemPK id = new OrderItemPK();
        id.setOrder(order);
        id.setProduct(product);

        return id;
    }

    private OrderItem buildOrderItem(OrderItemRequest orderItemRequest) {

        Order order = validateOrderChangeEligibility(orderItemRequest.getOrderId());
        Product product = productService.findProductById(orderItemRequest.getProductId());
        int quantity = orderItemRequest.getQuantity();
        BigDecimal price = product.isDiscountPresent() ? product.getPriceWithDiscount() : product.getPrice();
        OrderItem orderItem = new OrderItem(order, product, quantity, price);

        inventoryItemService.isItemAvailable(orderItem);

        return orderItem;
    }
}
