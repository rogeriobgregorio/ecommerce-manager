package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.StockMovementServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeFunction;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeProcedure;
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
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceImplTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private InventoryItemService inventoryItemService;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    private static StockMovement stockMovement;
    private static StockMovementRequest stockMovementRequest;
    private static StockMovementResponse stockMovementResponse;

    @BeforeEach
    void setUp() {

        ProductDiscount productDiscount = new ProductDiscount(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        Category category = new Category(1L, "Computers");
        Set<Category> categoryList = Set.of(category);

        Product product = Product.newBuilder()
                .withId(1L).withName("Intel i5-10400F").withDescription("Intel Core Processor")
                .withPrice(BigDecimal.valueOf(579.99)).withCategories(categoryList)
                .withImgUrl("https://example.com/i5-10400F.jpg")
                .withProductDiscount(productDiscount)
                .build();

        InventoryItem inventoryItem = InventoryItem.newBuilder()
                .withId(1L)
                .withQuantitySold(1)
                .withStockStatus(StockStatus.AVAILABLE)
                .withProduct(product)
                .withQuantityInStock(10)
                .build();

        stockMovement = StockMovement.newBuilder()
                .withId(1L)
                .withInventoryItem(inventoryItem)
                .withMovementType(MovementType.ENTRANCE)
                .withQuantityMoved(10)
                .withMoment(Instant.now())
                .build();

        MockitoAnnotations.openMocks(this);
        stockMovementService = new StockMovementServiceImpl(stockMovementRepository, inventoryItemService, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllStockMovements - Busca bem-sucedida retorna movimentações do estoque")
    void findAllStockMovements_SuccessfulSearch_ReturnsStockMovementsList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<StockMovement> stockMovementList = Collections.singletonList(stockMovement);
        List<StockMovementResponse> expectedResponses = Collections.singletonList(stockMovementResponse);
        PageImpl<StockMovement> page = new PageImpl<>(stockMovementList, pageable, stockMovementList.size());

        when(dataMapper.map(stockMovement, StockMovementResponse.class)).thenReturn(stockMovementResponse);
        when(stockMovementRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> stockMovementRepository.findAll(pageable));

        // Act
        Page<StockMovementResponse> actualResponse = stockMovementService.findAllStockMovements(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(stockMovement, StockMovementResponse.class);
        verify(stockMovementRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }
}
