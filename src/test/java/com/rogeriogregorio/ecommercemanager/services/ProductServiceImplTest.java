package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.ProductServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
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
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<ProductEntity> productEntityList = Collections.singletonList(productEntity);

        ProductResponse productResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<ProductResponse> expectedResponses = Collections.singletonList(productResponse);

        when(converter.toResponse(productEntity, ProductResponse.class)).thenReturn(productResponse);
        when(productRepository.findAll()).thenReturn(productEntityList);

        // Act
        List<ProductResponse> actualResponses = productService.findAllProducts();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one product");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one product");

        verify(converter, times(1)).toResponse(productEntity, ProductResponse.class);
        verify(productRepository, times(1)).findAll();
    }
}