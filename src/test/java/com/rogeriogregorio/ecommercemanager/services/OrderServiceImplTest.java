package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.OrderServiceImpl;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStatusStrategy;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeFunction;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeProcedure;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import jakarta.persistence.PersistenceException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private DiscountCouponService discountCouponService;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @Mock
    private List<OrderStatusStrategy> validators;

    @InjectMocks
    private OrderServiceImpl orderService;

    private static User user;
    private static Order order;
    private static DiscountCoupon discountCoupon;
    private static OrderRequest orderRequest;
    private static OrderResponse orderResponse;

    @BeforeEach
    void setUp() {

        discountCoupon = new DiscountCoupon(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        Payment payment = Payment.newBuilder()
                .withId(1L)
                .withMoment(Instant.now())
                .withOrder(order)
                .withPaymentStatus(PaymentStatus.CONCLUDED)
                .withPaymentType(PaymentType.PIX)
                .withTxId("b3f1b57e-ec0c-4b23-a6b2-647d2b176d74")
                .withChargeLink("https://bank.com/paymentqrcode")
                .build();

        user = User.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withName("Admin").withEmail("admin@email.com").withPhone("11912345678")
                .withCpf("72482581052").withPassword("Password123$").withRole(UserRole.ADMIN)
                .build();

        order = Order.newBuilder()
                .withId(1L)
                .withClient(user)
                .withMoment(Instant.now())
                .withCoupon(discountCoupon)
                .withOrderStatus(OrderStatus.DELIVERED)
                .withPayment(payment)
                .build();

        orderRequest = new OrderRequest(OrderStatus.DELIVERED, user.getId(), "PROMO70OFF");

        orderResponse = new OrderResponse(1L, Instant.now(), OrderStatus.DELIVERED, user, discountCoupon);

        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderRepository, userService,
                discountCouponService, validators, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllOrders - Busca bem-sucedida retorna lista de pedidos")
    void findAllOrders_SuccessfulSearch_ReturnsOrderList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = Collections.singletonList(order);
        List<OrderResponse> expectedResponses = Collections.singletonList(orderResponse);
        PageImpl<Order> page = new PageImpl<>(orderList, pageable, orderList.size());

        when(dataMapper.map(order, OrderResponse.class)).thenReturn(orderResponse);
        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.findAll(pageable));

        // Act
        Page<OrderResponse> actualResponses = orderService.findAllOrders(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponses, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(order, OrderResponse.class);
        verify(orderRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllOrders - Exceção no repositório ao tentar buscar lista de pedidos")
    void findAllOrders_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findAll(pageable)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.findAll(pageable));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.findAllOrders(pageable),
                "Expected RepositoryException to be thrown");
        verify(orderRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createOrder - Criação bem-sucedida retorna pedido criado")
    void createOrder_SuccessfulCreation_ReturnsOrder() {
        // Arrange
        OrderResponse expectedResponse = orderResponse;

        when(userService.getUserIfExists(orderRequest.getClientId())).thenReturn(user);
        when(dataMapper.map(order, OrderResponse.class)).thenReturn(expectedResponse);
        when(orderRepository.save(order)).thenReturn(order);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.save(order));

        // Act
        OrderResponse actualResponse = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(actualResponse, "Order should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(userService, times(1)).getUserIfExists(orderRequest.getClientId());
        verify(dataMapper, times(1)).map(order, OrderResponse.class);
        verify(orderRepository, times(1)).save(order);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createOrder - Exceção no repositório ao tentar criar pedido")
    void createOrder_RepositoryExceptionHandling() {
        // Arrange
        when(userService.getUserIfExists(orderRequest.getClientId())).thenReturn(user);
        when(orderRepository.save(order)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.save(order));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.createOrder(orderRequest),
                "Expected RepositoryException to be thrown");
        verify(userService, times(1)).getUserIfExists(orderRequest.getClientId());
        verify(orderRepository, times(1)).save(order);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findOrderById - Busca bem-sucedida retorna pedido")
    void findOrderById_SuccessfulSearch_ReturnsOrder() {
        // Arrange
        OrderResponse expectedResponse = orderResponse;

        when(dataMapper.map(order, OrderResponse.class)).thenReturn(expectedResponse);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.findById(order.getId()));

        // Act
        OrderResponse actualResponse = orderService.findOrderById(order.getId());

        // Assert
        assertNotNull(actualResponse, "Order should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        verify(dataMapper, times(1)).map(order, OrderResponse.class);
        verify(orderRepository, times(1)).findById(order.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findOrderById - Exceção ao tentar buscar pedido inexistente")
    void findOrderById_NotFoundExceptionHandling() {
        // Arrange
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.findById(order.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderService.findOrderById(order.getId()),
                "Expected NotFoundException to be thrown");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findOrderById - Busca bem-sucedida retorna pedido")
    void findOrderById_RepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findById(order.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.findById(order.getId()));

        // Assert and Assert
        assertThrows(RepositoryException.class, () -> orderService.findOrderById(order.getId()),
                "Expected RepositoryException to be thrown");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateOrder - Atualização bem-sucedida retorna pedido atualizado")
    void updateOrder_SuccessfulUpdate_ReturnsOrder() {
        // Arrange
        OrderResponse expectedResponse = orderResponse;

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(dataMapper.map(order, OrderResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(discountCouponService.findDiscountCouponByCode(order.getCoupon().getCode())).thenReturn(discountCoupon);

        // Act
        OrderResponse actualResponse = orderService.updateOrder(order.getId(), orderRequest);

        // Assert
        assertNotNull(actualResponse, "Order should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        verify(dataMapper, times(1)).map(order, OrderResponse.class);
        verify(catchError, times(2)).run(any(SafeFunction.class));
        verify(discountCouponService, times(1)).findDiscountCouponByCode(order.getCoupon().getCode());
    }

    @Test
    @DisplayName("updateOrder - Exceção ao tentar atualizar pedido inexistente")
    void updateOrder_NotFoundExceptionHandling() {
        // Arrange
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.findById(order.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderService.updateOrder(order.getId(), orderRequest),
                "Expected NotFoundException to be thrown");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, never()).save(order);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateOrder - Exceção no repositório ao tentar atualizar pedido")
    void updateOrder_RepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(discountCouponService.findDiscountCouponByCode(order.getCoupon().getCode())).thenReturn(discountCoupon);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.updateOrder(order.getId(), orderRequest),
        "Expected RepositoryException to be thrown");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        verify(catchError, times(2)).run(any(SafeFunction.class));
        verify(discountCouponService, times(1)).findDiscountCouponByCode(order.getCoupon().getCode());
    }

    @Test
    @DisplayName("updateOrderStatus - Atualização de status do pedido bem-sucedida retorna pedido atualizado")
    void updateOrderStatus_SuccessfulUpdateOrderStatus_ReturnsOrder() {
        // Arrange
        OrderResponse expectedResponse = orderResponse;

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(dataMapper.map(order, OrderResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());

        // Act
        OrderResponse actualResponse = orderService.updateOrderStatus(order.getId(), orderRequest);

        // Assert
        assertNotNull(actualResponse, "Order should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        verify(dataMapper, times(1)).map(order, OrderResponse.class);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateOrderStatus - Exceção ao tentar atualizar o status do pedido inexistente")
    void updateOrderStatus_NotFoundExceptionHandling() {
        // Arrange
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> orderRepository.findById(order.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderService.updateOrderStatus(order.getId(), orderRequest),
                "Expected NotFoundException to be thrown");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, never()).save(order);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateOrderStatus - Exceção no repositório ao tentar atualizar o status do pedido")
    void updateOrderStatus_RepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.updateOrderStatus(order.getId(), orderRequest),
                "Expected RepositoryException to be thrown");
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1)).save(order);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

//    @Test
//    @DisplayName("deleteOrder - Exclusão bem-sucedida do pedido")
//    void deleteOrder_DeletesOrderSuccessfully() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        // Act
//        orderService.deleteOrder(1L);
//
//        // Assert
//        verify(orderRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("deleteOrder - Exceção ao tentar excluir pedido inexistente")
//    void deleteOrder_NotFoundExceptionHandling() {
//        // Arrange
//        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> orderService.deleteOrder(1L), "Expected NotFoundException for non-existent order");
//
//        verify(orderRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("deleteOrder - Exceção no repositório ao tentar excluir pedido")
//    void deleteOrder_RepositoryExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//        doThrow(PersistenceException.class).when(orderRepository).deleteById(1L);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> orderService.deleteOrder(1L), "Expected RepositoryException for delete failure");
//
//        verify(orderRepository, times(1)).findById(1L);
//        verify(orderRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("deleteOrder - Exceção ao tentar excluir pedido pago")
//    void deleteOrder_IllegalStateExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, user);
//
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        // Act and Assert
//        assertThrows(IllegalStateException.class, () -> orderService.deleteOrder(1L), "Expected IllegalException for delete failure");
//
//        verify(orderRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("findOrderByClientId - Busca bem-sucedida retorna lista contendo um pedido")
//    void findOrderByClientId_SuccessfulSearch_ReturnsOrderResponse_OneOrder() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, user);
//        List<Order> orderList = Collections.singletonList(order);
//
//        OrderResponse orderResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, user);
//        List<OrderResponse> expectedResponses = Collections.singletonList(orderResponse);
//
//        when(converter.toResponse(order, OrderResponse.class)).thenReturn(orderResponse);
//        when(orderRepository.findByClient_Id(1L)).thenReturn(Optional.of(orderList));
//
//        // Act
//        List<OrderResponse> actualResponses = orderService.findOrderByClientId(1L);
//
//        // Assert
//        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one order");
//        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one order");
//
//        verify(converter, times(1)).toResponse(order, OrderResponse.class);
//        verify(orderRepository, times(1)).findByClient_Id(1L);
//    }
//
//    @Test
//    @DisplayName("findOrderByClientId - Busca bem-sucedida retorna lista contendo múltiplos pedidos")
//    void findOrderByClientId_SuccessfulSearch_ReturnsListResponse_MultipleOrders() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        List<Order> orderList = new ArrayList<>();
//        List<OrderResponse> expectedResponses = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            Order order = new Order((long) i, Instant.now(), OrderStatus.PAID, user);
//            orderList.add(order);
//
//            OrderResponse orderResponse = new OrderResponse((long) i, Instant.now(), OrderStatus.PAID, user);
//            expectedResponses.add(orderResponse);
//
//            when(converter.toResponse(order, OrderResponse.class)).thenReturn(orderResponse);
//        }
//
//        when(orderRepository.findByClient_Id(1L)).thenReturn(Optional.of(orderList));
//
//        // Act
//        List<OrderResponse> actualResponses = orderService.findOrderByClientId(1L);
//
//        // Assert
//        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple orders");
//        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple orders");
//
//        verify(converter, times(10)).toResponse(any(Order.class), eq(OrderResponse.class));
//        verify(orderRepository, times(1)).findByClient_Id(1L);
//    }
//
//    @Test
//    @DisplayName("findOrderByClientId - Exceção ao retornar lista de pedidos vazia")
//    void findOrderByClientId_SuccessfulSearch_ReturnsEmptyList() {
//        // Arrange
//        when(orderRepository.findByClient_Id(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> orderService.findOrderByClientId(1L), "Expected NotFoundException to be thrown");
//
//        verify(orderRepository, times(1)).findByClient_Id(1L);
//    }
}
