package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.findAllCategories(pageable).getContent());
    }

    @PostMapping(value = "/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryRequest));
    }

    @GetMapping(value = "/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.findCategoryById(id));
    }

    @PutMapping(value = "/categories")
    public ResponseEntity<CategoryResponse> updateCategory(@Valid @RequestBody CategoryRequest categoryRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.updateCategory(categoryRequest));
    }

    @DeleteMapping(value = "/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(value = "/categories/search")
    public ResponseEntity<List<CategoryResponse>> getCategoryByName(
            @RequestParam("name") String name, Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.findCategoryByName(name, pageable).getContent());
    }
}