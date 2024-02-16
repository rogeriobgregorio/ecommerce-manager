package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.CategoryServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
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
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Converter<CategoryRequest, CategoryEntity, CategoryResponse> categoryConverter;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryServiceImpl(categoryRepository, categoryConverter);
    }

    @Test
    @DisplayName("findAllCategories - Busca bem-sucedida retorna lista contendo uma categoria")
    void findAllCategories_SuccessfulSearch_ReturnsListResponse_OneCategory() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");
        List<CategoryEntity> categoryEntityList = Collections.singletonList(categoryEntity);

        CategoryResponse categoryResponse = new CategoryResponse(1L, "Computers");
        List<CategoryResponse> expectedResponses = Collections.singletonList(categoryResponse);

        when(categoryConverter.entityToResponse(categoryEntity)).thenReturn(categoryResponse);
        when(categoryRepository.findAll()).thenReturn(categoryEntityList);

        // Act
        List<CategoryResponse> actualResponses = categoryService.findAllCategories();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one category");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one categories");

        verify(categoryConverter, times(1)).entityToResponse(any(CategoryEntity.class));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllCategories - Busca bem-sucedida retorna lista contendo múltiplas categoria")
    void findAllCategories_SuccessfulSearch_ReturnsListResponse_MultipleCategories() {
        // Arrange
        List<CategoryEntity> categoryEntityList = new ArrayList<>();
        List<CategoryResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            CategoryEntity categoryEntity = new CategoryEntity((long) i, "category example");
            categoryEntityList.add(categoryEntity);

            CategoryResponse categoryResponse = new CategoryResponse((long) i, "category example");
            expectedResponses.add(categoryResponse);

            when(categoryConverter.entityToResponse(categoryEntity)).thenReturn(categoryResponse);
        }

        when(categoryRepository.findAll()).thenReturn(categoryEntityList);

        // Act
        List<CategoryResponse> actualResponses = categoryService.findAllCategories();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple categories");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple orders");

        verify(categoryConverter, times(10)).entityToResponse(any(CategoryEntity.class));
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllCategories - Busca bem-sucedida retorna lista de categorias vazia")
    void findAllCategories_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
        List<CategoryEntity> emptyCategoryList = new ArrayList<>();

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
        when(categoryRepository.findAll()).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.findAllCategories(), "Expected RepositoryException to be thrown");

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createCategory - Criação bem-sucedida retorna categoria criada")
    void createCategory_SuccessfulCreation_ReturnsCategoryResponse() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest("Computers");
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");

        when(categoryConverter.requestToEntity(categoryRequest)).thenReturn(categoryEntity);
        when(categoryConverter.entityToResponse(categoryEntity)).thenReturn(expectedResponse);
        when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);

        // Act
        CategoryResponse actualResponse = categoryService.createCategory(categoryRequest);

        // Assert
        assertNotNull(actualResponse, "CategoryResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(categoryConverter, times(1)).requestToEntity(categoryRequest);
        verify(categoryConverter, times(1)).entityToResponse(categoryEntity);
        verify(categoryRepository, times(1)).save(categoryEntity);
    }

    @Test
    @DisplayName("createCategory - Exceção no repositório ao tentar criar categoria")
    void createCategory_RepositoryExceptionHandling() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest(1L, "Computers");
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");

        when(categoryConverter.requestToEntity(categoryRequest)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.createCategory(categoryRequest), "Expected RepositoryException due to a generic runtime exception");

        verify(categoryConverter, times(1)).requestToEntity(categoryRequest);
        verify(categoryRepository, times(1)).save(categoryEntity);
    }

    @Test
    @DisplayName("findCategoryById - Busca bem-sucedida retorna pedido")
    void findCategoryById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");

        when(categoryConverter.entityToResponse(categoryEntity)).thenReturn(expectedResponse);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));

        // Act
        CategoryResponse actualResponse = categoryService.findCategoryById(1L);

        // Assert
        assertNotNull(actualResponse, "categoryResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(categoryConverter, times(1)).entityToResponse(categoryEntity);
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
        CategoryRequest categoryRequest = new CategoryRequest("Computers");
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");
        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");

        when(categoryConverter.requestToEntity(categoryRequest)).thenReturn(categoryEntity);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
        when(categoryConverter.entityToResponse(categoryEntity)).thenReturn(expectedResponse);
        when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);

        // Act
        CategoryResponse actualResponse = categoryService.updateCategory(categoryRequest);

        // Assert
        assertNotNull(actualResponse,"categoryResponse should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse.getName(), actualResponse.getName(), "Names should match");

        verify(categoryConverter, times(1)).requestToEntity(categoryRequest);
        verify(categoryRepository, times(1)).findById(categoryEntity.getId());
        verify(categoryRepository, times(1)).save(categoryEntity);
        verify(categoryConverter, times(1)).entityToResponse(categoryEntity);
    }

    @Test
    @DisplayName("updateCategory - Exceção ao tentar atualizar categoria inexistente")
    void updateCategory_NotFoundExceptionHandling() {
        // Arrange
        CategoryRequest categoryRequest = new CategoryRequest("Computers");
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");

        when(categoryConverter.requestToEntity(categoryRequest)).thenReturn(categoryEntity);
        when(categoryRepository.findById(categoryEntity.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(categoryRequest));

        verify(categoryConverter, times(1)).requestToEntity(categoryRequest);
        verify(categoryRepository, times(1)).findById(categoryEntity.getId());
    }

    @Test
    @DisplayName("deleteCategory - Exclusão bem-sucedida da categoria")
    void deleteCategory_DeletesCategorySuccessfully() {
        // Arrange
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
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
        CategoryEntity categoryEntity = new CategoryEntity(1L, "Computers");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryEntity));
        doThrow(RuntimeException.class).when(categoryRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.deleteCategory(1L), "Expected RepositoryException for delete failure");

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
