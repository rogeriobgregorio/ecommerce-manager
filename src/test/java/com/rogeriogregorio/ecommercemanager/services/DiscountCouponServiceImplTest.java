package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.DiscountCouponRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.DiscountCouponServiceImpl;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountCouponServiceImplTest {

    @Mock
    private DiscountCouponRepository discountCouponRepository;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private DiscountCouponServiceImpl discountCouponService;

    private static DiscountCoupon discountCoupon;
    private static DiscountCouponRequest discountCouponRequest;
    private static DiscountCouponResponse discountCouponResponse;

    @BeforeEach
    void setUp() {

        discountCoupon = new DiscountCoupon(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        discountCouponRequest = new DiscountCouponRequest("PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"), Instant.parse("2024-07-26T00:00:00Z"));

        discountCouponResponse = new DiscountCouponResponse(1L,
                "PROMO70OFF", BigDecimal.valueOf(0.15),
                Instant.parse("2024-06-26T00:00:00Z"),
                Instant.parse("2024-07-26T00:00:00Z"));

        MockitoAnnotations.openMocks(this);
        discountCouponService = new DiscountCouponServiceImpl(discountCouponRepository, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllDiscountCoupons - Busca bem-sucedida retorna lista de cupons de desconto")
    void findAllDiscountCoupons_SuccessfulSearch_ReturnsDiscountCouponsList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<DiscountCoupon> discountCouponList = Collections.singletonList(discountCoupon);
        List<DiscountCouponResponse> expectedResponses = Collections.singletonList(discountCouponResponse);
        PageImpl<DiscountCoupon> page = new PageImpl<>(discountCouponList, pageable, discountCouponList.size());

        when(dataMapper.map(discountCoupon, DiscountCouponResponse.class)).thenReturn(discountCouponResponse);
        when(discountCouponRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findAll(pageable));

        // Act
        Page<DiscountCouponResponse> actualResponse = discountCouponService.findAllDiscountCoupons(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(discountCoupon, DiscountCouponResponse.class);
        verify(discountCouponRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllDiscountCoupons - Exceção no repositório tentar buscar lista de cupons de desconto")
    void findAllDiscountCoupons_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(discountCouponRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> discountCouponService.findAllDiscountCoupons(pageable),
                "Expected RepositoryException to be thrown");
        verify(discountCouponRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createDiscountCoupon - Criação bem-sucedida retorna cupom de desconto criado")
    void createDiscountCoupon_SuccessfulCreation_ReturnsDiscountCoupon() {
        // Arrange
        DiscountCouponResponse expectedResponse = discountCouponResponse;

        when(dataMapper.map(discountCouponRequest, DiscountCoupon.class)).thenReturn(discountCoupon);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.save(discountCoupon));
        when(discountCouponRepository.save(discountCoupon)).thenReturn(discountCoupon);
        when(dataMapper.map(discountCoupon, DiscountCouponResponse.class)).thenReturn(expectedResponse);

        // Act
        DiscountCouponResponse actualResponse = discountCouponService.createDiscountCoupon(discountCouponRequest);

        // Assert
        assertNotNull(actualResponse, "Discount coupon should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(discountCouponRequest, DiscountCoupon.class);
        verify(discountCouponRepository, times(1)).save(discountCoupon);
        verify(dataMapper, times(1)).map(discountCoupon, DiscountCouponResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createDiscountCoupon - Exceção no repositório ao tentar criar cupom de desconto")
    void createDiscountCoupon_RepositoryExceptionHandling() {
        // Arrange
        when(dataMapper.map(discountCouponRequest, DiscountCoupon.class)).thenReturn(discountCoupon);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.save(discountCoupon));
        when(discountCouponRepository.save(discountCoupon)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> discountCouponService.createDiscountCoupon(discountCouponRequest),
                "Expected RepositoryException to be thrown");
        verify(dataMapper, times(1)).map(discountCouponRequest, DiscountCoupon.class);
        verify(discountCouponRepository, times(1)).save(discountCoupon);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findDiscountCouponById - Busca bem-sucedida retorna cupom de desconto")
    void findDiscountCouponById_SuccessfulSearch_ReturnsDiscountCoupon() {
        // Arrange
        DiscountCouponResponse expectedResponse = discountCouponResponse;

        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.of(discountCoupon));
        when(dataMapper.map(discountCoupon, DiscountCouponResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findById(discountCoupon.getId()));

        // Act
        DiscountCouponResponse actualResponse = discountCouponService.findDiscountCouponById(discountCoupon.getId());

        // Assert
        assertNotNull(actualResponse, "Discount coupon should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(dataMapper, times(1)).map(discountCoupon, DiscountCouponResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findDiscountCouponById - Exceção ao tentar buscar cupom de desconto inexistente")
    void findDiscountCouponById_NotFoundExceptionHandling() {
        // Arrange
        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findById(discountCoupon.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> discountCouponService.findDiscountCouponById(discountCoupon.getId()),
                "Expected NotFoundException to be thrown");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findDiscountCouponById - Exceção no repositório ao tentar buscar cupom de desconto")
    void findDiscountCouponById_RepositoryExceptionHandling() {
        // Arrange
        when(discountCouponRepository.findById(discountCoupon.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findById(discountCoupon.getId()));

        // Assert and Assert
        assertThrows(RepositoryException.class, () -> discountCouponService.findDiscountCouponById(discountCoupon.getId()),
                "Expected RepositoryException to be thrown");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateDiscountCoupon - Atualização bem-sucedida retorna cupom de desconto atualizado")
    void updateDiscountCoupon_SuccessfulUpdate_ReturnsDiscountCoupon() {
        // Arrange
        DiscountCouponResponse expectedResponse = discountCouponResponse;

        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.of(discountCoupon));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(dataMapper.map(eq(discountCouponRequest), any(DiscountCoupon.class))).thenReturn(discountCoupon);
        when(discountCouponRepository.save(discountCoupon)).thenReturn(discountCoupon);
        when(dataMapper.map(eq(discountCoupon), eq(DiscountCouponResponse.class))).thenReturn(expectedResponse);

        // Act
        DiscountCouponResponse actualResponse = discountCouponService.updateDiscountCoupon(discountCoupon.getId(), discountCouponRequest);

        // Assert
        assertNotNull(actualResponse, "Discount coupon should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(dataMapper, times(1)).map(eq(discountCouponRequest), any(DiscountCoupon.class));
        verify(discountCouponRepository, times(1)).save(discountCoupon);
        verify(dataMapper, times(1)).map(eq(discountCoupon), eq(DiscountCouponResponse.class));
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateDiscountCoupon - Exceção ao tentar atualizar cupom de desconto inexistente")
    void updateDiscountCoupon_NotFoundExceptionHandling() {
        // Arrange
        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> discountCouponRepository.findById(discountCoupon.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> discountCouponService.updateDiscountCoupon(discountCoupon.getId(), discountCouponRequest),
                "Expected NotFoundException to be thrown");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(discountCouponRepository, never()).save(discountCoupon);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateDiscountCoupon - Exceção no repositório ao tentar atualizar cupom de desconto")
    void updateDiscountCoupon_RepositoryExceptionHandling() {
        // Arrange
        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.of(discountCoupon));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation
                .getArgument(0, SafeFunction.class).execute());
        when(dataMapper.map(eq(discountCouponRequest), any(DiscountCoupon.class))).thenReturn(discountCoupon);
        when(discountCouponRepository.save(discountCoupon)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> discountCouponService.updateDiscountCoupon(discountCoupon.getId(), discountCouponRequest),
                "Expected RepositoryException to be thrown");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(dataMapper, times(1)).map(eq(discountCouponRequest), any(DiscountCoupon.class));
        verify(discountCouponRepository, times(1)).save(discountCoupon);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteDiscountCoupon - Exclusão bem-sucedida do cupom de desconto")
    void deleteDiscountCoupon_DeletesDiscountCouponSuccessfully() {
        // Arrange
        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.of(discountCoupon));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> discountCouponRepository.findById(discountCoupon.getId()));
        doAnswer(invocation -> {
            discountCouponRepository.delete(discountCoupon);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doNothing().when(discountCouponRepository).delete(discountCoupon);

        // Act
        discountCouponService.deleteDiscountCoupon(discountCoupon.getId());

        // Assert
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(discountCouponRepository, times(1)).delete(discountCoupon);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteDiscountCoupon - Exceção ao tentar excluir cupom de desconto inexistente")
    void deleteDiscountCoupon_NotFoundExceptionHandling() {
        // Arrange
        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> discountCouponRepository.findById(discountCoupon.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> discountCouponService.deleteDiscountCoupon(discountCoupon.getId()),
                "Expected NotFoundException to be thrown");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(discountCouponRepository, never()).delete(discountCoupon);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteDiscountCoupon - Exceção no repositório ao tentar excluir cupom de desconto")
    void deleteDiscountCoupon_RepositoryExceptionHandling() {
        // Arrange
        when(discountCouponRepository.findById(discountCoupon.getId())).thenReturn(Optional.of(discountCoupon));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> discountCouponRepository.findById(discountCoupon.getId()));
        doAnswer(invocation -> {
            discountCouponRepository.delete(discountCoupon);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doThrow(RepositoryException.class).when(discountCouponRepository).delete(discountCoupon);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> discountCouponService.deleteDiscountCoupon(discountCoupon.getId()),
                "Expected RepositoryException to be thrown");
        verify(discountCouponRepository, times(1)).findById(discountCoupon.getId());
        verify(discountCouponRepository, times(1)).delete(discountCoupon);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("findDiscountCouponByCode - Busca bem-sucedida retorna cupom de desconto")
    void findDiscountCouponByCode_SuccessfulSearch_ReturnsDiscountCoupon() {
        // Arrange
        DiscountCoupon expectedResponse = discountCoupon;

        when(discountCouponRepository.findByCode(discountCoupon.getCode())).thenReturn(Optional.of(discountCoupon));
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findByCode(discountCoupon.getCode()));

        // Act
        DiscountCoupon actualResponse = discountCouponService.findDiscountCouponByCode(discountCoupon.getCode());

        // Assert
        assertNotNull(actualResponse, "Discount coupon should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(discountCouponRepository, times(1)).findByCode(discountCoupon.getCode());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findDiscountCouponByCode - Exceção ao tentar buscar cupom de desconto inexistente")
    void findDiscountCouponByCode_NotFoundExceptionHandling() {
        // Arrange
        when(discountCouponRepository.findByCode(discountCoupon.getCode())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findByCode(discountCoupon.getCode()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> discountCouponService.findDiscountCouponByCode(discountCoupon.getCode()),
                "Expected NotFoundException to be thrown");
        verify(discountCouponRepository, times(1)).findByCode(discountCoupon.getCode());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }
}
