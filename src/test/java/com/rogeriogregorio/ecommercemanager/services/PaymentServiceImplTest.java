package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.PaymentServiceImpl;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InventoryItemService inventoryItemService;

    @Mock
    private StockMovementService stockMovementService;

    @Mock
    private OrderService orderService;

    @Mock
    private Converter converter;

    @Mock
    private List<PaymentStrategy> validators;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    PaymentServiceImplTest() {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentRepository, inventoryItemService, stockMovementService, orderService, discountCouponService, discountCouponService1, converter, validators);
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista contendo um pagamento")
    void findAllPayments_SuccessfulSearch_ReturnsListResponse_OnePayment() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        Order order = new Order(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, user);

        Payment payment = new Payment(1L, Instant.now(), order);
        List<Payment> paymentList = Collections.singletonList(payment);

        PaymentResponse paymentResponse = new PaymentResponse(1L, Instant.now(), order);
        List<PaymentResponse> expectedResponses = Collections.singletonList(paymentResponse);

        when(converter.toResponse(payment, PaymentResponse.class)).thenReturn(paymentResponse);
        when(paymentRepository.findAll()).thenReturn(paymentList);

        // Act
        List<PaymentResponse> actualResponses = paymentService.findAllPayments();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one payment");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one payment");

        verify(converter, times(1)).toResponse(payment, PaymentResponse.class);
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista contendo múltiplos pagamentos")
    void findAllPayments_SuccessfulSearch_ReturnsListResponse_MultiplePayments() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        Order order = new Order(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, user);

        List<Payment> paymentList = new ArrayList<>();
        List<PaymentResponse> expectedResponses = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Payment payment = new Payment((long) i, Instant.now(), order);
            paymentList.add(payment);

            PaymentResponse paymentResponse = new PaymentResponse((long) i, Instant.now(), order);
            expectedResponses.add(paymentResponse);

            when(converter.toResponse(payment, PaymentResponse.class)).thenReturn(paymentResponse);
        }

        when(paymentRepository.findAll()).thenReturn(paymentList);

        // Act
        List<PaymentResponse> actualResponses = paymentService.findAllPayments();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple payments");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple payments");

        verify(converter, times(10)).toResponse(any(Payment.class), eq(PaymentResponse.class));
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista de pagamentos vazia")
    void findAllPayments_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<Payment> emptyPaymentList = new ArrayList<>();

        when(paymentRepository.findAll()).thenReturn(emptyPaymentList);

        // Act
        List<PaymentResponse> actualResponses = paymentService.findAllPayments();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyPaymentList, actualResponses, "Expected an empty list of responses");

        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllPayments - Exceção ao tentar buscar lista de pagamentos")
    void findAllPayments_RepositoryExceptionHandling() {
        // Arrange
        when(paymentRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> paymentService.findAllPayments());

        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createPayment - Criação bem-sucedida retorna pagamento criado")
    void createPayment_SuccessfulCreation_ReturnsPaymentResponse() {
        // Arrange
        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        user.setAddress(address);

        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);
        OrderItem orderItem = new OrderItem(order, product, orderItemRequest.getQuantity(), product.getPrice());

        order.getItems().add(orderItem);

        PaymentRequest paymentRequest = new PaymentRequest(1L);
        Payment payment = new Payment(Instant.now(), order);
        PaymentResponse expectedResponse = new PaymentResponse(1L, Instant.now(), order);

        when(orderService.findOrderById(paymentRequest.getOrderId())).thenReturn(order);
        doNothing().when(orderService).savePaidOrder(order);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(converter.toResponse(payment, PaymentResponse.class)).thenReturn(expectedResponse);

        // Act
        PaymentResponse actualResponse = paymentService.createPayment(paymentRequest);

        // Assert
        assertNotNull(actualResponse, "paymentResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(orderService, times(1)).findOrderById(paymentRequest.getOrderId());
        verify(orderService, times(1)).savePaidOrder(order);
        verify(paymentRepository, times(1)).save(payment);
        verify(converter, times(1)).toResponse(payment, PaymentResponse.class);
    }

    @Test
    @DisplayName("createPayment - Exceção no repositório ao tentar criar pagamento")
    void createPayment_RepositoryExceptionHandling() {
        // Arrange
        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        user.setAddress(address);

        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);
        OrderItem orderItem = new OrderItem(order, product, orderItemRequest.getQuantity(), product.getPrice());

        order.getItems().add(orderItem);

        PaymentRequest paymentRequest = new PaymentRequest(1L);
        Payment payment = new Payment(Instant.now(), order);

        when(orderService.findOrderById(paymentRequest.getOrderId())).thenReturn(order);
        doNothing().when(orderService).savePaidOrder(order);
        when(paymentRepository.save(payment)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> paymentService.createPayment(paymentRequest), "Expected RepositoryException due to a generic runtime exception");

        verify(orderService, times(1)).findOrderById(paymentRequest.getOrderId());
        verify(orderService, times(1)).savePaidOrder(order);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    @DisplayName("findPaymentById - Busca bem-sucedida retorna pagamento")
    void findCategoryById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);

        Payment payment = new Payment(Instant.now(), order);
        PaymentResponse expectedResponse = new PaymentResponse(1L, Instant.now(), order);

        when(converter.toResponse(payment, PaymentResponse.class)).thenReturn(expectedResponse);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act
        PaymentResponse actualResponse = paymentService.findPaymentResponseById(1L);

        // Assert
        assertNotNull(actualResponse, "paymentResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(payment, PaymentResponse.class);
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findPaymentById - Exceção ao tentar buscar pagamento inexistente")
    void findPayment_NotFoundExceptionHandling() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> paymentService.findPaymentResponseById(1L));

        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deletePayment - Exclusão bem-sucedida do pedido")
    void deletePayment_DeletesPaymentSuccessfully() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);

        Payment payment = new Payment(Instant.now(), order);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act
        paymentService.deletePayment(1L);

        // Assert
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deletePayment - Exceção ao tentar excluir pagamento inexistente")
    void deletePayment_NotFoundExceptionHandling() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> paymentService.deletePayment(1L));

        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deletePayment - Exceção no repositório ao tentar excluir pagamento")
    void deletePayment_RepositoryExceptionHandling() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);

        Payment payment = new Payment(Instant.now(), order);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        doThrow(PersistenceException.class).when(paymentRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> paymentService.deletePayment(1L), "Expected RepositoryException for delete failure");

        verify(paymentRepository, times(1)).findById(1L);
    }
}
