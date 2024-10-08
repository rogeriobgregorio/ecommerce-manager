package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.OrderItemPK;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.OrderItemServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeFunction;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeProcedure;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private InventoryItemService inventoryItemService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private static Order order;
    private static Product product;
    private static OrderItem orderItem;
    private static OrderItemRequest orderItemRequest;
    private static OrderItemResponse orderItemResponse;

    @BeforeEach
    void setUp() {

        DiscountCoupon discountCoupon = new DiscountCoupon(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        Payment payment = Payment.newBuilder()
                .withId(1L)
                .withMoment(Instant.now())
                .withOrder(order)
                .withPaymentStatus(PaymentStatus.PROCESSING)
                .withPaymentType(PaymentType.PIX)
                .withTxId("b3f1b57e-ec0c-4b23-a6b2-647d2b176d74")
                .withChargeLink("https://bank.com/paymentqrcode")
                .build();

        Category category = new Category(1L, "Computers");
        Set<Category> categoryList = new HashSet<>();
        categoryList.add(category);

        ProductDiscount productDiscount = new ProductDiscount(1L,
                "Dia das Mães", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-01T00:00:00Z"),
                Instant.parse("2024-06-07T00:00:00Z"));

        product = Product.newBuilder()
                .withId(1L).withName("Intel i5-10400F")
                .withDescription("Intel Core Processor")
                .withPrice(BigDecimal.valueOf(579.99))
                .withCategories(categoryList)
                .withImgUrl("https://example.com/i5-10400F.jpg")
                .withProductDiscount(productDiscount)
                .build();

        User user = User.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withName("Admin")
                .withEmail("admin@email.com")
                .withPhone("11912345678")
                .withCpf("72482581052")
                .withPassword("Password123$")
                .withRole(UserRole.ADMIN)
                .build();

        order = Order.newBuilder()
                .withId(1L)
                .withClient(user)
                .withMoment(Instant.now())
                .withCoupon(discountCoupon)
                .withOrderStatus(OrderStatus.WAITING_PAYMENT)
                .withPayment(payment)
                .build();

        orderItem = OrderItem.newBuilder()
                .withOrder(order)
                .withProduct(product)
                .withPrice(BigDecimal.valueOf(579.99))
                .withQuantity(1)
                .build();

        orderItemRequest = new OrderItemRequest(1L, 1L, 1);

        orderItemResponse = new OrderItemResponse(order, product, 1, BigDecimal.valueOf(579.99));

        MockitoAnnotations.openMocks(this);
        orderItemService = new OrderItemServiceImpl(orderItemRepository, inventoryItemService,
                productService, orderService, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllOrderItems - Busca bem-sucedida retorna lista de itens de pedido")
    void findAllOrderItems_SuccessfulSearch_ReturnsOrderItemList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderItem> orderItemList = Collections.singletonList(orderItem);
        List<OrderItemResponse> expectedResponses = Collections.singletonList(orderItemResponse);
        PageImpl<OrderItem> page = new PageImpl<>(orderItemList, pageable, orderItemList.size());

        when(dataMapper.map(orderItem, OrderItemResponse.class)).thenReturn(orderItemResponse);
        when(orderItemRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.findAll(pageable));

        // Act
        Page<OrderItemResponse> actualResponses = orderItemService.findAllOrderItems(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponses, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(orderItem, OrderItemResponse.class);
        verify(orderItemRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllOrderItems - Exceção no repositório ao tentar buscar lista de itens de pedidos")
    void findAllOrderItems_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(orderItemRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderItemService.findAllOrderItems(pageable),
                "Expected PersistenceException to be thrown");
        verify(orderItemRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createOrderItem - Criação bem-sucedida retorna item do pedido criado")
    void createOrderItem_SuccessfulCreation_ReturnsOrderItem() {
        // Arrange
        OrderItemResponse expectedResponse = new OrderItemResponse(order, product, orderItemRequest.getQuantity(), product.getPrice());

        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        when(dataMapper.map(orderItem, OrderItemResponse.class)).thenReturn(expectedResponse);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.save(orderItem));

        // Act
        OrderItemResponse actualResponse = orderItemService.createOrderItem(orderItemRequest);

        // Assert
        assertNotNull(actualResponse, "OrderItemResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).save(orderItem);
        verify(dataMapper, times(1)).map(orderItem, OrderItemResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createOrderItem - Exceção no repositório ao tentar criar item do pedido")
    void createOrderItem_RepositoryExceptionHandling() {
        // Arrange
        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        when(orderItemRepository.save(orderItem)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.save(orderItem));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderItemService.createOrderItem(orderItemRequest));

        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).save(orderItem);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findOrderItemById - Busca bem-sucedida retorna item do pedido")
    void findOrderItemById_SuccessfulSearch_ReturnsOrderItem() {
        // Arrange
        OrderItemResponse expectedResponse = orderItemResponse;

        OrderItemPK id = new OrderItemPK();
        id.setOrder(order);
        id.setProduct(product);

        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        when(dataMapper.map(orderItem, OrderItemResponse.class)).thenReturn(expectedResponse);
        when(orderItemRepository.findById(id)).thenReturn(Optional.of(orderItem));
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.findById(id));

        // Act
        OrderItemResponse actualResponse = orderItemService.findOrderItemById(1L, 1L);

        // Assert
        assertNotNull(actualResponse, "Order item should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).findById(id);
        verify(dataMapper, times(1)).map(orderItem, OrderItemResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findOrderItemById - Exceção ao tentar buscar item do pedido inexistente")
    void findOrderItemById_NotFoundExceptionHandling() {
        // Arrange
        OrderItemPK id = new OrderItemPK();
        id.setOrder(order);
        id.setProduct(product);

        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        when(orderItemRepository.findById(id)).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.findById(id));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderItemService.findOrderItemById(1L, 1L),
                "Expected NotFoundException to be thrown");
        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).findById(id);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateOrderItem - Atualização bem-sucedida retorna item do pedido atualizado")
    void updateOrderItem_SuccessfulUpdate_ReturnsOrderItem() {
        // Arrange
        OrderItemResponse expectedResponse = orderItemResponse;

        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(dataMapper.map(orderItem, OrderItemResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.save(orderItem));

        // Act
        OrderItemResponse actualResponse = orderItemService.updateOrderItem(orderItemRequest);

        // Assert
        assertNotNull(actualResponse, "Order item should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).save(orderItem);
        verify(dataMapper, times(1)).map(orderItem, OrderItemResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateOrderItemById - Exceção ao tentar atualizar item do pedido inexistente")
    void updateOrderItemById_NotFoundExceptionHandling() {
        // Arrange
        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenThrow(NotFoundException.class);

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderItemService.updateOrderItem(orderItemRequest),
                "Expected NotFoundException to be thrown");
        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
    }

    @Test
    @DisplayName("updateOrderItemById - Exceção ao tentar atualizar item do pedido")
    void updateOrderItemById_RepositoryExceptionHandling() {
        // Arrange
        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        when(orderItemRepository.save(orderItem)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderItemRepository.save(orderItem));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderItemService.updateOrderItem(orderItemRequest),
                "Expected RepositoryException to be thrown");
        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).save(orderItem);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteOrderItem - Exclusão bem-sucedida do item do pedido")
    void deleteOrderItem_DeletesOrderItemSuccessfully() {
        // Arrange
        OrderItemPK id = new OrderItemPK();
        id.setOrder(order);
        id.setProduct(product);

        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        doAnswer(invocation -> {
            orderItemRepository.deleteById(id);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));

        // Act
        orderItemService.deleteOrderItem(1L, 1L);

        // Assert
        verify(orderService, times(2)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).deleteById(id);
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteOrderItem - Exceção ao tentar excluir item do pedido inexistente")
    void deleteOrderItem_NotFoundExceptionHandling() {
        // Arrange
        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenThrow(NotFoundException.class);

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderItemService.deleteOrderItem(1L, 1L),
                "Expected NotFoundException to be thrown");
        verify(orderService, times(1)).getOrderIfExists(orderItemRequest.getOrderId());
    }

    @Test
    @DisplayName("deleteOrderItem - Exceção ao tentar excluir item do pedido")
    void deleteOrderItem_RepositoryExceptionHandling() {
        // Arrange
        OrderItemPK id = new OrderItemPK();
        id.setOrder(order);
        id.setProduct(product);

        when(orderService.getOrderIfExists(orderItemRequest.getOrderId())).thenReturn(order);
        when(productService.getProductIfExists(orderItemRequest.getProductId())).thenReturn(product);
        doThrow(RepositoryException.class).when(orderItemRepository).deleteById(id);
        doAnswer(invocation -> {
            orderItemRepository.deleteById(id);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderItemService.deleteOrderItem(1L, 1L),
                "Expected RepositoryException to be thrown");
        verify(orderService, times(2)).getOrderIfExists(orderItemRequest.getOrderId());
        verify(productService, times(1)).getProductIfExists(orderItemRequest.getProductId());
        verify(orderItemRepository, times(1)).deleteById(id);
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }
}
