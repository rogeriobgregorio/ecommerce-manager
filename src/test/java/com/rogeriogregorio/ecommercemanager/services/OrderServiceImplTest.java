package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.OrderServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private Converter converter;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderRepository, userService, converter);
    }

    @Test
    @DisplayName("findAllOrders - Busca bem-sucedida retorna lista contendo um pedido")
    void findAllOrders_SuccessfulSearch_ReturnsListResponse_OneOrder() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);
        List<OrderEntity> orderEntityList = Collections.singletonList(orderEntity);

        OrderResponse orderResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, userEntity);
        List<OrderResponse> expectedResponses = Collections.singletonList(orderResponse);

        when(converter.toResponse(orderEntity, OrderResponse.class)).thenReturn(orderResponse);
        when(orderRepository.findAll()).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findAllOrders();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one order");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one order");

        verify(converter, times(1)).toResponse(orderEntity, OrderResponse.class);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrders - Busca bem-sucedida retorna lista contendo múltiplos pedidos")
    void findAllOrders_SuccessfulSearch_ReturnsListResponse_MultipleOrders() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        List<OrderEntity> orderEntityList = new ArrayList<>();
        List<OrderResponse> expectedResponses = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            OrderEntity orderEntity = new OrderEntity((long) i, Instant.now(), OrderStatus.PAID, userEntity);
            orderEntityList.add(orderEntity);

            OrderResponse orderResponse = new OrderResponse((long) i, Instant.now(), OrderStatus.PAID, userEntity);
            expectedResponses.add(orderResponse);

            when(converter.toResponse(orderEntity, OrderResponse.class)).thenReturn(orderResponse);
        }

        when(orderRepository.findAll()).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findAllOrders();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple orders");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple orders");

        verify(converter, times(10)).toResponse(any(OrderEntity.class), eq(OrderResponse.class));
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
    void findAllOrders_RepositoryExceptionHandling() {
        // Arrange
        when(orderRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.findAllOrders(), "Expected RepositoryException to be thrown");

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createOrder - Criação bem-sucedida retorna pedido criado")
    void createOrder_SuccessfulCreation_ReturnsOrderResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        UserResponse userResponse = new UserResponse(1L, "Maria Brown", "maria@gmail.com", "988888888");

        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);
        OrderResponse expectedResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, userEntity);

        when(userService.findUserById(orderRequest.getClientId())).thenReturn(userResponse);
        when(converter.toEntity(userResponse, UserEntity.class)).thenReturn(userEntity);
        when(converter.toEntity(orderRequest, OrderEntity.class)).thenReturn(orderEntity);
        when(converter.toResponse(orderEntity, OrderResponse.class)).thenReturn(expectedResponse);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);

        // Act
        OrderResponse actualResponse = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(actualResponse, "OrderResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(userService, times(1)).findUserById(orderRequest.getClientId());
        verify(converter, times(1)).toEntity(userResponse, UserEntity.class);
        verify(converter, times(1)).toEntity(orderRequest, OrderEntity.class);
        verify(converter, times(1)).toResponse(orderEntity, OrderResponse.class);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    @DisplayName("createOrder - Exceção no repositório ao tentar criar pedido")
    void createOrder_RepositoryExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        UserResponse userResponse = new UserResponse(1L, "Maria Brown", "maria@gmail.com", "988888888");

        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);

        when(userService.findUserById(orderRequest.getClientId())).thenReturn(userResponse);
        when(converter.toEntity(userResponse, UserEntity.class)).thenReturn(userEntity);
        when(converter.toEntity(orderRequest, OrderEntity.class)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.createOrder(orderRequest), "Expected RepositoryException due to a generic runtime exception");

        verify(converter, times(1)).toEntity(orderRequest, OrderEntity.class);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    @DisplayName("findOrderById - Busca bem-sucedida retorna pedido")
    void findOrderById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);
        OrderResponse expectedResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, userEntity);

        when(converter.toResponse(orderEntity, OrderResponse.class)).thenReturn(expectedResponse);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        // Act
        OrderResponse actualResponse = orderService.findOrderById(1L);

        // Assert
        assertNotNull(actualResponse, "OrderResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(orderEntity, OrderResponse.class);
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
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        UserResponse userResponse = new UserResponse(1L, "Maria Brown", "maria@gmail.com", "988888888");

        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);
        OrderResponse expectedResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, userEntity);

        when(converter.toEntity(orderRequest, OrderEntity.class)).thenReturn(orderEntity);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(userService.findUserById(orderRequest.getClientId())).thenReturn(userResponse);
        when(converter.toEntity(userResponse, UserEntity.class)).thenReturn(userEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(converter.toResponse(orderEntity, OrderResponse.class)).thenReturn(expectedResponse);

        // Act
        OrderResponse actualResponse = orderService.updateOrder(orderRequest);

        // Assert
        assertNotNull(actualResponse, "OrderResponse should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse.getMoment(), actualResponse.getMoment(), "Moments should match");
        assertEquals(expectedResponse.getOrderStatus(), actualResponse.getOrderStatus(), "OrderStatus should match");
        assertEquals(expectedResponse.getClient(), actualResponse.getClient(), "Clients should match");

        verify(converter, times(1)).toEntity(orderRequest, OrderEntity.class);
        verify(orderRepository, times(1)).findById(orderEntity.getId());
        verify(userService, times(1)).findUserById(orderRequest.getClientId());
        verify(converter, times(1)).toEntity(userResponse, UserEntity.class);
        verify(orderRepository, times(1)).save(orderEntity);
        verify(converter, times(1)).toResponse(orderEntity, OrderResponse.class);
    }

    @Test
    @DisplayName("updateOrder - Exceção ao tentar atualizar pedido inexistente")
    void updateOrder_NotFoundExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);

        when(converter.toEntity(orderRequest, OrderEntity.class)).thenReturn(orderEntity);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> orderService.updateOrder(orderRequest), "Expected NotFoundException for non-existent order");

        verify(converter, times(1)).toEntity(orderRequest, OrderEntity.class);
        verify(orderRepository, times(1)).findById(orderEntity.getId());
    }

    @Test
    @DisplayName("updateOrder - Exceção no repositório ao tentar atualizar pedido")
    void updateOrder_RepositoryExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        UserResponse userResponse = new UserResponse(1L, "Maria Brown", "maria@gmail.com", "988888888");

        OrderRequest orderRequest = new OrderRequest(1L, OrderStatus.PAID);
        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);

        when(converter.toEntity(orderRequest, OrderEntity.class)).thenReturn(orderEntity);
        when(orderRepository.findById(orderEntity.getId())).thenReturn(Optional.of(orderEntity));
        when(userService.findUserById(orderRequest.getClientId())).thenReturn(userResponse);
        when(converter.toEntity(userResponse, UserEntity.class)).thenReturn(userEntity);
        when(orderRepository.save(orderEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.updateOrder(orderRequest), "Expected RepositoryException for update failure");

        verify(converter, times(1)).toEntity(orderRequest, OrderEntity.class);
        verify(orderRepository, times(1)).findById(orderEntity.getId());
        verify(userService, times(1)).findUserById(orderRequest.getClientId());
        verify(converter, times(1)).toEntity(userResponse, UserEntity.class);
        verify(orderRepository, times(1)).save(orderEntity);
    }

    @Test
    @DisplayName("deleteOrder - Exclusão bem-sucedida do pedido")
    void deleteOrder_DeletesOrderSuccessfully() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);

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
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        doThrow(PersistenceException.class).when(orderRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.deleteOrder(1L), "Expected RepositoryException for delete failure");

        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("findOrderByClientId - Busca bem-sucedida retorna lista contendo um pedido")
    void findOrderByClientId_SuccessfulSearch_ReturnsOrderResponse_OneOrder() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        OrderEntity orderEntity = new OrderEntity(1L, Instant.now(), OrderStatus.PAID, userEntity);
        List<OrderEntity> orderEntityList = Collections.singletonList(orderEntity);

        OrderResponse orderResponse = new OrderResponse(1L, Instant.now(), OrderStatus.PAID, userEntity);
        List<OrderResponse> expectedResponses = Collections.singletonList(orderResponse);

        when(converter.toResponse(orderEntity, OrderResponse.class)).thenReturn(orderResponse);
        when(orderRepository.findByClient_Id(1L)).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findOrderByClientId(1L);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one order");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one order");

        verify(converter, times(1)).toResponse(orderEntity, OrderResponse.class);
        verify(orderRepository, times(1)).findByClient_Id(1L);
    }

    @Test
    @DisplayName("findOrderByClientId - Busca bem-sucedida retorna lista contendo múltiplos pedidos")
    void findOrderByClientId_SuccessfulSearch_ReturnsListResponse_MultipleOrders() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        List<OrderEntity> orderEntityList = new ArrayList<>();
        List<OrderResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            OrderEntity orderEntity = new OrderEntity((long) i, Instant.now(), OrderStatus.PAID, userEntity);
            orderEntityList.add(orderEntity);

            OrderResponse orderResponse = new OrderResponse((long) i, Instant.now(), OrderStatus.PAID, userEntity);
            expectedResponses.add(orderResponse);

            when(converter.toResponse(orderEntity, OrderResponse.class)).thenReturn(orderResponse);
        }

        when(orderRepository.findByClient_Id(1L)).thenReturn(orderEntityList);

        // Act
        List<OrderResponse> actualResponses = orderService.findOrderByClientId(1L);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple orders");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple orders");

        verify(converter, times(10)).toResponse(any(OrderEntity.class), eq(OrderResponse.class));
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
        when(orderRepository.findByClient_Id(1L)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderService.findOrderByClientId(1L), "Expected RepositoryException to be thrown");

        verify(orderRepository, times(1)).findByClient_Id(1L);
    }
}
