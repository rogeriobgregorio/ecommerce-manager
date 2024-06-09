package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.OrderItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.OrderItemPK;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderItemService;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final InventoryItemService inventoryItemService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private static final Logger logger = LogManager.getLogger(OrderItemServiceImpl.class);

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                                InventoryItemService inventoryItemService,
                                ProductService productService,
                                OrderService orderService,
                                ErrorHandler errorHandler,
                                DataMapper dataMapper) {

        this.orderItemRepository = orderItemRepository;
        this.inventoryItemService = inventoryItemService;
        this.productService = productService;
        this.orderService = orderService;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<OrderItemResponse> findAllOrderItems(Pageable pageable) {

        return errorHandler.catchException(
                () -> orderItemRepository.findAll(pageable)
                        .map(orderItem -> dataMapper.map(orderItem, OrderItemResponse.class)),
                "Error while trying to fetch all items of the order: "
        );
    }

    @Transactional
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        OrderItem savedOrderItem = errorHandler.catchException(
                () -> orderItemRepository.save(orderItem),
                "Error while trying to create order item: "
        );

        logger.info("Order item created: {}", savedOrderItem);
        return dataMapper.map(savedOrderItem, OrderItemResponse.class);
    }

    @Transactional(readOnly = true)
    public OrderItemResponse findOrderItemById(Long orderId, Long itemId) {

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        return errorHandler.catchException(
                () -> orderItemRepository.findById(id)
                        .map(orderItem -> dataMapper.map(orderItem, OrderItemResponse.class))
                        .orElseThrow(() -> new NotFoundException("Item not found with ID: " + id + ".")),
                "Error while trying to fetch the order item by ID: "
        );
    }

    @Transactional
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        OrderItem updatedOrderItem = errorHandler.catchException(
                () -> orderItemRepository.save(orderItem),
                "Error while trying to update the order item: "
        );

        logger.info("Order item updated: {}", updatedOrderItem);
        return dataMapper.map(updatedOrderItem, OrderItemResponse.class);
    }

    @Transactional
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

        Order order = orderService.getOrderIfExists(orderId);

        if (order.isOrderPaid()) {
            throw new IllegalStateException("You cannot change items on a paid order.");
        }

        return order;
    }

    private OrderItemPK buildOrderItemPK(Long orderId, Long itemId) {

        Order order = orderService.getOrderIfExists(orderId);
        Product product = productService.getProductIfExists(itemId);

        OrderItemPK id = new OrderItemPK();
        id.setOrder(order);
        id.setProduct(product);

        return id;
    }

    private OrderItem buildOrderItem(OrderItemRequest orderItemRequest) {

        Order order = validateOrderChangeEligibility(orderItemRequest.getOrderId());
        Product product = productService.getProductIfExists(orderItemRequest.getProductId());

        OrderItem orderItem = OrderItem.newBuilder()
                .withOrder(order)
                .withProduct(product)
                .withQuantity(orderItemRequest.getQuantity())
                .withPrice(product.getPriceFinal())
                .build();

        return inventoryItemService.validateItemAvailability(orderItem);
    }
}
