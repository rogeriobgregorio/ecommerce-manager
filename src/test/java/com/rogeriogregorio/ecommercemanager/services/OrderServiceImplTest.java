package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    @DisplayName("findAllOrders - Busca bem-sucedida retorna lista contendo um pedido")
    void findAllOrders_SuccessfulSearch_ReturnsListResponse_OneOrder() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        List<OrderEntity> orderEntityList = Collections.singletonList(orderEntity);

        OrderResponse orderResponse = new OrderResponse(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        List<OrderResponse> expectedResponses = Collections.singletonList(orderResponse);

        when(orderConverter.entityToResponse(orderEntity)).thenReturn(orderResponse);
        when(orderRepository.findAll()).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findAllOrders();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one order");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one order");

        verify(orderConverter, times(1)).entityToResponse(any(OrderEntity.class));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrders - Busca bem-sucedida retorna lista contendo múltiplos pedidos")
    void findAllOrders_SuccessfulSearch_ReturnsListResponse_MultipleOrders() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        List<OrderEntity> orderEntityList = new ArrayList<>();
        List<OrderResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            OrderEntity orderEntity = new OrderEntity((long) i, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
            orderEntityList.add(orderEntity);

            OrderResponse orderResponse = new OrderResponse((long) i, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
            expectedResponses.add(orderResponse);

            when(orderConverter.entityToResponse(orderEntity)).thenReturn(orderResponse);
        }

        when(orderRepository.findAll()).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findAllOrders();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple orders");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple orders");

        verify(orderConverter, times(10)).entityToResponse(any(OrderEntity.class));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrders - Busca bem-sucedida retorna lista de pedidos vazia")
    void findAllOrders_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<OrderEntity> emptyOrderEntityList = new ArrayList<>();

        when(orderRepository.findAll()).thenReturn(emptyOrderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findAllOrders();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyOrderEntityList, actualResponses, "Expected an empty list of responses");

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrders - Exceção ao tentar buscar lista de pedidos")
    void findAllOrders_OrderRepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findAll()).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.findAllOrders(), "Expected RepositoryException to be thrown");

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createOrder - Criação bem-sucedida retorna pedido criado")
    void createOrder_SuccessfulCreation_ReturnsOrderResponse() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderRequest orderRequest = new OrderRequest(Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderResponse expectedResponse = new OrderResponse(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderConverter.requestToEntity(orderRequest)).thenReturn(orderEntity);
        when(orderConverter.entityToResponse(orderEntity)).thenReturn(expectedResponse);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        // Act
        OrderResponse actualResponse = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(actualResponse, "OrderResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(orderRepository, times(1)).save(any());
        verify(orderConverter, times(1)).requestToEntity(orderRequest);
        verify(orderConverter, times(1)).entityToResponse(orderEntity);
    }

    @Test
    @DisplayName("createOrder - Exceção no repositório ao tentar criar pedido")
    void createOrder_RepositoryExceptionHandling() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderRequest orderRequest = new OrderRequest(Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderConverter.requestToEntity(orderRequest)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.createOrder(orderRequest), "Expected RepositoryException due to a generic runtime exception");

        verify(orderConverter, times(1)).requestToEntity(orderRequest);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    @DisplayName("findOrderById - Busca bem-sucedida retorna pedido")
    void findOrderById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderResponse expectedResponse = new OrderResponse(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderConverter.entityToResponse(orderEntity)).thenReturn(expectedResponse);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        // Act
        OrderResponse actualResponse = orderService.findOrderById(1L);

        // Assert
        assertNotNull(actualResponse, "OrderResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(orderConverter, times(1)).entityToResponse(orderEntity);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findOrderById - Exceção ao tentar buscar pedido inexistente")
    void findOrderById_NotFoundExceptionHandling() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderService.findOrderById(1L), "Expected NotFoundException for non-existent order");

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateOrder - Atualização bem-sucedida retorna pedido atualizado")
    void updateOrder_SuccessfulUpdate_ReturnsOrderResponse() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderRequest orderRequest = new OrderRequest(Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderResponse expectedResponse = new OrderResponse(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderConverter.requestToEntity(orderRequest)).thenReturn(orderEntity);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(orderConverter.entityToResponse(orderEntity)).thenReturn(expectedResponse);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        // Act
        OrderResponse actualResponse = orderService.updateOrder(orderRequest);

        // Assert
        assertNotNull(actualResponse, "OrderResponse should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse.getMoment(), actualResponse.getMoment(), "Moments should match");
        assertEquals(expectedResponse.getOrderStatus(), actualResponse.getOrderStatus(), "OrderStatus should match");
        assertEquals(expectedResponse.getClient(), actualResponse.getClient(), "Clients should match");

        verify(orderConverter, times(1)).requestToEntity(orderRequest);
        verify(orderRepository, times(1)).findById(orderEntity.getId());
        verify(orderRepository, times(1)).save(orderEntity);
        verify(orderConverter, times(1)).entityToResponse(orderEntity);
    }

    @Test
    @DisplayName("updateOrder - Exceção ao tentar atualizar pedido inexistente")
    void updateOrder_NotFoundExceptionHandling() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderRequest orderRequest = new OrderRequest(Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderConverter.requestToEntity(orderRequest)).thenReturn(orderEntity);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderService.updateOrder(orderRequest), "Expected NotFoundException for non-existent order");

        verify(orderConverter, times(1)).requestToEntity(orderRequest);
        verify(orderRepository, times(1)).findById(orderEntity.getId());
    }

    @Test
    @DisplayName("updateOrder - Exceção no repositório ao tentar atualizar pedido")
    void updateOrder_RepositoryExceptionHandling() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderRequest orderRequest = new OrderRequest(Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderConverter.requestToEntity(orderRequest)).thenReturn(orderEntity);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(orderEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.updateOrder(orderRequest), "Expected RepositoryException for update failure");

        verify(orderConverter, times(1)).requestToEntity(orderRequest);
        verify(orderRepository, times(1)).findById(orderEntity.getId());
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    @DisplayName("deleteOrder - Exclusão bem-sucedida do pedido")
    void deleteOrder_DeletesOrderSuccessfully() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteOrder - Exceção ao tentar excluir pedido inexistente")
    void deleteOrder_NotFoundExceptionHandling() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderService.deleteOrder(1L), "Expected NotFoundException for non-existent order");

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deleteOrder - Exceção no repositório ao tentar excluir pedido")
    void deleteOrder_RepositoryExceptionHandling() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        doThrow(RuntimeException.class).when(orderRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.deleteOrder(1L), "Expected RepositoryException for delete failure");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("findOrderByClientId - Busca bem-sucedida retorna lista contendo um pedido")
    void findOrderByClientId_SuccessfulSearch_ReturnsOrderResponse_OneOrder() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        List<OrderEntity> orderEntityList = Collections.singletonList(orderEntity);

        OrderResponse orderResponse = new OrderResponse(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        List<OrderResponse> expectedResponses = Collections.singletonList(orderResponse);

        when(orderConverter.entityToResponse(orderEntity)).thenReturn(orderResponse);
        when(orderRepository.findByClient_Id(1L)).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findOrderByClientId(1L);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one order");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one order");

        verify(orderConverter, times(1)).entityToResponse(orderEntity);
        verify(orderRepository, times(1)).findByClient_Id(1L);
    }

    @Test
    @DisplayName("findOrderByClientId - Busca bem-sucedida retorna lista contendo múltiplos pedidos")
    void findOrderByClientId_SuccessfulSearch_ReturnsListResponse_MultipleOrders() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        List<OrderEntity> orderEntityList = new ArrayList<>();
        List<OrderResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            OrderEntity orderEntity = new OrderEntity((long) i, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
            orderEntityList.add(orderEntity);

            OrderResponse orderResponse = new OrderResponse((long) i, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
            expectedResponses.add(orderResponse);

            when(orderConverter.entityToResponse(orderEntity)).thenReturn(orderResponse);
        }

        when(orderRepository.findByClient_Id(1L)).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findOrderByClientId(1L);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple orders");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple orders");

        verify(orderConverter, times(10)).entityToResponse(any(OrderEntity.class));
        verify(orderRepository, times(1)).findByClient_Id(1L);
    }

    @Test
    @DisplayName("findOrderByClientId - Busca bem-sucedida retorna lista de pedidos vazia")
    void findOrderByClientId_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<OrderEntity> emptyOrderEntityList = new ArrayList<>();
        when(orderRepository.findByClient_Id(1L)).thenReturn(emptyOrderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findOrderByClientId(1L);

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyOrderEntityList, actualResponses, "Expected an empty list of responses");

        verify(orderRepository, times(1)).findByClient_Id(1L);
    }

    @Test
    @DisplayName("findOrderByClientId - Exceção ao tentar buscar lista de pedidos")
    void findOrderByClientId_RepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findByClient_Id(1L)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.findOrderByClientId(1L), "Expected RepositoryException to be thrown");

        verify(orderRepository, times(1)).findByClient_Id(1L);
    }
}
