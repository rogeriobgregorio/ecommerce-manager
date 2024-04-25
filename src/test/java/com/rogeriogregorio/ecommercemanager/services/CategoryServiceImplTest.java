package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.CategoryServiceImpl;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Converter converter;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryServiceImpl(categoryRepository, converter, errorHandler, mapper);
    }

    @Test
    @DisplayName("findAllCategories - Busca bem-sucedida retorna lista contendo uma categoria")
    void findAllCategories_SuccessfulSearch_ReturnsListResponse_OneCategory() {
        // Arrange
        Category category = new Category(1L, "Computers");
        List<Category> categoryList = Collections.singletonList(category);

        CategoryResponse categoryResponse = new CategoryResponse(1L, "Computers");
        List<CategoryResponse> expectedResponses = Collections.singletonList(categoryResponse);

        when(converter.toResponse(category, CategoryResponse.class)).thenReturn(categoryResponse);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<CategoryResponse> actualResponses = categoryService.findAllCategories();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one category");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one categories");

        verify(converter, times(1)).toResponse(category, CategoryResponse.class);
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllCategories - Busca bem-sucedida retorna lista contendo múltiplas categoria")
    void findAllCategories_SuccessfulSearch_ReturnsListResponse_MultipleCategories() {
        // Arrange
        List<Category> categoryList = new ArrayList<>();
        List<CategoryResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Category category = new Category((long) i, "category example");
            categoryList.add(category);

            CategoryResponse categoryResponse = new CategoryResponse((long) i, "category example");
            expectedResponses.add(categoryResponse);

            when(converter.toResponse(category, CategoryResponse.class)).thenReturn(categoryResponse);
        }

        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<CategoryResponse> actualResponses = categoryService.findAllCategories();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple categories");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple orders");

        verify(converter, times(10)).toResponse(any(Category.class), eq(CategoryResponse.class));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllCategories - Busca bem-sucedida retorna lista de categorias vazia")
    void findAllCategories_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<Category> emptyCategoryList = new ArrayList<>();

        when(categoryRepository.findAll()).thenReturn(emptyCategoryList);

        // Act
        List<CategoryResponse> actualResponses = categoryService.findAllCategories();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyCategoryList, actualResponses, "Expected an empty list of responses");

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllCategories - Exceção ao tentar buscar lista de categorias")
    void findAllCategories_RepositoryExceptionHandling() {
        // Arrange
        when(categoryRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.findAllCategories(), "Expected RepositoryException to be thrown");

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createCategory - Criação bem-sucedida retorna categoria criada")
    void createCategory_SuccessfulCreation_ReturnsCategoryResponse() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest("Computers");
        Category category = new Category("Computers");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");

        when(converter.toResponse(category, CategoryResponse.class)).thenReturn(expectedResponse);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        CategoryResponse actualResponse = categoryService.createCategory(categoryRequest);

        // Assert
        assertNotNull(actualResponse, "CategoryResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(category, CategoryResponse.class);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("createCategory - Exceção no repositório ao tentar criar categoria")
    void createCategory_RepositoryExceptionHandling() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest(1L, "Computers");
        Category category = new Category("Computers");

        when(categoryRepository.save(category)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.createCategory(categoryRequest), "Expected RepositoryException due to a PersistenceException");

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("findCategoryById - Busca bem-sucedida retorna pedido")
    void findCategoryById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        Category category = new Category(1L, "Computers");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");

        when(converter.toResponse(category, CategoryResponse.class)).thenReturn(expectedResponse);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        CategoryResponse actualResponse = categoryService.findCategoryById(1L);

        // Assert
        assertNotNull(actualResponse, "categoryResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(category, CategoryResponse.class);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findCategoryById - Exceção ao tentar buscar categoria inexistente")
    void findCategory_NotFoundExceptionHandling() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> categoryService.findCategoryById(1L), "Expected NotFoundException for non-existent category");

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateCategory - Atualização bem-sucedida retorna categoria atualizada")
    void updateCategory_SuccessfulUpdate_ReturnsCategoryResponse() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest(1L,"Computers");
        Category category = new Category(1L, "Computers");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(converter.toResponse(category, CategoryResponse.class)).thenReturn(expectedResponse);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        CategoryResponse actualResponse = categoryService.updateCategory(categoryRequest);

        // Assert
        assertNotNull(actualResponse,"categoryResponse should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse.getName(), actualResponse.getName(), "Names should match");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(category);
        verify(converter, times(1)).toResponse(category, CategoryResponse.class);
    }

    @Test
    @DisplayName("updateCategory - Exceção ao tentar atualizar categoria inexistente")
    void updateCategory_NotFoundExceptionHandling() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest(1L, "Computers");

        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(categoryRequest), "Expected NotFoundException for update failure");

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateCategory - Exceção no repositório ao tentar atualizar a categoria")
    void updateCategory_RepositoryExceptionHandling() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest(1L,"Computers");
        Category category = new Category(1L, "Computers");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenThrow(PersistenceException.class);

        // Act
        assertThrows(RepositoryException.class, () -> categoryService.updateCategory(categoryRequest), "Expected RepositoryException due to a generic runtime exception");

        // Assert
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("deleteCategory - Exclusão bem-sucedida da categoria")
    void deleteCategory_DeletesCategorySuccessfully() {
        // Arrange
        Category category = new Category(1L, "Computers");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCategory - Exceção ao tentar excluir categoria inexistente")
    void deleteCategory_NotFoundExceptionHandling() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(1L), "Expected NotFoundException for non-existent category");

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deleteCategory - Exceção no repositório ao tentar excluir categoria")
    void deleteCategory_RepositoryExceptionHandling() {
        // Arrange
        Category category = new Category(1L, "Computers");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doThrow(PersistenceException.class).when(categoryRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.deleteCategory(1L), "Expected RepositoryException for delete failure");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
