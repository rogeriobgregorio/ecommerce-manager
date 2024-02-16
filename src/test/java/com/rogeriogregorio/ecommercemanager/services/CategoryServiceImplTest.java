package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
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
}
