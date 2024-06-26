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
    private static Payment payment;
    private static OrderRequest orderRequest;
    private static OrderResponse orderResponse;
    private static DiscountCoupon discountCoupon;

    @BeforeEach
    void setUp() {

        discountCoupon = new DiscountCoupon(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-01T00:00:00Z"),
                Instant.parse("2024-06-07T00:00:00Z"));

        payment = Payment.newBuilder()
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
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> orderRepository.findAll(pageable));

        // Act
        Page<OrderResponse> actualResponses = orderService.findAllOrders(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponses, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(order, OrderResponse.class);
        verify(orderRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("findAllOrders - Exceção ao tentar buscar lista de pedidos")
    void findAllOrders_RepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.findAllOrders(), "Expected RepositoryException to be thrown");

        verify(orderRepository, times(1)).findAll();
    }

//    @Test
//    @DisplayName("createOrder - Criação bem-sucedida retorna pedido criado")
//    void createOrder_SuccessfulCreation_ReturnsOrderResponse() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        OrderRequest orderRequest = new OrderRequest(1L);
//        Order order = new Order(Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//        OrderResponse expectedResponse = new OrderResponse(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//
//        when(userService.getUserIfExists(orderRequest.getClientId())).thenReturn(user);
//        when(converter.toResponse(order, OrderResponse.class)).thenReturn(expectedResponse);
//        when(orderRepository.save(order)).thenReturn(order);
//
//        // Act
//        OrderResponse actualResponse = orderService.createOrder(orderRequest);
//
//        // Assert
//        assertNotNull(actualResponse, "OrderResponse should not be null");
//        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
//
//        verify(userService, times(1)).getUserIfExists(orderRequest.getClientId());
//        verify(converter, times(1)).toResponse(order, OrderResponse.class);
//        verify(orderRepository, times(1)).save(order);
//    }
//
//    @Test
//    @DisplayName("createOrder - Exceção no repositório ao tentar criar pedido")
//    void createOrder_RepositoryExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        OrderRequest orderRequest = new OrderRequest(1L);
//        Order order = new Order(Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//
//        when(userService.getUserIfExists(orderRequest.getClientId())).thenReturn(user);
//        when(orderRepository.save(order)).thenThrow(PersistenceException.class);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> orderService.createOrder(orderRequest), "Expected RepositoryException due to a generic runtime exception");
//
//        verify(orderRepository, times(1)).save(order);
//    }
//
//    @Test
//    @DisplayName("findOrderById - Busca bem-sucedida retorna pedido")
//    void findOrderById_SuccessfulSearch_ReturnsOrderResponse() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, user);
//        OrderResponse expectedResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, user);
//
//        when(converter.toResponse(order, OrderResponse.class)).thenReturn(expectedResponse);
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        // Act
//        OrderResponse actualResponse = orderService.findOrderById(1L);
//
//        // Assert
//        assertNotNull(actualResponse, "OrderResponse should not be null");
//        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
//
//        verify(converter, times(1)).toResponse(order, OrderResponse.class);
//        verify(orderRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("findOrderById - Exceção ao tentar buscar pedido inexistente")
//    void findOrderById_NotFoundExceptionHandling() {
//        // Arrange
//        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> orderService.findOrderById(1L), "Expected NotFoundException for non-existent order");
//
//        verify(orderRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("updateOrder - Atualização bem-sucedida retorna pedido atualizado")
//    void updateOrder_SuccessfulUpdate_ReturnsOrderResponse() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID, 1L);
//        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, user);
//        OrderResponse expectedResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, user);
//
//        when(orderRepository.findById(orderRequest.getId())).thenReturn(Optional.of(order));
//        when(orderRepository.save(order)).thenReturn(order);
//        when(converter.toResponse(order, OrderResponse.class)).thenReturn(expectedResponse);
//
//        // Act
//        OrderResponse actualResponse = orderService.updateOrder(orderRequest);
//
//        // Assert
//        assertNotNull(actualResponse, "OrderResponse should not be null");
//        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
//        assertEquals(expectedResponse.getMoment(), actualResponse.getMoment(), "Moments should match");
//        assertEquals(expectedResponse.getOrderStatus(), actualResponse.getOrderStatus(), "OrderStatus should match");
//        assertEquals(expectedResponse.getClient(), actualResponse.getClient(), "Clients should match");
//
//        verify(orderRepository, times(1)).findById(orderRequest.getId());
//        verify(orderRepository, times(1)).save(order);
//        verify(converter, times(1)).toResponse(order, OrderResponse.class);
//    }
//
//    @Test
//    @DisplayName("updateOrder - Exceção ao tentar atualizar pedido inexistente")
//    void updateOrder_NotFoundExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID, 1L);
//        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, user);
//
//        when(orderRepository.findById(orderRequest.getId())).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> orderService.updateOrder(orderRequest), "Expected NotFoundException for non-existent order");
//
//        verify(orderRepository, times(1)).findById(order.getId());
//    }
//
//    @Test
//    @DisplayName("updateOrder - Exceção no repositório ao tentar atualizar pedido")
//    void updateOrder_RepositoryExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID, 1L);
//        Order order = new Order(1L, Instant.now(), OrderStatus.PAID, user);
//
//        when(orderRepository.findById(orderRequest.getId())).thenReturn(Optional.of(order));
//        when(orderRepository.save(order)).thenThrow(PersistenceException.class);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> orderService.updateOrder(orderRequest), "Expected RepositoryException for update failure");
//
//        verify(orderRepository, times(1)).findById(order.getId());
//        verify(orderRepository, times(1)).save(order);
//    }
//
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
