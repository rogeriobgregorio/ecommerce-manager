package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductDiscountRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductDiscountResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductDiscountRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.ProductDiscountServiceImpl;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductDiscountServiceImplTest {

    @Mock
    private ProductDiscountRepository productDiscountRepository;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private ProductDiscountServiceImpl productDiscountService;

    private static ProductDiscount productDiscount;
    private static ProductDiscountRequest productDiscountRequest;
    private static ProductDiscountResponse productDiscountResponse;

    @BeforeEach
    void setUp() {

        productDiscount = new ProductDiscount(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        productDiscountRequest = new ProductDiscountRequest(
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        productDiscountResponse = new ProductDiscountResponse(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        MockitoAnnotations.openMocks(this);
        productDiscountService = new ProductDiscountServiceImpl(productDiscountRepository, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllProductDiscounts - Busca bem-sucedida retorna lista de desconto de produtos")
    void findAllProductDiscounts_SuccessfulSearch_ReturnsProductDiscountsList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDiscount> productDiscountList = Collections.singletonList(productDiscount);
        List<ProductDiscountResponse> expectedResponses = Collections.singletonList(productDiscountResponse);
        PageImpl<ProductDiscount> page = new PageImpl<>(productDiscountList, pageable, productDiscountList.size());

        when(dataMapper.map(productDiscount, ProductDiscountResponse.class)).thenReturn(productDiscountResponse);
        when(productDiscountRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productDiscountRepository.findAll(pageable));

        // Act
        Page<ProductDiscountResponse> actualResponse = productDiscountService.findAllProductDiscounts(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(productDiscount, ProductDiscountResponse.class);
        verify(productDiscountRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("findAllProductDiscounts - Exceção no repositório tentar buscar lista de desconto de produtos")
    void findAllProductDiscounts_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(productDiscountRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productDiscountRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productDiscountService.findAllProductDiscounts(pageable),
                "Expected RepositoryException to be thrown");
        verify(productDiscountRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("createProductDiscount - Criação bem-sucedida retorna desconto de produto criado")
    void createProductDiscount_SuccessfulCreation_ReturnsProductDiscount() {
        // Arrange
        ProductDiscountResponse expectedResponse = productDiscountResponse;

        when(dataMapper.map(productDiscountRequest, ProductDiscount.class)).thenReturn(productDiscount);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productDiscountRepository.save(productDiscount));
        when(productDiscountRepository.save(productDiscount)).thenReturn(productDiscount);
        when(dataMapper.map(productDiscount, ProductDiscountResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductDiscountResponse actualResponse = productDiscountService.createProductDiscount(productDiscountRequest);

        // Assert
        assertNotNull(actualResponse, "Product discount should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(productDiscountRequest, ProductDiscount.class);
        verify(productDiscountRepository, times(1)).save(productDiscount);
        verify(dataMapper, times(1)).map(productDiscount, ProductDiscountResponse.class);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("createProductDiscount - Exceção no repositório ao tentar criar desconto de produto")
    void createProductDiscount_RepositoryExceptionHandling() {
        // Arrange
        when(dataMapper.map(productDiscountRequest, ProductDiscount.class)).thenReturn(productDiscount);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productDiscountRepository.save(productDiscount));
        when(productDiscountRepository.save(productDiscount)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productDiscountService.createProductDiscount(productDiscountRequest),
                "Expected RepositoryException to be thrown");
        verify(dataMapper, times(1)).map(productDiscountRequest, ProductDiscount.class);
        verify(productDiscountRepository, times(1)).save(productDiscount);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("findProductDiscountById - Busca bem-sucedida retorna desconto de produto")
    void findProductDiscountById_SuccessfulSearch_ReturnsProductDiscount() {
        // Arrange
        ProductDiscountResponse expectedResponse = productDiscountResponse;

        when(productDiscountRepository.findById(productDiscount.getId())).thenReturn(Optional.of(productDiscount));
        when(dataMapper.map(productDiscount, ProductDiscountResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productDiscountRepository.findById(productDiscount.getId()));

        // Act
        ProductDiscountResponse actualResponse = productDiscountService.findProductDiscountById(productDiscount.getId());

        // Assert
        assertNotNull(actualResponse, "Product discount should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(productDiscountRepository, times(1)).findById(productDiscount.getId());
        verify(dataMapper, times(1)).map(productDiscount, ProductDiscountResponse.class);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("findProductDiscountById - Exceção ao tentar buscar desconto de produto inexistente")
    void findProductDiscountById_NotFoundExceptionHandling() {
        // Arrange
        when(productDiscountRepository.findById(productDiscount.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productDiscountRepository.findById(productDiscount.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productDiscountService.findProductDiscountById(productDiscount.getId()),
                "Expected NotFoundException to be thrown");
        verify(productDiscountRepository, times(1)).findById(productDiscount.getId());
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("findProductDiscountById - Exceção no repositório ao tentar buscar desconto de produto")
    void findProductDiscountById_RepositoryExceptionHandling() {
        // Arrange
        when(productDiscountRepository.findById(productDiscount.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productDiscountRepository.findById(productDiscount.getId()));

        // Assert and Assert
        assertThrows(RepositoryException.class, () -> productDiscountService.findProductDiscountById(productDiscount.getId()),
                "Expected RepositoryException to be thrown");
        verify(productDiscountRepository, times(1)).findById(productDiscount.getId());
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("updateProductDiscount - Atualização bem-sucedida retorna desconto de produto atualizado")
    void updateProductDiscount_SuccessfulUpdate_ReturnsProductDiscount() {
        // Arrange
        ProductDiscountResponse expectedResponse = productDiscountResponse;

        when(productDiscountRepository.findById(productDiscount.getId())).thenReturn(Optional.of(productDiscount));
        when(catchError.run(any(CatchError.SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, CatchError.SafeFunction.class).execute());
        when(dataMapper.map(eq(productDiscountRequest), any(ProductDiscount.class))).thenReturn(productDiscount);
        when(productDiscountRepository.save(productDiscount)).thenReturn(productDiscount);
        when(dataMapper.map(eq(productDiscount), eq(ProductDiscountResponse.class))).thenReturn(expectedResponse);

        // Act
        ProductDiscountResponse actualResponse = productDiscountService.updateProductDiscount(productDiscount.getId(), productDiscountRequest);

        // Assert
        assertNotNull(actualResponse, "Product discount should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(productDiscountRepository, times(1)).findById(productDiscount.getId());
        verify(dataMapper, times(1)).map(eq(productDiscountRequest), any(ProductDiscount.class));
        verify(productDiscountRepository, times(1)).save(productDiscount);
        verify(dataMapper, times(1)).map(eq(productDiscount), eq(ProductDiscountResponse.class));
        verify(catchError, times(2)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("updateProductDiscount - Exceção ao tentar atualizar desconto de produto inexistente")
    void updateProductDiscount_NotFoundExceptionHandling() {
        // Arrange
        when(productDiscountRepository.findById(productDiscount.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(CatchError.SafeFunction.class))).then(invocation -> productDiscountRepository.findById(productDiscount.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productDiscountService.updateProductDiscount(productDiscount.getId(), productDiscountRequest),
                "Expected NotFoundException to be thrown");
        verify(productDiscountRepository, times(1)).findById(productDiscount.getId());
        verify(productDiscountRepository, never()).save(productDiscount);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("updateProductDiscount - Exceção no repositório ao tentar atualizar desconto de produto")
    void updateProductDiscount_RepositoryExceptionHandling() {
        // Arrange
        when(productDiscountRepository.findById(productDiscount.getId())).thenReturn(Optional.of(productDiscount));
        when(catchError.run(any(CatchError.SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, CatchError.SafeFunction.class).execute());
        when(dataMapper.map(eq(productDiscountRequest), any(ProductDiscount.class))).thenReturn(productDiscount);
        when(productDiscountRepository.save(productDiscount)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productDiscountService.updateProductDiscount(productDiscount.getId(), productDiscountRequest),
                "Expected RepositoryException to be thrown");
        verify(productDiscountRepository, times(1)).findById(productDiscount.getId());
        verify(dataMapper, times(1)).map(eq(productDiscountRequest), any(ProductDiscount.class));
        verify(productDiscountRepository, times(1)).save(productDiscount);
        verify(catchError, times(2)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("deleteProductDiscount - Exclusão bem-sucedida do desconto de produto")
    void deleteProductDiscount_DeletesProductDiscountSuccessfully() {
        // Arrange
        when(productDiscountRepository.findById(productDiscount.getId())).thenReturn(Optional.of(productDiscount));
        when(catchError.run(any(CatchError.SafeFunction.class))).then(invocation -> productDiscountRepository.findById(productDiscount.getId()));
        doAnswer(invocation -> {
            productDiscountRepository.delete(productDiscount);
            return null;
        }).when(catchError).run(any(CatchError.SafeProcedure.class));
        doNothing().when(productDiscountRepository).delete(productDiscount);

        // Act
        productDiscountService.deleteProductDiscount(productDiscount.getId());

        // Assert
        verify(productDiscountRepository, times(1)).findById(productDiscount.getId());
        verify(productDiscountRepository, times(1)).delete(productDiscount);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
        verify(catchError, times(1)).run(any(CatchError.SafeProcedure.class));
    }
}
