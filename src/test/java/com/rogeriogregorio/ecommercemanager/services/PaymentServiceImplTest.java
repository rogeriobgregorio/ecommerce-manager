package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
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
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Converter converter;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentRepository, orderRepository, converter);
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista contendo um pagamento")
    void findAllPayments_SuccessfulSearch_ReturnsListResponse_OnePayment() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
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
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
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
        
    }
}
