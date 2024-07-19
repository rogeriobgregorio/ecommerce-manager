package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.repositories.DiscountCouponRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.DiscountCouponServiceImpl;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
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
    @DisplayName("findAllDiscountCoupons - Busca bem-sucedida retorna lista de cupons de disconto")
    void findAllDiscountCoupons_SuccessfulSearch_ReturnsDiscountCouponsList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<DiscountCoupon> discountCouponList = Collections.singletonList(discountCoupon);
        List<DiscountCouponResponse> expectedResponses = Collections.singletonList(discountCouponResponse);
        PageImpl<DiscountCoupon> page = new PageImpl<>(discountCouponList, pageable, discountCouponList.size());

        when(dataMapper.map(discountCoupon, DiscountCouponResponse.class)).thenReturn(discountCouponResponse);
        when(discountCouponRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(CatchError.SafeFunction.class))).thenAnswer(invocation -> discountCouponRepository.findAll(pageable));

        // Act
        Page<DiscountCouponResponse> actualResponse = discountCouponService.findAllDiscountCoupons(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(discountCoupon, DiscountCouponResponse.class);
        verify(discountCouponRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(CatchError.SafeFunction.class));
    }
}
