package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.*;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.PaymentRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.PaymentServiceImpl;
import com.rogeriogregorio.ecommercemanager.services.strategy.payments.PaymentStrategy;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InventoryItemService inventoryItemService;

    @Mock
    private StockMovementService stockMovementService;

    @Mock
    private OrderService orderService;

    @Mock
    private MailService mailService;

    @Mock
    private List<PaymentStrategy> paymentMethods;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @Mock
    private List<OrderStrategy> validators;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private static Order order;
    private static Payment payment;
    private static PaymentRequest paymentRequest;
    private static PaymentResponse paymentResponse;
    private static DiscountCoupon discountCoupon;

    @Mock
    private static PaymentStrategy paymentStrategy;

    @BeforeEach
    void setUp() {

        User user = User.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withName("Admin").withEmail("admin@email.com").withPhone("11912345678")
                .withCpf("72482581052").withPassword("Password123$").withRole(UserRole.ADMIN)
                .build();

        Address address = Address.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withStreet("Rua ABC, 123").withCity("São Paulo").withState("SP")
                .withCep("01234-567").withCountry("Brasil").withUser(user)
                .build();

        user.setAddress(address);

        Category category = new Category(1L, "Computers");
        Set<Category> categoryList = new HashSet<>();
        categoryList.add(category);

        ProductDiscount productDiscount = new ProductDiscount(1L,
                "Dia das Mães", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-01T00:00:00Z"),
                Instant.parse("2024-06-07T00:00:00Z"));

        Product product = Product.newBuilder()
                .withId(1L).withName("Intel i5-10400F").withDescription("Intel Core Processor")
                .withPrice(BigDecimal.valueOf(579.99)).withCategories(categoryList)
                .withImgUrl("https://example.com/i5-10400F.jpg")
                .withProductDiscount(productDiscount)
                .build();

        order = Order.newBuilder()
                .withId(1L)
                .withClient(user)
                .withMoment(Instant.now())
                .withCoupon(discountCoupon)
                .withOrderStatus(OrderStatus.WAITING_PAYMENT)
                .withPayment(payment)
                .withItems(new HashSet<>())
                .build();

        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);

        OrderItem orderItem = OrderItem.newBuilder()
                .withOrder(order)
                .withProduct(product)
                .withQuantity(orderItemRequest.getQuantity())
                .withPrice(product.getPrice())
                .build();

        order.getItems().add(orderItem);

        payment = Payment.newBuilder()
                .withId(1L)
                .withMoment(Instant.now())
                .withOrder(order)
                .withPaymentStatus(PaymentStatus.PROCESSING)
                .withPaymentType(PaymentType.PIX)
                .withTxId("b3f1b57e-ec0c-4b23-a6b2-647d2b176d74")
                .withChargeLink("https://bank.com/paymentqrcode")
                .build();

        paymentRequest = new PaymentRequest(1L, PaymentType.PIX);

        paymentResponse = new PaymentResponse(1L, Instant.now(), order, "b3f1b57e-ec0c-4b23-a6b2-647d2b176d74",
                PaymentType.PIX, "https://bank.com/paymentqrcode", PaymentStatus.PROCESSING);

        paymentStrategy = new PaymentStrategy() {
            @Override
            public PaymentType getSupportedPaymentMethod() {
                return PaymentType.PIX;
            }

            @Override
            public Payment createPayment(Order order) {
                return payment;
            }
        };

        paymentMethods.add(paymentStrategy);

        MockitoAnnotations.openMocks(this);
        paymentService = new PaymentServiceImpl(paymentRepository, inventoryItemService, stockMovementService,
                mailService, orderService, validators, paymentMethods, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllPayments - Busca bem-sucedida retorna lista de pagamentos")
    void findAllPayments_SuccessfulSearch_ReturnsPaymentList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Payment> paymentList = Collections.singletonList(payment);
        List<PaymentResponse> expectedResponses = Collections.singletonList(paymentResponse);
        PageImpl<Payment> page = new PageImpl<>(paymentList, pageable, paymentList.size());

        when(dataMapper.map(payment, PaymentResponse.class)).thenReturn(paymentResponse);
        when(paymentRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> paymentRepository.findAll(pageable));

        // Act
        Page<PaymentResponse> actualResponses = paymentService.findAllPayments(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponses, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(payment, PaymentResponse.class);
        verify(paymentRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("findAllPayments - Exceção ao tentar buscar lista de pagamentos")
    void findAllPayments_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(paymentRepository.findAll(pageable)).thenThrow(RepositoryException.class);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> paymentRepository.findAll(pageable));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> paymentService.findAllPayments(pageable),
                "Expected RepositoryException to be thrown");
        verify(paymentRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }

    @Test
    @DisplayName("createPayment - Criação bem-sucedida retorna pagamento criado")
    void createPayment_SuccessfulCreation_ReturnsPayment() {
        // Arrange
        PaymentResponse expectedResponse = paymentResponse;

        when(orderService.getOrderIfExists(paymentRequest.getOrderId())).thenReturn(order);
        when(paymentStrategy.getSupportedPaymentMethod()).thenReturn(PaymentType.PIX);
        when(paymentStrategy.createPayment(order)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(dataMapper.map(payment, PaymentResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> paymentRepository.save(payment));

        // Act
        PaymentResponse actualResponse = paymentService.createPaymentProcess(paymentRequest);

        // Assert
        assertNotNull(actualResponse, "paymentResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(orderService, times(1)).getOrderIfExists(paymentRequest.getOrderId());
        verify(paymentRepository, times(1)).save(payment);
        verify(dataMapper, times(1)).map(payment, PaymentResponse.class);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }
//
//    @Test
//    @DisplayName("createPayment - Exceção no repositório ao tentar criar pagamento")
//    void createPayment_RepositoryExceptionHandling() {
//        // Arrange
//        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
//
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        user.setAddress(address);
//
//        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//        Product product = new Product(1L, "Playstation 5", "Video game console", 4099.0, "www.url.com");
//
//        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 1L, 1);
//        OrderItem orderItem = new OrderItem(order, product, orderItemRequest.getQuantity(), product.getPrice());
//
//        order.getItems().add(orderItem);
//
//        PaymentRequest paymentRequest = new PaymentRequest(1L);
//        Payment payment = new Payment(Instant.now(), order);
//
//        when(orderService.findOrderById(paymentRequest.getOrderId())).thenReturn(order);
//        doNothing().when(orderService).savePaidOrder(order);
//        when(paymentRepository.save(payment)).thenThrow(PersistenceException.class);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> paymentService.createPaymentProcess(paymentRequest), "Expected RepositoryException due to a generic runtime exception");
//
//        verify(orderService, times(1)).findOrderById(paymentRequest.getOrderId());
//        verify(orderService, times(1)).savePaidOrder(order);
//        verify(paymentRepository, times(1)).save(payment);
//    }
//
//    @Test
//    @DisplayName("findPaymentById - Busca bem-sucedida retorna pagamento")
//    void findCategoryById_SuccessfulSearch_ReturnsOrderResponse() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//
//        Payment payment = new Payment(Instant.now(), order);
//        PaymentResponse expectedResponse = new PaymentResponse(1L, Instant.now(), order);
//
//        when(converter.toResponse(payment, PaymentResponse.class)).thenReturn(expectedResponse);
//        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
//
//        // Act
//        PaymentResponse actualResponse = paymentService.findPaymentById(1L);
//
//        // Assert
//        assertNotNull(actualResponse, "paymentResponse should not be null");
//        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
//
//        verify(converter, times(1)).toResponse(payment, PaymentResponse.class);
//        verify(paymentRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("findPaymentById - Exceção ao tentar buscar pagamento inexistente")
//    void findPayment_NotFoundExceptionHandling() {
//        // Arrange
//        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> paymentService.findPaymentById(1L));
//
//        verify(paymentRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("deletePayment - Exclusão bem-sucedida do pedido")
//    void deletePayment_DeletesPaymentSuccessfully() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//
//        Payment payment = new Payment(Instant.now(), order);
//
//        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
//
//        // Act
//        paymentService.deletePayment(1L);
//
//        // Assert
//        verify(paymentRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("deletePayment - Exceção ao tentar excluir pagamento inexistente")
//    void deletePayment_NotFoundExceptionHandling() {
//        // Arrange
//        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> paymentService.deletePayment(1L));
//
//        verify(paymentRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("deletePayment - Exceção no repositório ao tentar excluir pagamento")
//    void deletePayment_RepositoryExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//        Order order = new Order(1L, Instant.now(), OrderStatus.WAITING_PAYMENT, user);
//
//        Payment payment = new Payment(Instant.now(), order);
//
//        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
//        doThrow(PersistenceException.class).when(paymentRepository).deleteById(1L);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> paymentService.deletePayment(1L), "Expected RepositoryException for delete failure");
//
//        verify(paymentRepository, times(1)).findById(1L);
//    }
}
