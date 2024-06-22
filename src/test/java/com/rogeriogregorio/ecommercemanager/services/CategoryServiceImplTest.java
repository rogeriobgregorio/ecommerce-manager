package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.CategoryServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeFunction;
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
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> categoryRepository.findAll(pageable));

        // Act
        Page<CategoryResponse> actualResponses = categoryService.findAllCategories(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponses, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(category, CategoryResponse.class);
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllCategories - Exceção no repositório buscar lista de categorias")
    void findAllCategories_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(categoryRepository.findAll(pageable)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> categoryRepository.findAll(pageable));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.findAllCategories(pageable),
                "Expected RepositoryException to be thrown");
        verify(categoryRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createCategory - Criação bem-sucedida retorna categoria criada")
    void createCategory_SuccessfulCreation_ReturnsCategory() {
        // Arrange
        CategoryResponse expectedResponse = categoryResponse;

        when(dataMapper.map(categoryRequest, Category.class)).thenReturn(category);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> categoryRepository.save(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(dataMapper.map(category, CategoryResponse.class)).thenReturn(expectedResponse);

        // Act
        CategoryResponse actualResponse = categoryService.createCategory(categoryRequest);

        // Assert
        assertNotNull(actualResponse, "Category should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(category, CategoryResponse.class);
        verify(dataMapper, times(1)).map(categoryRequest, Category.class);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("createCategory - Exceção no repositório ao tentar criar categoria")
    void createCategory_RepositoryExceptionHandling() {
        // Arrange
        when(dataMapper.map(categoryRequest, Category.class)).thenReturn(category);
        when(categoryRepository.save(category)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> categoryRepository.save(category));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.createCategory(categoryRequest),
                "Expected RepositoryException to be thrown");
        verify(dataMapper, times(1)).map(categoryRequest, Category.class);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("findCategoryById - Busca bem-sucedida retorna pedido")
    void findCategoryById_SuccessfulSearch_ReturnsOrder() {
        // Arrange
        CategoryResponse expectedResponse = categoryResponse;

        when(dataMapper.map(category, CategoryResponse.class)).thenReturn(expectedResponse);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> categoryRepository.findById(category.getId()));

        // Act
        CategoryResponse actualResponse = categoryService.findCategoryById(category.getId());

        // Assert
        assertNotNull(actualResponse, "category should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(category, CategoryResponse.class);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findCategoryById - Exceção ao tentar buscar categoria inexistente")
    void findCategory_NotFoundExceptionHandling() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> categoryRepository.findById(category.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> categoryService.findCategoryById(1L),
                "Expected NotFoundException to be thrown");
        verify(categoryRepository, times(1)).findById(1L);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findCategoryById - Exceção no repositório ao tentar buscar categoria")
    void findCategory_RepositoryExceptionHandling() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> categoryRepository.findById(category.getId()));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.findCategoryById(category.getId()),
                "Expected RepositoryException to be thrown");
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateCategory - Atualização bem-sucedida retorna categoria atualizada")
    void updateCategory_SuccessfulUpdate_ReturnsCategoryResponse() {
        // Arrange
        CategoryResponse expectedResponse = categoryResponse;

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(dataMapper.map(eq(categoryRequest), any(Category.class))).thenReturn(category);
        when(dataMapper.map(eq(category), eq(CategoryResponse.class))).thenReturn(expectedResponse);
        when(categoryRepository.save(category)).thenReturn(category);
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation.getArgument(0, SafeFunction.class).execute());

        // Act
        CategoryResponse actualResponse = categoryService.updateCategory(category.getId(), categoryRequest);

        // Assert
        assertNotNull(actualResponse,"category should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse.getName(), actualResponse.getName(), "Names should match");
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).save(category);
        verify(dataMapper, times(1)).map(eq(categoryRequest), any(Category.class));
        verify(dataMapper, times(1)).map(eq(category), eq(CategoryResponse.class));
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateCategory - Exceção ao tentar atualizar categoria inexistente")
    void updateCategory_NotFoundExceptionHandling() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation.getArgument(0, SafeFunction.class).execute());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(category.getId(), categoryRequest),
                "Expected NotFoundException for update failure");
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(categoryRepository, never()).save(category);
    }

    @Test
    @DisplayName("updateCategory - Exceção no repositório ao tentar atualizar a categoria")
    void updateCategory_RepositoryExceptionHandling() {
        // Arrange
        when(dataMapper.map(eq(categoryRequest), any(Category.class))).thenReturn(category);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation.getArgument(0, SafeFunction.class).execute());
        when(categoryRepository.save(category)).thenThrow(RepositoryException.class);

        // Act
        assertThrows(RepositoryException.class, () -> categoryService.updateCategory(category.getId(), categoryRequest),
                "Expected RepositoryException to be thrown");

        // Assert
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).save(category);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteCategory - Exclusão bem-sucedida da categoria")
    void deleteCategory_DeletesCategorySuccessfully() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> categoryRepository.findById(category.getId()));
        doAnswer(invocation -> {
            categoryRepository.delete(category);
            return null;
        }).when(catchError).run(any(CatchError.SafeProcedure.class));
        doNothing().when(categoryRepository).delete(category);

        // Act
        categoryService.deleteCategory(category.getId());

        // Assert
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).delete(category);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(CatchError.SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteCategory - Exceção ao tentar excluir categoria inexistente")
    void deleteCategory_NotFoundExceptionHandling() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> categoryRepository.findById(category.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(category.getId()),
                "Expected NotFoundException to be thrown");
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(categoryRepository, never()).delete(category);
    }

    @Test
    @DisplayName("deleteCategory - Exceção no repositório ao tentar excluir categoria")
    void deleteCategory_RepositoryExceptionHandling() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> categoryRepository.findById(category.getId()));
        doAnswer(invocation -> {
            categoryRepository.delete(category);
            return null;
        }).when(catchError).run(any(CatchError.SafeProcedure.class));
        doThrow(RepositoryException.class).when(categoryRepository).delete(category);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> categoryService.deleteCategory(category.getId()),
                "Expected RepositoryException to be thrown");
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryRepository, times(1)).delete(category);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(CatchError.SafeProcedure.class));
    }
}
