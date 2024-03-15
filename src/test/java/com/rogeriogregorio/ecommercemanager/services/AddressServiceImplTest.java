package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.AddressEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.AddressRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.AddressServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserService userService;

    @Mock
    private Converter converter;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addressService = new AddressServiceImpl(addressRepository, userService, converter);
    }

    @Test
    @DisplayName("findAllAddresses - Busca bem-sucedida retorna lista contendo um endereço")
    void findAllAddresses_SuccessfulSearch_ReturnsListResponse_OneAddresses() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);
        List<AddressEntity> addressEntityList = Collections.singletonList(addressEntity);

        AddressResponse addressResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressResponse.setUser(userEntity);
        List<AddressResponse> expectedResponses = Collections.singletonList(addressResponse);

        when(converter.toResponse(addressEntity, AddressResponse.class)).thenReturn(addressResponse);
        when(addressRepository.findAll()).thenReturn(addressEntityList);

        // Act
        List<AddressResponse> actualResponse = addressService.findAllAddresses();

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.size(), "Expected a list of responses with one address");
        assertIterableEquals(expectedResponses, actualResponse, "Expected a list of responses with one address");

        verify(converter, times(1)).toResponse(addressEntity, AddressResponse.class);
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllAddresses - Busca bem-sucedida retorna lista contendo múltiplos endereços")
    void findAllAddresses_SuccessfulSearch_ReturnsListResponse_MultipleAddresses() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        List<AddressEntity> addressEntityList = new ArrayList<>();
        List<AddressResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            AddressEntity addressEntity = new AddressEntity((long) i, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
            addressEntity.setUserEntity(userEntity);
            addressEntityList.add(addressEntity);

            AddressResponse addressResponse = new AddressResponse((long) i, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
            addressResponse.setUser(userEntity);
            expectedResponses.add(addressResponse);

            when(converter.toResponse(addressEntity, AddressResponse.class)).thenReturn(addressResponse);
        }

        when(addressRepository.findAll()).thenReturn(addressEntityList);

        // Act
        List<AddressResponse> actualResponses = addressService.findAllAddresses();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple address");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple address");

        verify(converter, times(10)).toResponse(any(AddressEntity.class), eq(AddressResponse.class));
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllAddresses - Exceção ao tentar buscar lista de endereços")
    void findAllAddresses_RepositoryExceptionHandling() {
        // Arrange
        when(addressRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.findAllAddresses(), "Expected RepositoryException to be thrown");

        verify(addressRepository, times(1)).findAll();
    }
}
