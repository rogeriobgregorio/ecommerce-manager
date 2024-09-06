package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.StockMovementRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.StockMovementResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.MovementType;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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
    private static InventoryItem inventoryItem;
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

        inventoryItem = InventoryItem.newBuilder()
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

        stockMovementRequest = new StockMovementRequest(1L, MovementType.ENTRANCE, 10);

        stockMovementResponse = new StockMovementResponse(1L, Instant.now(), inventoryItem, MovementType.ENTRANCE, 10);

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

    @Test
    @DisplayName("findAllStockMovements - Exceção no repositório tentar buscar lista de movimentações do estoque")
    void findAllStockMovements_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(stockMovementRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> stockMovementRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> stockMovementService.findAllStockMovements(pageable),
                "Expected RepositoryException to be thrown");
        verify(stockMovementRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createStockMovement - Criação bem-sucedida retorna movimentação do estoque criada")
    void createStockMovement_SuccessfulCreation_ReturnsStockMovement() {
        // Arrange
        StockMovementResponse expectedResponse = stockMovementResponse;

        when(inventoryItemService.getInventoryItemIfExists(stockMovementRequest.getInventoryItemId())).thenReturn(inventoryItem);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> stockMovementRepository.save(stockMovement));
        when(stockMovementRepository.save(stockMovement)).thenReturn(stockMovement);
        when(dataMapper.map(stockMovement, StockMovementResponse.class)).thenReturn(expectedResponse);

        // Act
        StockMovementResponse actualResponse = stockMovementService.createStockMovement(stockMovementRequest);

        // Assert
        assertNotNull(actualResponse, "Stock movement should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(inventoryItemService, times(1)).getInventoryItemIfExists(stockMovementRequest.getInventoryItemId());
        verify(stockMovementRepository, times(1)).save(stockMovement);
        verify(dataMapper, times(1)).map(stockMovement, StockMovementResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createStockMovement - Exceção no repositório ao tentar criar movimentação do estoque")
    void createStockMovement_RepositoryExceptionHandling() {
        // Arrange
        when(inventoryItemService.getInventoryItemIfExists(stockMovementRequest.getInventoryItemId())).thenReturn(inventoryItem);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> stockMovementRepository.save(stockMovement));
        when(stockMovementRepository.save(stockMovement)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> stockMovementService.createStockMovement(stockMovementRequest),
                "Expected RepositoryException to be thrown");
        verify(inventoryItemService, times(1)).getInventoryItemIfExists(stockMovementRequest.getInventoryItemId());
        verify(stockMovementRepository, times(1)).save(stockMovement);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findStockMovementById - Busca bem-sucedida retorna movimentação do estoque")
    void findStockMovementById_SuccessfulSearch_ReturnsAddress() {
        // Arrange
        StockMovementResponse expectedResponse = stockMovementResponse;

        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.of(stockMovement));
        when(dataMapper.map(stockMovement, StockMovementResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> stockMovementRepository.findById(stockMovement.getId()));

        // Act
        StockMovementResponse actualResponse = stockMovementService.findStockMovementById(stockMovement.getId());

        // Assert
        assertNotNull(actualResponse, "Stock movement should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(dataMapper, times(1)).map(stockMovement, StockMovementResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findStockMovementById - Exceção ao tentar buscar movimentação do estoque inexistente")
    void findStockMovementById_NotFoundExceptionHandling() {
        // Arrange
        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> stockMovementRepository.findById(stockMovement.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> stockMovementService.findStockMovementById(stockMovement.getId()),
                "Expected NotFoundException to be thrown");
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findStockMovementById - Exceção no repositório ao tentar buscar movimentação do estoque")
    void findStockMovementById_RepositoryExceptionHandling() {
        // Arrange
        when(stockMovementRepository.findById(stockMovement.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> stockMovementRepository.findById(stockMovement.getId()));

        // Assert and Assert
        assertThrows(RepositoryException.class, () -> stockMovementService.findStockMovementById(stockMovement.getId()),
                "Expected RepositoryException to be thrown");
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateStockMovement - Atualização bem-sucedida retorna movimentação do estoque atualizada")
    void updateStockMovement_SuccessfulUpdate_ReturnsStockMovement() {
        // Arrange
        StockMovementResponse expectedResponse = stockMovementResponse;

        when(inventoryItemService.getInventoryItemIfExists(stockMovementRequest.getInventoryItemId())).thenReturn(inventoryItem);
        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.of(stockMovement));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(stockMovementRepository.save(stockMovement)).thenReturn(stockMovement);
        when(dataMapper.map(eq(stockMovement), eq(StockMovementResponse.class))).thenReturn(expectedResponse);

        // Act
        StockMovementResponse actualResponse = stockMovementService.updateStockMovement(stockMovement.getId(), stockMovementRequest);

        // Assert
        assertNotNull(actualResponse, "Stock movement should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(inventoryItemService, times(1)).getInventoryItemIfExists(stockMovementRequest.getInventoryItemId());
        verify(stockMovementRepository, times(1)).save(stockMovement);
        verify(dataMapper, times(1)).map(eq(stockMovement), eq(StockMovementResponse.class));
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateStockMovement - Exceção ao tentar atualizar movimentação do estoque inexistente")
    void updateStockMovement_NotFoundExceptionHandling() {
        // Arrange
        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> stockMovementService.updateStockMovement(stockMovement.getId(), stockMovementRequest),
                "Expected NotFoundException to be thrown");
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(stockMovementRepository, never()).save(stockMovement);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateStockMovement - Exceção no repositório ao tentar atualizar movimentação do estoque")
    void updateStockMovement_RepositoryExceptionHandling() {
        // Arrange
        when(inventoryItemService.getInventoryItemIfExists(stockMovementRequest.getInventoryItemId())).thenReturn(inventoryItem);
        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.of(stockMovement));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(stockMovementRepository.save(stockMovement)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> stockMovementService.updateStockMovement(stockMovement.getId(), stockMovementRequest),
                "Expected RepositoryException to be thrown");
        verify(inventoryItemService, times(1)).getInventoryItemIfExists(stockMovementRequest.getInventoryItemId());
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(stockMovementRepository, times(1)).save(stockMovement);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteStockMovement - Exclusão bem-sucedida da movimentação do estoque")
    void deleteStockMovement_DeletesAddressSuccessfully() {
        // Arrange
        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.of(stockMovement));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> stockMovementRepository.findById(stockMovement.getId()));
        doAnswer(invocation -> {
            stockMovementRepository.delete(stockMovement);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doNothing().when(stockMovementRepository).delete(stockMovement);

        // Act
        stockMovementService.deleteStockMovement(stockMovement.getId());

        // Assert
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(stockMovementRepository, times(1)).delete(stockMovement);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteStockMovement - Exceção ao tentar excluir movimentação do estoque inexistente")
    void deleteStockMovement_NotFoundExceptionHandling() {
        // Arrange
        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> stockMovementRepository.findById(stockMovement.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> stockMovementService.deleteStockMovement(stockMovement.getId()),
                "Expected NotFoundException to be thrown");
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(stockMovementRepository, never()).delete(stockMovement);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteStockMovement - Exceção no repositório ao tentar excluir movimentação do estoque")
    void deleteStockMovement_RepositoryExceptionHandling() {
        // Arrange
        when(stockMovementRepository.findById(stockMovement.getId())).thenReturn(Optional.of(stockMovement));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> stockMovementRepository.findById(stockMovement.getId()));
        doAnswer(invocation -> {
            stockMovementRepository.delete(stockMovement);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doThrow(RepositoryException.class).when(stockMovementRepository).delete(stockMovement);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> stockMovementService.deleteStockMovement(stockMovement.getId()),
                "Expected RepositoryException to be thrown");
        verify(stockMovementRepository, times(1)).findById(stockMovement.getId());
        verify(stockMovementRepository, times(1)).delete(stockMovement);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }
}
