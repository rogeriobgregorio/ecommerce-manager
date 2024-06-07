package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.ProductServiceImpl;
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
class ProductServiceImplTest {

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
        productService = new ProductServiceImpl(productRepository, categoryService, productDiscountService, converter, mapper);
    }

    @Test
    @DisplayName("findAllProducts - Busca bem-sucedida retorna lista contendo um produto")
    void findAllProducts_SuccessfulSearch_ReturnsListResponse_OneProduct() {
        // Arrange
        Category category = new Category(1L, "Video games");
        List<Category> categoryList = Collections.singletonList(category);

        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        product.getCategories().addAll(categoryList);
        List<Product> productList = Collections.singletonList(product);

        ProductResponse productResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<ProductResponse> expectedResponses = Collections.singletonList(productResponse);

        when(converter.toResponse(product, ProductResponse.class)).thenReturn(productResponse);
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<ProductResponse> actualResponses = productService.findAllProducts();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one product");
        assertEquals(expectedResponses.get(0).getCategories(), actualResponses.get(0).getCategories(), "Expected a list of responses with one product");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one product");

        verify(converter, times(1)).toResponse(product, ProductResponse.class);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllProducts - Busca bem-sucedida retorna lista contendo múltiplos produtos")
    void findAllProducts_SuccessfulSearch_ReturnsListResponse_MultipleProducts() {
        // Arrange
        List<Product> productList = new ArrayList<>();
        List<ProductResponse> expectedResponses = new ArrayList<>();

        for (long i = 1; i <= 10; i++) {
            Product product = new Product(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            productList.add(product);

            ProductResponse productResponse = new ProductResponse(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            expectedResponses.add(productResponse);

            when(converter.toResponse(product, ProductResponse.class)).thenReturn(productResponse);
        }

        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<ProductResponse> actualResponses = productService.findAllProducts();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one product");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one product");

        verify(converter, times(10)).toResponse(any(Product.class), eq(ProductResponse.class));
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllProducts - Busca bem-sucedida retorna lista de produtos vazia")
    void findAllProducts_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<Product> emptyProductList = Collections.emptyList();

        when(productRepository.findAll()).thenReturn(emptyProductList);

        // Act
        List<ProductResponse> actualResponses = productService.findAllProducts();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyProductList, actualResponses, "Expected an empty list of responses");

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
        Category category = new Category(1L, "Video games");
        List<Category> categoryList = Collections.singletonList(category);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        product.getCategories().addAll(categoryList);
        ProductResponse expectedResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(converter.toEntity(productRequest, Product.class)).thenReturn(product);
        when(categoryService.findAllCategoriesByIds(categoryListId)).thenReturn(categoryList);
        when(productRepository.save(product)).thenReturn(product);
        when(converter.toResponse(product, ProductResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = productService.createProduct(productRequest);

        // Assert
        assertNotNull(actualResponse, "ProductResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toEntity(productRequest, Product.class);
        verify(categoryService, times(1)).findAllCategoriesByIds(categoryListId);
        verify(productRepository, times(1)).save(product);
        verify(converter, times(1)).toResponse(product, ProductResponse.class);
    }

    @Test
    @DisplayName("createProduct - Exceção no repositório ao tentar criar produto")
    void createProduct_RepositoryExceptionHandling() {
        // Arrange
        Category category = new Category(1L, "Video games");
        List<Category> categoryList = Collections.singletonList(category);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        product.getCategories().addAll(categoryList);

        when(converter.toEntity(productRequest, Product.class)).thenReturn(product);
        when(categoryService.findAllCategoriesByIds(categoryListId)).thenReturn(categoryList);
        when(productRepository.save(product)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.createProduct(productRequest));

        verify(converter, times(1)).toEntity(productRequest, Product.class);
        verify(categoryService, times(1)).findAllCategoriesByIds(categoryListId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("findProductById - Busca bem-sucedida retorna produto")
    void findProductById_SuccessfulSearch_ReturnsProductResponse() {
        // Arrange
        Category category = new Category(1L, "Video games");
        List<Category> categoryList = Collections.singletonList(category);

        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        product.getCategories().addAll(categoryList);

        ProductResponse expectedResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(converter.toResponse(product, ProductResponse.class)).thenReturn(expectedResponse);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponse actualResponse = productService.findProductById(1L);

        // Assert
        assertNotNull(actualResponse, "ProductResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(product, ProductResponse.class);
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
        Category category = new Category(1L, "Video games");
        List<Category> categoryList = Collections.singletonList(category);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        product.getCategories().addAll(categoryList);
        ProductResponse expectedResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(converter.toEntity(productRequest, Product.class)).thenReturn(product);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryService.findAllCategoriesByIds(categoryListId)).thenReturn(categoryList);
        when(productRepository.save(product)).thenReturn(product);
        when(converter.toResponse(product, ProductResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductResponse actualResponse = productService.updateProduct(productRequest);

        // Assert
        assertNotNull(actualResponse, "ProductResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual response should match");

        verify(converter, times(1)).toEntity(productRequest, Product.class);
        verify(productRepository, times(1)).findById(product.getId());
        verify(categoryService, times(1)).findAllCategoriesByIds(categoryListId);
    }

    @Test
    @DisplayName("updateProduct - Exceção ao tentar atualizar produto inexistente")
    void updateProduto_NotFoundExceptionHandling() {
        // Arrange
        Category category = new Category(1L, "Video games");
        List<Category> categoryList = Collections.singletonList(category);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        product.getCategories().addAll(categoryList);

        when(productRepository.findById(productRequest.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(productRequest));

        verify(productRepository, times(1)).findById(productRequest.getId());
    }

    @Test
    @DisplayName("updateProduto - Exceção no repositório ao tentar atualizar produto")
    void updateProduto_RepositoryExceptionHandling() {
        // Arrange
        Category category = new Category(1L, "Video games");
        List<Category> categoryList = Collections.singletonList(category);

        List<Long> categoryListId = new ArrayList<>();
        categoryListId.add(1L);

        ProductRequest productRequest = new ProductRequest(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com", categoryListId);
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        product.getCategories().addAll(categoryList);

        when(converter.toEntity(productRequest, Product.class)).thenReturn(product);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(categoryService.findAllCategoriesByIds(categoryListId)).thenReturn(categoryList);
        when(productRepository.save(product)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.updateProduct(productRequest));

        verify(converter, times(1)).toEntity(productRequest, Product.class);
        verify(productRepository, times(1)).findById(product.getId());
        verify(categoryService, times(1)).findAllCategoriesByIds(categoryListId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("deleteProduct - Exclusão bem-sucedida do produto")
    void deleteProduct_DeletesSuccessfully() {
        // Arrange
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

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
        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doThrow(PersistenceException.class).when(productRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productService.deleteProduct(1L));

        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("findProductByName - Busca bem-sucedida pelo nome retorna lista contendo um produto")
    void findProductByName_SuccessfulSearch_ReturnsListResponse_OneProduct() {
        // Arrange
        String productName = "Playstation 5";

        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<Product> productList = Collections.singletonList(product);

        ProductResponse productResponse = new ProductResponse(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
        List<ProductResponse> expectedResponses = Collections.singletonList(productResponse);

        when(productRepository.findByName(eq(productName))).thenReturn(productList);
        when(converter.toResponse(eq(product), eq(ProductResponse.class))).thenReturn(productResponse);

        // Act
        List<ProductResponse> actualResponses = productService.findProductByName("Playstation 5");

        // Assert
        assertNotNull(actualResponses, "ProductResponses should not be null");
        assertEquals(expectedResponses, actualResponses, "Size of ProductResponses should match size of ProductEntities");
        assertEquals(productName, actualResponses.get(0).getName(), "Names should match");

        verify(productRepository, times(1)).findByName(eq(productName));
        verify(converter, times(1)).toResponse(eq(product), eq(ProductResponse.class));
    }

    @Test
    @DisplayName("findProductByName - Busca bem-sucedida pelo nome retorna lista contendo múltiplos produtos")
    void findProductByName_SuccessfulSearch_ReturnsListResponse_MultipleProducts() {
        // Arrange
        String productName = "Playstation 5";

        List<Product> productList = new ArrayList<>();
        List<ProductResponse> expectedResponses = new ArrayList<>();

        for (long i = 1; i <= 10; i++) {
            Product product = new Product(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            productList.add(product);

            ProductResponse productResponse = new ProductResponse(i, "Produto" + i, "Descrição" + i, 100.0 + i, "www.url" +i+ ".com");
            expectedResponses.add(productResponse);

            when(converter.toResponse(eq(product), eq(ProductResponse.class))).thenReturn(productResponse);
        }

        when(productRepository.findByName(eq(productName))).thenReturn(productList);

        // Act
        List<ProductResponse> actualResponses = productService.findProductByName("Playstation 5");

        // Assert
        assertNotNull(actualResponses, "ProductResponses should not be null");
        assertEquals(expectedResponses, actualResponses, "Size of ProductResponses should match size of ProductEntities");

        verify(productRepository, times(1)).findByName(eq(productName));
        verify(converter, times(10)).toResponse(any(Product.class), eq(ProductResponse.class));
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