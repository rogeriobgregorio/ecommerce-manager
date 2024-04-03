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
import java.util.Set;

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
                "Erro ao tentar buscar todos os itens do pedido: ")
                .map(orderItem -> converter.toResponse(orderItem, OrderItemResponse.class));
    }

    @Transactional(readOnly = true)
    public OrderItemResponse findOrderItemById(Long orderId, Long itemId) {

        OrderItemPK id = buildOrderItemPK(orderId, itemId);

        return handleError(() -> orderItemRepository.findById(id),
                "Erro ao tentar buscar o item do pedido pelo id")
                .map(orderItem -> converter.toResponse(orderItem, OrderItemResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Itens não encontrado com o ID: {}", id);
                    return new NotFoundException("Itens não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        handleError(() -> orderItemRepository.save(orderItem),
                "Erro ao tentar criar item do pedido: ");

        logger.info("Item do pedido criado: {}", orderItem);
        return converter.toResponse(orderItem, OrderItemResponse.class);
    }

    @Transactional(readOnly = false)
    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest) {

        OrderItem orderItem = buildOrderItem(orderItemRequest);

        handleError(() -> orderItemRepository.save(orderItem),
                "Erro ao tentar atualizar o item do pedido: ");

        logger.info("Item do pedido atualizado: {}", orderItem);
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
        }, "Erro ao tentar excluir item do pedido: {}");

        logger.warn("Item do pedido removido: {}", id.getProduct());
    }

    public void validateOrderChangeEligibility(Order order) {

        String orderStatus = order.getOrderStatus().name();
        boolean isOrderPaid = Set.of("PAID", "SHIPPED", "DELIVERED").contains(orderStatus);

        if (isOrderPaid) {
            throw new IllegalStateException("Não é possível alterar a lista de itens: o pedido já foi pago.");
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
