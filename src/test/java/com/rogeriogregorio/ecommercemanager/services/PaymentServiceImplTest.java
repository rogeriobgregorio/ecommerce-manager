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
import com.rogeriogregorio.ecommercemanager.util.Converter;
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
    private OrderService orderService;

    @Mock
    private InventoryItemService inventoryItemService;

    @Mock
    private Converter converter;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentRepository, orderService, inventoryItemService, converter);
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista contendo um pagamento")
    void findAllPayments_SuccessfulSearch_ReturnsListResponse_OnePayment() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, userEntity);

        PaymentEntity paymentEntity = new PaymentEntity(1L, Instant.now(), orderEntity);
        List<PaymentEntity> paymentEntityList = Collections.singletonList(paymentEntity);

        PaymentResponse paymentResponse = new PaymentResponse(1L, Instant.now(), orderEntity);
        List<PaymentResponse> expectedResponses = Collections.singletonList(paymentResponse);

        when(converter.toResponse(paymentEntity, PaymentResponse.class)).thenReturn(paymentResponse);
        when(paymentRepository.findAll()).thenReturn(paymentEntityList);

        // Act
        List<PaymentResponse> actualResponses = paymentService.findAllPayments();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one payment");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one payment");

        verify(converter, times(1)).toResponse(paymentEntity, PaymentResponse.class);
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista contendo múltiplos pagamentos")
    void findAllPayments_SuccessfulSearch_ReturnsListResponse_MultiplePayments() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, userEntity);

        List<PaymentEntity> paymentEntityList = new ArrayList<>();
        List<PaymentResponse> expectedResponses = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            PaymentEntity paymentEntity = new PaymentEntity((long) i, Instant.now(), orderEntity);
            paymentEntityList.add(paymentEntity);

            PaymentResponse paymentResponse = new PaymentResponse((long) i, Instant.now(), orderEntity);
            expectedResponses.add(paymentResponse);

            when(converter.toResponse(paymentEntity, PaymentResponse.class)).thenReturn(paymentResponse);
        }

        when(paymentRepository.findAll()).thenReturn(paymentEntityList);

        // Act
        List<PaymentResponse> actualResponses = paymentService.findAllPayments();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple payments");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple payments");

        verify(converter, times(10)).toResponse(any(PaymentEntity.class), eq(PaymentResponse.class));
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista de pagamentos vazia")
    void findAllPayments_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<PaymentEntity> emptyPaymentEntityList = new ArrayList<>();

        when(paymentRepository.findAll()).thenReturn(emptyPaymentEntityList);

        // Act
        List<PaymentResponse> actualResponses = paymentService.findAllPayments();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyPaymentEntityList, actualResponses, "Expected an empty list of responses");

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
        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        userEntity.setAddressEntity(addressEntity);

        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, userEntity);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);
        OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, productEntity, orderItemRequest.getQuantity(), productEntity.getPrice());

        orderEntity.getItems().add(orderItemEntity);

        PaymentRequest paymentRequest = new PaymentRequest(1L);
        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderEntity);
        PaymentResponse expectedResponse = new PaymentResponse(1L, Instant.now(), orderEntity);

        when(orderService.isOrderItemsPresent(orderEntity)).thenReturn(true);
        when(orderService.isAddressClientPresent(orderEntity)).thenReturn(true);
        when(orderService.findOrderEntityById(paymentRequest.getOrderId())).thenReturn(orderEntity);
        doNothing().when(orderService).savePaidOrder(orderEntity);
        when(paymentRepository.save(paymentEntity)).thenReturn(paymentEntity);
        when(converter.toResponse(paymentEntity, PaymentResponse.class)).thenReturn(expectedResponse);

        // Act
        PaymentResponse actualResponse = paymentService.createPayment(paymentRequest);

        // Assert
        assertNotNull(actualResponse, "paymentResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(orderService, times(1)).findOrderEntityById(paymentRequest.getOrderId());
        verify(orderService, times(1)).savePaidOrder(orderEntity);
        verify(paymentRepository, times(1)).save(paymentEntity);
        verify(converter, times(1)).toResponse(paymentEntity, PaymentResponse.class);
    }

    @Test
    @DisplayName("createPayment - Exceção no repositório ao tentar criar pagamento")
    void createPayment_RepositoryExceptionHandling() {
        // Arrange
        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        userEntity.setAddressEntity(addressEntity);

        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, userEntity);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);
        OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, productEntity, orderItemRequest.getQuantity(), productEntity.getPrice());

        orderEntity.getItems().add(orderItemEntity);

        PaymentRequest paymentRequest = new PaymentRequest(1L);
        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderEntity);

        when(orderService.isOrderItemsPresent(orderEntity)).thenReturn(true);
        when(orderService.isAddressClientPresent(orderEntity)).thenReturn(true);
        when(orderService.findOrderEntityById(paymentRequest.getOrderId())).thenReturn(orderEntity);
        doNothing().when(orderService).savePaidOrder(orderEntity);
        when(paymentRepository.save(paymentEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> paymentService.createPayment(paymentRequest), "Expected RepositoryException due to a generic runtime exception");

        verify(orderService, times(1)).findOrderEntityById(paymentRequest.getOrderId());
        verify(orderService, times(1)).savePaidOrder(orderEntity);
        verify(paymentRepository, times(1)).save(paymentEntity);
    }

    @Test
    @DisplayName("findPaymentById - Busca bem-sucedida retorna pagamento")
    void findCategoryById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, userEntity);

        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderEntity);
        PaymentResponse expectedResponse = new PaymentResponse(1L, Instant.now(), orderEntity);

        when(converter.toResponse(paymentEntity, PaymentResponse.class)).thenReturn(expectedResponse);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentEntity));

        // Act
        PaymentResponse actualResponse = paymentService.findPaymentById(1L);

        // Assert
        assertNotNull(actualResponse, "paymentResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(paymentEntity, PaymentResponse.class);
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findPaymentById - Exceção ao tentar buscar pagamento inexistente")
    void findPayment_NotFoundExceptionHandling() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> paymentService.findPaymentById(1L));

        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deletePayment - Exclusão bem-sucedida do pedido")
    void deletePayment_DeletesPaymentSuccessfully() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, userEntity);

        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderEntity);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentEntity));

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
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, userEntity);

        PaymentEntity paymentEntity = new PaymentEntity(Instant.now(), orderEntity);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(paymentEntity));
        doThrow(PersistenceException.class).when(paymentRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> paymentService.deletePayment(1L), "Expected RepositoryException for delete failure");

        verify(paymentRepository, times(1)).findById(1L);
    }
}
