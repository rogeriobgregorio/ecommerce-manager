package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.repositories.OrderItemRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.OrderItemServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceImplTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private Converter<OrderItemRequest, OrderItemEntity, OrderItemResponse> orderItemConverter;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderItemService = new OrderItemServiceImpl(orderItemRepository, orderItemConverter);
    }

    @Test
    @DisplayName("findAllOrderItems - Busca bem-sucedida retorna lista contendo itens de um pedido")
    void findAllOrderItems_SuccessfulSearch_ReturnsListResponse_OneOrderItem() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        OrderEntity o1 = new OrderEntity(1L, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        ProductEntity p1 = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        OrderItemEntity orderItemEntity = new OrderItemEntity(o1, p1, 1, 4099.0);
        List<OrderItemEntity> orderItemEntityList = Collections.singletonList(orderItemEntity);

        OrderItemResponse orderItemResponse = new OrderItemResponse(o1, p1, 1, 4099.0);
        List<OrderItemResponse> expectedResponses = Collections.singletonList(orderItemResponse);

        when(orderItemConverter.entityToResponse(orderItemEntity)).thenReturn(orderItemResponse);
        when(orderItemRepository.findAll()).thenReturn(orderItemEntityList);

        // Act
        List<OrderItemResponse> actualResponses = orderItemService.findAllOrderItems();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one orderItem");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one category");

        verify(orderItemConverter, times(1)).entityToResponse(any(OrderItemEntity.class));
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrderItems - Busca bem-sucedida retorna lista contendo itens de múltiplos pedidos")
    void findAllCategories_SuccessfulSearch_ReturnsListResponse_MultipleCategories() {
        // Arrange
        UserEntity u1 = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        ProductEntity p1 = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        List<OrderItemResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            OrderEntity o1 = new OrderEntity((long) i, Instant.now(), OrderStatus.PAID, u1);

            OrderItemEntity orderItemEntity = new OrderItemEntity(o1, p1, 1, 4099.0);
            orderItemEntityList.add(orderItemEntity);

            OrderItemResponse orderItemResponse = new OrderItemResponse(o1, p1, 1, 4099.0);
            expectedResponses.add(orderItemResponse);

            when(orderItemConverter.entityToResponse(orderItemEntity)).thenReturn(orderItemResponse);
        }

        when(orderItemRepository.findAll()).thenReturn(orderItemEntityList);

        // Act
        List<OrderItemResponse> actualResponses = orderItemService.findAllOrderItems();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple orderItem");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple categories");

        verify(orderItemConverter, times(10)).entityToResponse(any(OrderItemEntity.class));
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllOrderItems - Busca bem-sucedida retorna lista de itens de pedidos vazia")
    void findAllCategories_SuccessfulSearch_ReturnsEmptyList() {
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
}
