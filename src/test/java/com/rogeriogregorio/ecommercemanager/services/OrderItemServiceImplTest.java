package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.OrderItemServiceImpl;
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
public class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private Converter converter;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderItemService = new OrderItemServiceImpl(orderItemRepository, orderService, productService, converter);
    }

    @Test
    @DisplayName("findAllOrderItems - Busca bem-sucedida retorna lista contendo itens de um pedido")
    void findAllOrderItems_SuccessfulSearch_ReturnsListResponse_OneOrderItem() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, userEntity);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, productEntity, 1, 4099.0);
        List<OrderItemEntity> orderItemEntityList = Collections.singletonList(orderItemEntity);

        OrderItemResponse orderItemResponse = new OrderItemResponse(orderEntity, productEntity, 1, 4099.0);
        List<OrderItemResponse> expectedResponses = Collections.singletonList(orderItemResponse);

        when(converter.toResponse(orderItemEntity, OrderItemResponse.class)).thenReturn(orderItemResponse);
        when(orderItemRepository.findAll()).thenReturn(orderItemEntityList);

        // Act
        List<OrderItemResponse> actualResponses = orderItemService.findAllOrderItems();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one orderItem");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one category");

        verify(converter, times(1)).toResponse(orderItemEntity, OrderItemResponse.class);
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrderItems - Busca bem-sucedida retorna lista contendo itens de múltiplos pedidos")
    void findAllOrderItems_SuccessfulSearch_ReturnsListResponse_MultipleOrderItems() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        List<OrderItemResponse> expectedResponses = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            OrderEntity o1 = new OrderEntity((long) i, Instant.now(), OrderStatus.PAID, userEntity);

            OrderItemEntity orderItemEntity = new OrderItemEntity(o1, productEntity, 1, 4099.0);
            orderItemEntityList.add(orderItemEntity);

            OrderItemResponse orderItemResponse = new OrderItemResponse(o1, productEntity, 1, 4099.0);
            expectedResponses.add(orderItemResponse);

            when(converter.toResponse(orderItemEntity, OrderItemResponse.class)).thenReturn(orderItemResponse);
        }

        when(orderItemRepository.findAll()).thenReturn(orderItemEntityList);

        // Act
        List<OrderItemResponse> actualResponses = orderItemService.findAllOrderItems();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple orderItem");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple categories");

        verify(converter, times(10)).toResponse(any(OrderItemEntity.class), eq(OrderItemResponse.class));
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrderItems - Busca bem-sucedida retorna lista de itens de pedidos vazia")
    void findAllOrderItems_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<OrderItemEntity> emptyOrderItemList = new ArrayList<>();

        when(orderItemRepository.findAll()).thenReturn(emptyOrderItemList);

        // Act
        List<OrderItemResponse> actualResponses = orderItemService.findAllOrderItems();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyOrderItemList, actualResponses, "Expected an empty list of responses");

        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrderItems - Exceção ao tentar buscar lista de itens de pedidos")
    void findAllOrderItems_RepositoryExceptionHandling() {
        // Arrange
        when(orderItemRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderItemService.findAllOrderItems());

        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createOrderItem - Criação bem-sucedida retorna item do pedido criado")
    void createOrderItem_SuccessfulCreation_ReturnsOrderItemResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, userEntity);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, productEntity, 1, 4099.0);

        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);
        OrderItemResponse expectedResponse = new OrderItemResponse(orderEntity, productEntity, 1, 4099.0);

        when(orderService.findOrderById(1L)).thenReturn(orderEntity);
        when(productService.findProductById(1L)).thenReturn(productEntity);
        when(orderItemRepository.save(orderItemEntity)).thenReturn(orderItemEntity);
        when(converter.toResponse(orderItemEntity, OrderItemResponse.class)).thenReturn(expectedResponse);

        // Act
        OrderItemResponse actualResponse = orderItemService.createOrderItem(orderItemRequest);

        // Assert
        assertNotNull(actualResponse, "OrderItemResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be qual");

        verify(orderItemService, times(1)).buildOrderItemFromRequest(orderItemRequest);
        verify(converter, times(1)).toResponse(orderItemEntity, OrderItemResponse.class);
        verify(orderItemRepository, times(1)).save(orderItemEntity);
    }

    @Test
    @DisplayName("createOrderItem - Exceção no repositório ao tentar criar item do pedido")
    void createOrderItem_RepositoryExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, userEntity);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);
        OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, productEntity, orderItemRequest.getQuantity(), productEntity.getPrice());

        when(orderItemService.buildOrderItemFromRequest(orderItemRequest)).thenReturn(orderItemEntity);
        when(orderItemRepository.save(orderItemEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> orderItemService.createOrderItem(orderItemRequest));

        verify(converter, times(1)).toEntity(orderItemRequest, OrderItemEntity.class);
        verify(orderItemRepository, times(1)).save(orderItemEntity);
    }

    @Test
    @DisplayName("findOrderItemById - Busca bem-sucedida retorna item(s) do pedido")
    void findOrderItemById_SuccessfulSearch_ReturnsOrderItemResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity orderEntity = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, userEntity);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemPK id = new OrderItemPK();
        id.setOrderEntity(orderEntity);
        id.setProductEntity(productEntity);

        OrderItemEntity orderItemEntity = new OrderItemEntity(orderEntity, productEntity, 1, 4099.0);
        OrderItemResponse expectedResponse = new OrderItemResponse(orderEntity, productEntity, 1, 4099.0);

        when(orderItemService.buildOrderItemPK(1L, 1L)).thenReturn(id);
        when(converter.toResponse(orderItemEntity, OrderItemResponse.class)).thenReturn(expectedResponse);
        when(orderItemRepository.findById(id)).thenReturn(Optional.of(orderItemEntity));

        // Act
        OrderItemResponse actualResponse = orderItemService.findOrderItemById(1L, 1L);

        // Assert
        assertNotNull(actualResponse, "OrderItemResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(orderItemService, times(1)).buildOrderItemPK(1L, 1L);
        verify(converter, times(1)).toResponse(orderItemEntity, OrderItemResponse.class);
        verify(orderItemRepository, times(1)).findById(id);
    }
}
