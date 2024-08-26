package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductReviewRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductReviewResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.ProductReviewRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.ProductReviewServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductReviewServiceImplTest {

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private ProductReviewServiceImpl productReviewService;

    private static User user;
    private static Product product;
    private static ProductReview productReview;
    private static ProductReviewRequest productReviewRequest;
    private static ProductReviewResponse productReviewResponse;

    @BeforeEach
    void setUp() {

        user = User.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withName("Admin").withEmail("admin@email.com").withPhone("11912345678")
                .withCpf("72482581052").withPassword("Password123$").withRole(UserRole.ADMIN)
                .build();

        Category category = new Category(1L, "Computers");
        Set<Category> categoryList = new HashSet<>();
        categoryList.add(category);

        ProductDiscount productDiscount = new ProductDiscount(1L,
                "Dia das Mães", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-01T00:00:00Z"),
                Instant.parse("2024-06-07T00:00:00Z"));

        product = Product.newBuilder()
                .withId(1L).withName("Intel i5-10400F").withDescription("Intel Core Processor")
                .withPrice(BigDecimal.valueOf(579.99)).withCategories(categoryList)
                .withImgUrl("https://example.com/i5-10400F.jpg")
                .withProductDiscount(productDiscount)
                .build();

        productReview = ProductReview.newBuilder()
                .withUser(user)
                .withProduct(product)
                .withMoment(Instant.now())
                .withRating(5)
                .withComment("Very good")
                .build();

        productReviewRequest = new ProductReviewRequest(1L,
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), 5, "Very good");

        productReviewResponse = new ProductReviewResponse(product, user, 5, "Very good", Instant.now());

        MockitoAnnotations.openMocks(this);
        productReviewService = new ProductReviewServiceImpl(productReviewRepository, productService, userService, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllProductReviews - Busca bem-sucedida retorna lista de reviews de produtos")
    void findAllProductReviews_SuccessfulSearch_ReturnsProductReviewsList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductReview> productReviewList = Collections.singletonList(productReview);
        List<ProductReviewResponse> expectedResponses = Collections.singletonList(productReviewResponse);
        PageImpl<ProductReview> page = new PageImpl<>(productReviewList, pageable, productReviewList.size());

        when(dataMapper.map(productReview, ProductReviewResponse.class)).thenReturn(productReviewResponse);
        when(productReviewRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.findAll(pageable));

        // Act
        Page<ProductReviewResponse> actualResponse = productReviewService.findAllProductReviews(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(productReview, ProductReviewResponse.class);
        verify(productReviewRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }
}
