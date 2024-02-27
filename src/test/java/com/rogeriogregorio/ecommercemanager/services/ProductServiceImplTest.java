package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.ProductServiceImpl;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private Converter converter;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productRepository, categoryService, converter);
    }

    @Test
    @DisplayName("findAllProducts - Busca bem-sucedida retorna lista contendo um produto")
    void findAllProducts_SuccessfulSearch_ReturnsListResponse_OneProduct() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Video games");
        List<CategoryEntity> categoryEntityList = Collections.singletonList(categoryEntity);

        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        productEntity.getCategories().addAll(categoryEntityList);
        List<ProductEntity> productEntityList = Collections.singletonList(productEntity);

        ProductResponse productResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<ProductResponse> expectedResponses = Collections.singletonList(productResponse);

        when(converter.toResponse(productEntity, ProductResponse.class)).thenReturn(productResponse);
        when(productRepository.findAll()).thenReturn(productEntityList);

        // Act
        List<ProductResponse> actualResponses = productService.findAllProducts();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one product");
        assertEquals(expectedResponses.get(0).getCategories(), actualResponses.get(0).getCategories(), "Expected a list of responses with one product");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one product");

        verify(converter, times(1)).toResponse(productEntity, ProductResponse.class);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllProducts - Busca bem-sucedida retorna lista contendo múltiplos produtos")
    void findAllProducts_SuccessfulSearch_ReturnsListResponse_MultipleProducts() {
        // Arrange
        List<ProductEntity> productEntityList = new ArrayList<>();
        List<ProductResponse> expectedResponses = new ArrayList<>();

        for (long i = 1; i <= 10; i++) {
            ProductEntity productEntity = new ProductEntity(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            productEntityList.add(productEntity);

            ProductResponse productResponse = new ProductResponse(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            expectedResponses.add(productResponse);

            when(converter.toResponse(productEntity, ProductResponse.class)).thenReturn(productResponse);
        }

        when(productRepository.findAll()).thenReturn(productEntityList);

        // Act
        List<ProductResponse> actualResponses = productService.findAllProducts();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one product");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one product");

        verify(converter, times(10)).toResponse(any(ProductEntity.class), eq(ProductResponse.class));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllProducts - Busca bem-sucedida retorna lista de produtos vazia")
    void findAllProducts_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<ProductEntity> emptyProductEntityList = Collections.emptyList();

        when(productRepository.findAll()).thenReturn(emptyProductEntityList);

        // Act
        List<ProductResponse> actualResponses = productService.findAllProducts();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyProductEntityList, actualResponses, "Expected an empty list of responses");
        
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllProducts - Exceção ao tentar buscar lista de produtos")
    void findAllProducts_RepositoryExceptionHandling() {
        // Arrange
        when(productRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.findAllProducts());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createProduct - Criação bem-sucedida retorna produto criado")
    void createProduct_SuccessfulCreation_ReturnsProductResponse() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Video games");
        List<CategoryEntity> categoryEntityList = Collections.singletonList(categoryEntity);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        productEntity.getCategories().addAll(categoryEntityList);
        ProductResponse expectedResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(converter.toEntity(productRequest, ProductEntity.class)).thenReturn(productEntity);
        when(categoryService.findAllCategoryById(categoryListId)).thenReturn(categoryEntityList);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(converter.toResponse(productEntity, ProductResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = productService.createProduct(productRequest);

        // Assert
        assertNotNull(actualResponse, "ProductResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toEntity(productRequest, ProductEntity.class);
        verify(categoryService, times(1)).findAllCategoryById(categoryListId);
        verify(productRepository, times(1)).save(productEntity);
        verify(converter, times(1)).toResponse(productEntity, ProductResponse.class);
    }
}