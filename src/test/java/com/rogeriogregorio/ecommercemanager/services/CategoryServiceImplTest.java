package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.CategoryServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.Function;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.Procedure;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import jakarta.persistence.PersistenceException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private static Category category;
    private static CategoryRequest categoryRequest;
    private static CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {

        category = new Category(1L, "Computers");
        categoryRequest = new CategoryRequest("Computers");
        categoryResponse = new CategoryResponse(1L, "Computers");

        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryServiceImpl(categoryRepository, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllCategories - Busca bem-sucedida retorna lista de categorias")
    void findAllCategories_SuccessfulSearch_ReturnsCategoryList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categoryList = Collections.singletonList(category);
        List<CategoryResponse> expectedResponses = Collections.singletonList(categoryResponse);
        PageImpl<Category> page = new PageImpl<>(categoryList, pageable, categoryList.size());

        when(dataMapper.map(category, CategoryResponse.class)).thenReturn(categoryResponse);
        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(Function.class))).thenAnswer(invocation -> categoryRepository.findAll(pageable));

        // Act
        Page<CategoryResponse> actualResponses = categoryService.findAllCategories(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.getContent().size(), "Expected a list with one category");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list with one categories");

        verify(dataMapper, times(1)).map(category, CategoryResponse.class);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(Function.class));
    }

    @Test
    @DisplayName("findAllCategories - Exceção no repositório buscar lista de categorias")
    void findAllCategories_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(categoryRepository.findAll(pageable)).thenThrow(RepositoryException.class);
        when(catchError.run(any(Function.class))).thenAnswer(invocation -> categoryRepository.findAll(pageable));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.findAllCategories(pageable),
                "Expected RepositoryException to be thrown");
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(Function.class));
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
//
//    @Test
//    @DisplayName("createCategory - Exceção no repositório ao tentar criar categoria")
//    void createCategory_RepositoryExceptionHandling() {
//        // Arrange
//        CategoryRequest categoryRequest = new CategoryRequest(1L, "Computers");
//        Category category = new Category("Computers");
//
//        when(categoryRepository.save(category)).thenThrow(PersistenceException.class);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> categoryService.createCategory(categoryRequest), "Expected RepositoryException due to a PersistenceException");
//
//        verify(categoryRepository, times(1)).save(category);
//    }
//
//    @Test
//    @DisplayName("findCategoryById - Busca bem-sucedida retorna pedido")
//    void findCategoryById_SuccessfulSearch_ReturnsOrderResponse() {
//        // Arrange
//        Category category = new Category(1L, "Computers");
//        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");
//
//        when(converter.toResponse(category, CategoryResponse.class)).thenReturn(expectedResponse);
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//
//        // Act
//        CategoryResponse actualResponse = categoryService.findCategoryById(1L);
//
//        // Assert
//        assertNotNull(actualResponse, "categoryResponse should not be null");
//        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
//
//        verify(converter, times(1)).toResponse(category, CategoryResponse.class);
//        verify(categoryRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("findCategoryById - Exceção ao tentar buscar categoria inexistente")
//    void findCategory_NotFoundExceptionHandling() {
//        // Arrange
//        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> categoryService.findCategoryById(1L), "Expected NotFoundException for non-existent category");
//
//        verify(categoryRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("updateCategory - Atualização bem-sucedida retorna categoria atualizada")
//    void updateCategory_SuccessfulUpdate_ReturnsCategoryResponse() {
//        // Arrange
//        CategoryRequest categoryRequest = new CategoryRequest(1L,"Computers");
//        Category category = new Category(1L, "Computers");
//        CategoryResponse expectedResponse = new CategoryResponse(1L, "Computers");
//
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//        when(converter.toResponse(category, CategoryResponse.class)).thenReturn(expectedResponse);
//        when(categoryRepository.save(category)).thenReturn(category);
//
//        // Act
//        CategoryResponse actualResponse = categoryService.updateCategory(categoryRequest);
//
//        // Assert
//        assertNotNull(actualResponse,"categoryResponse should not be null");
//        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
//        assertEquals(expectedResponse.getName(), actualResponse.getName(), "Names should match");
//
//        verify(categoryRepository, times(1)).findById(1L);
//        verify(categoryRepository, times(1)).save(category);
//        verify(converter, times(1)).toResponse(category, CategoryResponse.class);
//    }
//
//    @Test
//    @DisplayName("updateCategory - Exceção ao tentar atualizar categoria inexistente")
//    void updateCategory_NotFoundExceptionHandling() {
//        // Arrange
//        CategoryRequest categoryRequest = new CategoryRequest(1L, "Computers");
//
//        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(categoryRequest), "Expected NotFoundException for update failure");
//
//        verify(categoryRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("updateCategory - Exceção no repositório ao tentar atualizar a categoria")
//    void updateCategory_RepositoryExceptionHandling() {
//        // Arrange
//        CategoryRequest categoryRequest = new CategoryRequest(1L,"Computers");
//        Category category = new Category(1L, "Computers");
//
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//        when(categoryRepository.save(category)).thenThrow(PersistenceException.class);
//
//        // Act
//        assertThrows(RepositoryException.class, () -> categoryService.updateCategory(categoryRequest), "Expected RepositoryException due to a generic runtime exception");
//
//        // Assert
//        verify(categoryRepository, times(1)).findById(1L);
//        verify(categoryRepository, times(1)).save(category);
//    }
//
//    @Test
//    @DisplayName("deleteCategory - Exclusão bem-sucedida da categoria")
//    void deleteCategory_DeletesCategorySuccessfully() {
//        // Arrange
//        Category category = new Category(1L, "Computers");
//
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//
//        // Act
//        categoryService.deleteCategory(1L);
//
//        // Assert
//        verify(categoryRepository, times(1)).findById(1L);
//        verify(categoryRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("deleteCategory - Exceção ao tentar excluir categoria inexistente")
//    void deleteCategory_NotFoundExceptionHandling() {
//        // Arrange
//        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(1L), "Expected NotFoundException for non-existent category");
//
//        verify(categoryRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("deleteCategory - Exceção no repositório ao tentar excluir categoria")
//    void deleteCategory_RepositoryExceptionHandling() {
//        // Arrange
//        Category category = new Category(1L, "Computers");
//
//        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
//        doThrow(PersistenceException.class).when(categoryRepository).deleteById(1L);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> categoryService.deleteCategory(1L), "Expected RepositoryException for delete failure");
//
//        verify(categoryRepository, times(1)).findById(1L);
//        verify(categoryRepository, times(1)).deleteById(1L);
//    }
}
