package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.InventoryItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.InventoryItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.StockStatus;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.InventoryItemRepository;
import com.rogeriogregorio.ecommercemanager.repositories.StockMovementRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.InventoryItemServiceImpl;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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
    private static InventoryItemRequest inventoryItemRequest;
    private static InventoryItemResponse inventoryItemResponse;
    private static Product product;

    @BeforeEach
    void setUp() {

        Category category = new Category(1L, "Computers");
        Set<Category> categoryList = new HashSet<>();
        categoryList.add(category);

        ProductDiscount productDiscount = new ProductDiscount(1L,
                "Dia das Mães", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-01T00:00:00Z"),
                Instant.parse("2024-06-07T00:00:00Z"));

        product = Product.newBuilder()
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

        inventoryItemRequest = new InventoryItemRequest(1L, 10, StockStatus.AVAILABLE);

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
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> inventoryItemRepository.findAll(pageable));

        // Act
        Page<InventoryItemResponse> actualResponse = inventoryItemService.findAllInventoryItems(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(inventoryItem, InventoryItemResponse.class);
        verify(inventoryItemRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllInventoryItems - Exceção no repositório tentar buscar itens do inventário")
    void findAllInventoryItems_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(inventoryItemRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> inventoryItemRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> inventoryItemService.findAllInventoryItems(pageable),
                "Expected RepositoryException to be thrown");
        verify(inventoryItemRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createInventoryItem - Criação bem-sucedida retorna item do inventário criado")
    void createInventoryItem_SuccessfulCreation_ReturnsInventoryItem() {
        // Arrange
        InventoryItemResponse expectedResponse = inventoryItemResponse;

        when(productService.getProductIfExists(product.getId())).thenReturn(product);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> {
            SafeFunction<?> safeFunction = invocation.getArgument(0);
            Object result = safeFunction.execute();
            if (result instanceof Boolean) {
                return result;
            } else if (result instanceof InventoryItem) {
                return result;
            } else if (result instanceof StockMovement) {
                return result;
            }
            return null;
        });
        when(inventoryItemRepository.existsByProduct(product)).thenReturn(false);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> {
            InventoryItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(dataMapper.map(inventoryItem, InventoryItemResponse.class)).thenReturn(expectedResponse);

        // Act
        InventoryItemResponse actualResponse = inventoryItemService.createInventoryItem(inventoryItemRequest);

        // Assert
        assertNotNull(actualResponse, "Inventory item should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(inventoryItemRepository, times(1)).save(any(InventoryItem.class));
        verify(dataMapper, times(1)).map(inventoryItem, InventoryItemResponse.class);
        verify(productService, times(1)).getProductIfExists(product.getId());
        verify(catchError, times(3)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createInventoryItem - Exceção no repositório ao tentar item do inventário criado")
    void createInventoryItem_RepositoryExceptionHandling() {
        // Arrange
        when(productService.getProductIfExists(product.getId())).thenReturn(product);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> {
            SafeFunction<?> safeFunction = invocation.getArgument(0);
            Object result = safeFunction.execute();
            if (result instanceof Boolean) {
                return result;
            } else if (result instanceof InventoryItem) {
                return result;
            } else if (result instanceof StockMovement) {
                return result;
            }
            return null;
        });
        when(inventoryItemRepository.existsByProduct(product)).thenReturn(false);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenThrow(RepositoryException.class);

        // Assert
        assertThrows(RepositoryException.class, () -> inventoryItemService.createInventoryItem(inventoryItemRequest),
                "Expected RepositoryException to be thrown");
        verify(inventoryItemRepository, times(1)).save(any(InventoryItem.class));
        verify(productService, times(1)).getProductIfExists(product.getId());
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findInventoryItemById - Busca bem-sucedida retorna item do inventário")
    void findInventoryItemById_SuccessfulSearch_ReturnsInventoryItem() {
        // Arrange
        InventoryItemResponse expectedResponse = inventoryItemResponse;

        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));
        when(dataMapper.map(inventoryItem, InventoryItemResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> inventoryItemRepository.findById(inventoryItem.getId()));

        // Act
        InventoryItemResponse actualResponse = inventoryItemService.findInventoryItemById(inventoryItem.getId());

        // Assert
        assertNotNull(actualResponse, "Inventory item should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(dataMapper, times(1)).map(inventoryItem, InventoryItemResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findInventoryItemById - Exceção ao tentar buscar item do inventário inexistente")
    void findInventoryItemById_NotFoundExceptionHandling() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> inventoryItemRepository.findById(inventoryItem.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () ->inventoryItemService.findInventoryItemById(inventoryItem.getId()),
                "Expected NotFoundException to be thrown");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findInventoryItemById - Exceção no repositório ao tentar buscar item do inventário")
    void findInventoryItemById_RepositoryExceptionHandling() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> inventoryItemRepository.findById(inventoryItem.getId()));

        // Assert and Assert
        assertThrows(RepositoryException.class, () -> inventoryItemService.findInventoryItemById(inventoryItem.getId()),
                "Expected RepositoryException to be thrown");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateInventoryItem - Atualização bem-sucedida retorna item do inventário atualizado")
    void updateInventoryItem_SuccessfulUpdate_ReturnsInventoryItem() {
        // Arrange
        InventoryItemResponse expectedResponse = inventoryItemResponse;

        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(inventoryItemRepository.save(inventoryItem)).thenReturn(inventoryItem);
        when(dataMapper.map(eq(inventoryItem), eq(InventoryItemResponse.class))).thenReturn(expectedResponse);

        // Act
        InventoryItemResponse actualResponse = inventoryItemService.updateInventoryItem(inventoryItem.getId(), inventoryItemRequest);

        // Assert
        assertNotNull(actualResponse, "Inventory item should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
        verify(dataMapper, times(1)).map(eq(inventoryItem), eq(InventoryItemResponse.class));
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateInventoryItem - Exceção ao tentar atualizar item do inventário inexistente")
    void updateInventoryItem_NotFoundExceptionHandling() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> inventoryItemRepository.findById(inventoryItem.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> inventoryItemService.updateInventoryItem(inventoryItem.getId(), inventoryItemRequest),
                "Expected NotFoundException to be thrown");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(inventoryItemRepository, never()).save(inventoryItem);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateInventoryItem - Exceção no repositório ao tentar atualizar item do inventário")
    void updateInventoryItem_RepositoryExceptionHandling() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(inventoryItemRepository.save(inventoryItem)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> inventoryItemService.updateInventoryItem(inventoryItem.getId(), inventoryItemRequest),
                "Expected RepositoryException to be thrown");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteInventoryItem - Exclusão bem-sucedida do item do inventário")
    void deleteInventoryItem_DeletesInventoryItemSuccessfully() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> inventoryItemRepository.findById(inventoryItem.getId()));
        doAnswer(invocation -> {
            inventoryItemRepository.delete(inventoryItem);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doNothing().when(inventoryItemRepository).delete(inventoryItem);

        // Act
        inventoryItemService.deleteInventoryItem(inventoryItem.getId());

        // Assert
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(inventoryItemRepository, times(1)).delete(inventoryItem);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteInventoryItem - Exceção ao tentar excluir item do inventário inexistente")
    void deleteInventoryItem_NotFoundExceptionHandling() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> inventoryItemRepository.findById(inventoryItem.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> inventoryItemService.deleteInventoryItem(inventoryItem.getId()),
                "Expected NotFoundException to be thrown");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(inventoryItemRepository, never()).delete(inventoryItem);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteInventoryItem - Exceção no repositório ao tentar excluir item do inventário")
    void deleteInventoryItem_RepositoryExceptionHandling() {
        // Arrange
        when(inventoryItemRepository.findById(inventoryItem.getId())).thenReturn(Optional.of(inventoryItem));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> inventoryItemRepository.findById(inventoryItem.getId()));
        doAnswer(invocation -> {
            inventoryItemRepository.delete(inventoryItem);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doThrow(RepositoryException.class).when(inventoryItemRepository).delete(inventoryItem);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> inventoryItemService.deleteInventoryItem(inventoryItem.getId()),
                "Expected RepositoryException to be thrown");
        verify(inventoryItemRepository, times(1)).findById(inventoryItem.getId());
        verify(inventoryItemRepository, times(1)).delete(inventoryItem);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }
}