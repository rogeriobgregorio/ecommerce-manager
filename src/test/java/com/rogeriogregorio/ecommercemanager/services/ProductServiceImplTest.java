package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
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
import java.util.Optional;

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

    @Test
    @DisplayName("createProduct - Exceção no repositório ao tentar criar produto")
    void createProduct_RepositoryExceptionHandling() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Video games");
        List<CategoryEntity> categoryEntityList = Collections.singletonList(categoryEntity);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        productEntity.getCategories().addAll(categoryEntityList);

        when(converter.toEntity(productRequest, ProductEntity.class)).thenReturn(productEntity);
        when(categoryService.findAllCategoryById(categoryListId)).thenReturn(categoryEntityList);
        when(productRepository.save(productEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.createProduct(productRequest));

        verify(converter, times(1)).toEntity(productRequest, ProductEntity.class);
        verify(categoryService, times(1)).findAllCategoryById(categoryListId);
        verify(productRepository, times(1)).save(productEntity);
    }

    @Test
    @DisplayName("findProductById - Busca bem-sucedida retorna produto")
    void findProductById_SuccessfulSearch_ReturnsProductResponse() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Video games");
        List<CategoryEntity> categoryEntityList = Collections.singletonList(categoryEntity);

        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        productEntity.getCategories().addAll(categoryEntityList);

        ProductResponse expectedResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(converter.toResponse(productEntity, ProductResponse.class)).thenReturn(expectedResponse);
        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        // Act
        ProductResponse actualResponse = productService.findProductById(1L);

        // Assert
        assertNotNull(actualResponse, "ProductResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(productEntity, ProductResponse.class);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findProductById - Exceção ao tentar buscar produto inexistente")
    void findProductById_NotFoundExceptionHandling() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productService.findProductById(1L));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateProduct - Atualização bem-sucedida retorna produto atualizado")
    void updateProduct_SuccessfulUpdate_ReturnsProductResponse() {
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
        when(productRepository.findById(productEntity.getId())).thenReturn(Optional.of(productEntity));
        when(categoryService.findAllCategoryById(categoryListId)).thenReturn(categoryEntityList);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(converter.toResponse(productEntity, ProductResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = productService.updateProduct(productRequest);

        // Assert
        assertNotNull(actualResponse, "ProductResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual response should match");

        verify(converter, times(1)).toEntity(productRequest, ProductEntity.class);
        verify(productRepository, times(1)).findById(productEntity.getId());
        verify(categoryService, times(1)).findAllCategoryById(categoryListId);
    }

    @Test
    @DisplayName("updateProduct - Exceção ao tentar atualizar produto inexistente")
    void updateProduto_NotFoundExceptionHandling() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Video games");
        List<CategoryEntity> categoryEntityList = Collections.singletonList(categoryEntity);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        productEntity.getCategories().addAll(categoryEntityList);

        when(converter.toEntity(productRequest, ProductEntity.class)).thenReturn(productEntity);
        when(productRepository.findById(productEntity.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(productRequest));

        verify(converter, times(1)).toEntity(productRequest, ProductEntity.class);
        verify(productRepository, times(1)).findById(productEntity.getId());
    }

    @Test
    @DisplayName("updateProduto - Exceção no repositório ao tentar atualizar produto")
    void updateProduto_RepositoryExceptionHandling() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Video games");
        List<CategoryEntity> categoryEntityList = Collections.singletonList(categoryEntity);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        productEntity.getCategories().addAll(categoryEntityList);

        when(converter.toEntity(productRequest, ProductEntity.class)).thenReturn(productEntity);
        when(productRepository.findById(productEntity.getId())).thenReturn(Optional.of(productEntity));
        when(categoryService.findAllCategoryById(categoryListId)).thenReturn(categoryEntityList);
        when(productRepository.save(productEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.updateProduct(productRequest));

        verify(converter, times(1)).toEntity(productRequest, ProductEntity.class);
        verify(productRepository, times(1)).findById(productEntity.getId());
        verify(categoryService, times(1)).findAllCategoryById(categoryListId);
    }

    @Test
    @DisplayName("deleteProduct - Exclusão bem-sucedida do produto")
    void deleteProduct_DeletesSuccessfully() {
        // Arrange
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct - Exceção ao tentar excluir produto inexistente")
    void deleteProduct_NotFoundExceptionHandling() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deleteProduct - Exceção no repositório ao tentar excluir produto")
    void deleteProduct_RepositoryExceptionHandling() {
        // Arrange
        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        doThrow(PersistenceException.class).when(productRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.deleteProduct(1L));

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findProductByName - Busca bem-sucedida pelo nome retorna lista contendo um produto")
    void findProductByName_SuccessfulSearch_ReturnsListResponse_OneProduct() {
        // Arrange
        String productName = "Playstation 5";

        ProductEntity productEntity = new ProductEntity(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<ProductEntity> productEntityList = Collections.singletonList(productEntity);

        ProductResponse productResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<ProductResponse> expectedResponses = Collections.singletonList(productResponse);

        when(productRepository.findByName(eq(productName))).thenReturn(productEntityList);
        when(converter.toResponse(eq(productEntity), eq(ProductResponse.class))).thenReturn(productResponse);

        // Act
        List<ProductResponse> actualResponses = productService.findProductByName("Playstation 5");

        // Assert
        assertNotNull(actualResponses, "ProductResponses should not be null");
        assertEquals(expectedResponses, actualResponses, "Size of ProductResponses should match size of ProductEntities");
        assertEquals(productName, actualResponses.get(0).getName(), "Names should match");

        verify(productRepository, times(1)).findByName(eq(productName));
        verify(converter, times(1)).toResponse(eq(productEntity), eq(ProductResponse.class));
    }

    @Test
    @DisplayName("findProductByName - Busca bem-sucedida pelo nome retorna lista contendo múltiplos produtos")
    void findProductByName_SuccessfulSearch_ReturnsListResponse_MultipleProducts() {
        // Arrange
        String productName = "Playstation 5";

        List<ProductEntity> productEntityList = new ArrayList<>();
        List<ProductResponse> expectedResponses = new ArrayList<>();

        for (long i = 1; i <= 10; i++) {
            ProductEntity productEntity = new ProductEntity(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            productEntityList.add(productEntity);

            ProductResponse productResponse = new ProductResponse(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            expectedResponses.add(productResponse);

            when(converter.toResponse(eq(productEntity), eq(ProductResponse.class))).thenReturn(productResponse);
        }

        when(productRepository.findByName(eq(productName))).thenReturn(productEntityList);

        // Act
        List<ProductResponse> actualResponses = productService.findProductByName("Playstation 5");

        // Assert
        assertNotNull(actualResponses, "ProductResponses should not be null");
        assertEquals(expectedResponses, actualResponses, "Size of ProductResponses should match size of ProductEntities");

        verify(productRepository, times(1)).findByName(eq(productName));
        verify(converter, times(10)).toResponse(any(ProductEntity.class), eq(ProductResponse.class));
    }

    @Test
    @DisplayName("findProductByName - Exceção ao tentar buscar produto inexistente pelo nome")
    void findProductByName_NotFoundExceptionHandling() {
        // Arrange
        String productName = "Inexistente";

        when(productRepository.findByName(productName)).thenReturn(Collections.emptyList());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productService.findProductByName("Inexistente"), "Expected NotFoundException for non-existent product");

        verify(productRepository, times(1)).findByName("Inexistente");
    }

    @Test
    @DisplayName("findProductByName - Exceção no repositório ao tentar buscar produto pelo nome")
    void findProductByName_RepositoryExceptionHandling() {
        // Arrange
        String productName = "Erro";

        when(productRepository.findByName(productName)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.findProductByName("Erro"), "Expected RepositoryException for repository error");
    }
}