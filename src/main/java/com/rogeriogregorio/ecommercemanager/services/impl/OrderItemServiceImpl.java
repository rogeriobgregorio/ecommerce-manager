package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderItemServiceImpl extends ErrorHandlerTemplateImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final InventoryItemService inventoryItemService;
    private final ProductService productService;
    private final OrderService orderService;
    private final Converter converter;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                                InventoryItemService inventoryItemService,
                                ProductService productService,
                                OrderService orderService,
                                Converter converter) {

        this.orderItemRepository = orderItemRepository;
        this.inventoryItemService = inventoryItemService;
        this.productService = productService;
        this.orderService = orderService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<OrderItemResponse> findAllOrderItems(Pageable pageable) {

        return handleError(() -> orderItemRepository.findAll(pageable),
                "Error while trying to fetch all items of the order: ")
                .map(orderItem -> converter.toResponse(orderItem, OrderItemResponse.class));
    }

    @Transactional(readOnly = true)
    public OrderItemResponse findOrderItemById(Long orderId, Long itemId) {

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        return handleError(() -> orderItemRepository.findById(id),
                "Error while trying to fetch the order item by ID: ")
                .map(orderItem -> converter.toResponse(orderItem, OrderItemResponse.class))
                .orElseThrow(() -> new NotFoundException("Item not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        handleError(() -> orderItemRepository.save(orderItem),
                "Error while trying to create order item: ");
        logger.info("Order item created: {}", orderItem);

        return converter.toResponse(orderItem, OrderItemResponse.class);
    }

    @Transactional(readOnly = false)
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        handleError(() -> orderItemRepository.save(orderItem),
                "Error while trying to update the order item: ");
        logger.info("Order item updated: {}", orderItem);

        return converter.toResponse(orderItem, OrderItemResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteOrderItem(Long orderId, Long itemId) {

        Order order = orderService.findOrderById(orderId);

        validateOrderChangeEligibility(order);

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        handleError(() -> {
            orderItemRepository.deleteById(id);
            return null;
        }, "Error while trying to delete order item: ");
        logger.warn("Order item removed: {}", id.getProduct());
    }

    public void validateOrderChangeEligibility(Order order) {

        boolean isOrderPaid = order.isOrderPaid();

        if (isOrderPaid) {
            throw new IllegalStateException("It's not possible to modify the list of items: the order has already been paid for.");
        }
    }

    public OrderItemPK buildOrderItemPK(Long orderId, Long itemId) {

        Order order = orderService.findOrderById(orderId);
        Product product = productService.findProductById(itemId);

        OrderItemPK id = new OrderItemPK();
        id.setOrder(order);
        id.setProduct(product);

        return id;
    }

    public OrderItem buildOrderItem(OrderItemRequest orderItemRequest) {

        Long orderId = orderItemRequest.getOrderId();
        Order order = orderService.findOrderById(orderId);

        validateOrderChangeEligibility(order);

        Long productId = orderItemRequest.getProductId();
        Product product = productService.findProductById(productId);
        int quantity = orderItemRequest.getQuantity();
        BigDecimal price = product.getPrice();
        OrderItem orderItem = new OrderItem(order, product, quantity, price);

        inventoryItemService.isItemAvailable(orderItem);

        return orderItem;
    }
}
