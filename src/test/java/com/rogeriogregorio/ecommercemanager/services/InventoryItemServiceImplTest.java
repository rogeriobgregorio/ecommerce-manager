package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.entities.InventoryItem;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.repositories.InventoryItemRepository;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.InventoryItemServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryItemServiceImplTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private InventoryItemServiceImpl inventoryItemService;

    private static InventoryItem inventoryItem;
    private static InventoryItemResponse inventoryItemResponse;

    @BeforeEach
    void setUp() {

        Category category = new Category(1L, "Computers");
        Set<Category> categoryList = new HashSet<>();
        categoryList.add(category);

        ProductDiscount productDiscount = new ProductDiscount(1L,
                "Dia das Mães", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-01T00:00:00Z"),
                Instant.parse("2024-06-07T00:00:00Z"));

        Product product = Product.newBuilder()
                .withId(1L).withName("Intel i5-10400F").withDescription("Intel Core Processor")
                .withPrice(BigDecimal.valueOf(579.99)).withCategories(categoryList)
                .withImgUrl("https://example.com/i5-10400F.jpg")
                .withProductDiscount(productDiscount)
                .build();

        inventoryItem = InventoryItem.newBuilder()
                .withId(1L)
                .withQuantitySold(1)
                .withStockStatus(StockStatus.AVAILABLE)
                .withProduct(product)
                .withQuantityInStock(10)
                .build();

        inventoryItemResponse = new InventoryItemResponse(1L, product, 10, 1, 1);

        MockitoAnnotations.openMocks(this);
        inventoryItemService = new InventoryItemServiceImpl(
                inventoryItemRepository, stockMovementRepository,
                productService, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllInventoryItems - Busca bem-sucedida retorna lista de itens do inventário")
    void findAllInventoryItems_SuccessfulSearch_ReturnsInventoryItemList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<InventoryItem> inventoryItemList = Collections.singletonList(inventoryItem);
        List<InventoryItemResponse> expectedResponses = Collections.singletonList(inventoryItemResponse);
        PageImpl<InventoryItem> page = new PageImpl<>(inventoryItemList, pageable, inventoryItemList.size());

        when(dataMapper.map(inventoryItem, InventoryItemResponse.class)).thenReturn(inventoryItemResponse);
        when(inventoryItemRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> inventoryItemRepository.findAll(pageable));

        // Act
        Page<InventoryItemResponse> actualResponse = inventoryItemService.findAllInventoryItems(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(inventoryItem, InventoryItemResponse.class);
        verify(inventoryItemRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }
}