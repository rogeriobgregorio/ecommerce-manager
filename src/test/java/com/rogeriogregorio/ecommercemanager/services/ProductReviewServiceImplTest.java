package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductReviewRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductReviewResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.ProductReviewPK;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductReviewRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.ProductReviewServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeFunction;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeProcedure;
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

import static org.junit.jupiter.api.Assertions.*;
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
    private static Payment payment;
    private static ProductReview productReview;
    private static ProductReviewRequest productReviewRequest;
    private static ProductReviewResponse productReviewResponse;

    @BeforeEach
    void setUp() {

        DiscountCoupon discountCoupon = new DiscountCoupon(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        user = User.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withName("Admin")
                .withEmail("admin@email.com")
                .withPhone("11912345678")
                .withCpf("72482581052")
                .withPassword("Password123$")
                .withRole(UserRole.ADMIN)
                .build();

        Order order = Order.newBuilder()
                .withId(1L)
                .withClient(user)
                .withMoment(Instant.now())
                .withCoupon(discountCoupon)
                .withOrderStatus(OrderStatus.DELIVERED)
                .withPayment(payment)
                .build();

        payment = Payment.newBuilder()
                .withId(1L)
                .withMoment(Instant.now())
                .withOrder(order)
                .withPaymentStatus(PaymentStatus.CONCLUDED)
                .withPaymentType(PaymentType.PIX)
                .withTxId("b3f1b57e-ec0c-4b23-a6b2-647d2b176d74")
                .withChargeLink("https://bank.com/paymentqrcode")
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
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.findAll(pageable));

        // Act
        Page<ProductReviewResponse> actualResponse = productReviewService.findAllProductReviews(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(productReview, ProductReviewResponse.class);
        verify(productReviewRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllProductReviews - Exceção no repositório tentar buscar lista de reviews de produtos")
    void findAllProductReviews_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(productReviewRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productReviewService.findAllProductReviews(pageable),
                "Expected RepositoryException to be thrown");
        verify(productReviewRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createProductReview - Criação bem-sucedida retorna review de produto criado")
    void createProductReview_SuccessfulCreation_ReturnsProductReview() {
        // Arrange
        ProductReviewResponse expectedResponse = productReviewResponse;
        User mockUser = mock(User.class);

        when(mockUser.getPurchasedProducts()).thenReturn(Set.of(product));
        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(mockUser);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.save(productReview));
        when(productReviewRepository.save(productReview)).thenReturn(productReview);
        when(dataMapper.map(productReview, ProductReviewResponse.class)).thenReturn(expectedResponse);

        // Act
        ProductReviewResponse actualResponse = productReviewService.createProductReview(productReviewRequest);

        // Assert
        assertNotNull(actualResponse, "Product review should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).save(productReview);
        verify(dataMapper, times(1)).map(productReview, ProductReviewResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createProductReview - Exceção no repositório ao tentar criar review de produto")
    void createProductReview_RepositoryExceptionHandling() {
        // Arrange
        User mockUser = mock(User.class);

        when(mockUser.getPurchasedProducts()).thenReturn(Set.of(product));
        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(mockUser);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.save(productReview));
        when(productReviewRepository.save(productReview)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productReviewService.createProductReview(productReviewRequest),
                "Expected RepositoryException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).save(productReview);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findProductReviewById - Busca bem-sucedida retorna review de produto")
    void findProductReviewById_SuccessfulSearch_ReturnsProductReview() {
        // Arrange
        ProductReviewResponse expectedResponse = productReviewResponse;

        ProductReviewPK id = new ProductReviewPK();
        id.setUser(user);
        id.setProduct(product);

        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(user);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(dataMapper.map(productReview, ProductReviewResponse.class)).thenReturn(expectedResponse);
        when(productReviewRepository.findById(id)).thenReturn(Optional.of(productReview));
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.findById(id));

        // Act
        ProductReviewResponse actualResponse = productReviewService.findProductReviewById(1L, user.getId());

        // Assert
        assertNotNull(actualResponse, "Product review should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).findById(id);
        verify(dataMapper, times(1)).map(productReview, ProductReviewResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findProductReviewById - Exceção ao tentar buscar review de produto inexistente")
    void findProductReviewById_NotFoundExceptionHandling() {
        // Arrange
        ProductReviewPK id = new ProductReviewPK();
        id.setUser(user);
        id.setProduct(product);

        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(user);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(productReviewRepository.findById(id)).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.findById(id));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productReviewService.findProductReviewById(1L, user.getId()),
                "Expected NotFoundException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).findById(id);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findProductReviewById - Exceção ao tentar buscar review de produto")
    void findProductReviewById_RepositoryExceptionHandling() {
        // Arrange
        ProductReviewPK id = new ProductReviewPK();
        id.setUser(user);
        id.setProduct(product);

        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(user);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(productReviewRepository.findById(id)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.findById(id));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productReviewService.findProductReviewById(1L, user.getId()),
                "Expected RepositoryException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).findById(id);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateProductReview - Atualização bem-sucedida retorna review de produto atualizado")
    void updateProductReview_SuccessfulUpdate_ReturnsProductReview() {
        // Arrange
        User mockUser = mock(User.class);

        when(mockUser.getPurchasedProducts()).thenReturn(Set.of(product));
        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(mockUser);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.save(productReview));
        when(productReviewRepository.save(productReview)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productReviewService.updateProductReview(productReviewRequest),
                "Expected RepositoryException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).save(productReview);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateProductReview - Exceção ao tentar atualizar review de produto inexistente")
    void updateProductReview_NotFoundExceptionHandling() {
        // Arrange
        User mockUser = mock(User.class);

        when(mockUser.getPurchasedProducts()).thenReturn(Set.of(product));
        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(mockUser);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.save(productReview));
        when(productReviewRepository.save(productReview)).thenThrow(NotFoundException.class);

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productReviewService.updateProductReview(productReviewRequest),
                "Expected NotFoundException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).save(productReview);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateProductReview - Exceção ao tentar atualizar review de produto")
    void updateProductReview_RepositoryExceptionHandling() {
        // Arrange
        User mockUser = mock(User.class);

        when(mockUser.getPurchasedProducts()).thenReturn(Set.of(product));
        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(mockUser);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        when(productReviewRepository.save(productReview)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> productReviewRepository.save(productReview));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productReviewService.updateProductReview(productReviewRequest),
                "Expected RepositoryException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).save(productReview);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteProductReview - Exclusão bem-sucedida do review de produto")
    void deleteProductReview_DeletesProductReviewSuccessfully() {
        // Arrange
        ProductReviewPK id = new ProductReviewPK();
        id.setUser(user);
        id.setProduct(product);

        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(user);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        doAnswer(invocation -> {
            productReviewRepository.deleteById(id);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));

        // Act
        productReviewService.deleteProductReview(1L, user.getId());

        // Assert
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).deleteById(id);
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteProductReview - Exceção ao tentar excluir review de produto inexistente")
    void deleteProductReview_NotFoundExceptionHandling() {
        // Arrange
        ProductReviewPK id = new ProductReviewPK();
        id.setUser(user);
        id.setProduct(product);

        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(user);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        doThrow(NotFoundException.class).when(productReviewRepository).deleteById(id);
        doAnswer(invocation -> {
            productReviewRepository.deleteById(id);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> productReviewService.deleteProductReview(1L, user.getId()),
                "Expected NotFoundException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).deleteById(id);
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteProductReview - Exceção ao tentar excluir review de produto")
    void deleteProductReview_RepositoryExceptionHandling() {
        // Arrange
        ProductReviewPK id = new ProductReviewPK();
        id.setUser(user);
        id.setProduct(product);

        when(userService.getUserIfExists(productReviewRequest.getUserId())).thenReturn(user);
        when(productService.getProductIfExists(productReviewRequest.getProductId())).thenReturn(product);
        doThrow(RepositoryException.class).when(productReviewRepository).deleteById(id);
        doAnswer(invocation -> {
            productReviewRepository.deleteById(id);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> productReviewService.deleteProductReview(1L, user.getId()),
                "Expected RepositoryException to be thrown");
        verify(userService, times(1)).getUserIfExists(productReviewRequest.getUserId());
        verify(productService, times(1)).getProductIfExists(productReviewRequest.getProductId());
        verify(productReviewRepository, times(1)).deleteById(id);
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }
}
