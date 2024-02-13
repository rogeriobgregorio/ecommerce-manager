package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.OrderServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private Converter<OrderRequest, OrderEntity, OrderResponse> orderConverter;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderRepository, orderConverter);
    }

    @Test
    @DisplayName("findAllOrders - Busca bem-sucedida retorna lista de pedidos")
    void findAllOrders_SuccessfulSearch_ReturnsListResponse() {
        // Arrange
        OrderEntity orderEntity = new OrderEntity(/* define o que é necessário para criar uma OrderEntity */);
        List<OrderEntity> orderEntityList = Collections.singletonList(orderEntity);

        OrderResponse orderResponse = new OrderResponse(/* define o que é necessário para criar uma OrderResponse */);
        List<OrderResponse> expectedResponses = Collections.singletonList(orderResponse);

        when(orderConverter.entityToResponse(orderEntity)).thenReturn(orderResponse);
        when(orderRepository.findAll()).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findAllOrders();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size());
        assertIterableEquals(expectedResponses, actualResponses);

        verify(orderConverter, times(1)).entityToResponse(any());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrders - Exceção ao tentar buscar lista de pedidos")
    void findAllOrders_OrderRepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findAll()).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.findAllOrders(), "Expected RepositoryException to be thrown");
    }
}
