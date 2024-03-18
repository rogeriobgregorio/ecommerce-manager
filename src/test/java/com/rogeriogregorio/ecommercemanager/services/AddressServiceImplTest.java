package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.AddressEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
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
import java.util.Optional;

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

    @Test
    @DisplayName("createAddress - Criação bem-sucedida retorna endereço criado")
    void createAddress_SuccessfulCreation_ReturnsAddressResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);

        AddressRequest addressRequest = new AddressRequest("Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);
        AddressResponse expectedResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        when(userService.findUserEntityById(addressRequest.getUserId())).thenReturn(userEntity);
        when(converter.toEntity(addressRequest, AddressEntity.class)).thenReturn(addressEntity);
        doNothing().when(userService).saveUserAddress(userEntity);
        when(addressRepository.save(addressEntity)).thenReturn(addressEntity);
        when(converter.toResponse(addressEntity, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.createAddress(addressRequest);

        // Assert
        assertNotNull(actualResponse, "AddressResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(userService, times(1)).findUserEntityById(addressRequest.getUserId());
        verify(converter, times(1)).toEntity(addressRequest, AddressEntity.class);
        verify(userService, times(1)).saveUserAddress(userEntity);
        verify(addressRepository, times(1)).save(addressEntity);
        verify(converter, times(1)).toResponse(addressEntity, AddressResponse.class);
    }

    @Test
    @DisplayName("createAddress - Exceção no repositório ao tentar criar endereço")
    void createAddress_RepositoryExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);

        AddressRequest addressRequest = new AddressRequest("Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);

        when(userService.findUserEntityById(addressRequest.getUserId())).thenReturn(userEntity);
        when(converter.toEntity(addressRequest, AddressEntity.class)).thenReturn(addressEntity);
        doNothing().when(userService).saveUserAddress(userEntity);
        when(addressRepository.save(addressEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.createAddress(addressRequest), "Expected RepositoryException due to a PersistenceException");

        verify(userService, times(1)).findUserEntityById(addressRequest.getUserId());
        verify(converter, times(1)).toEntity(addressRequest, AddressEntity.class);
        verify(userService, times(1)).saveUserAddress(userEntity);
        verify(addressRepository, times(1)).save(addressEntity);
    }

    @Test
    @DisplayName("findAddressById - Busca bem-sucedida retorna endereço")
    void findAddressById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);
        AddressResponse expectedResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressEntity));
        when(converter.toResponse(addressEntity, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.findAddressById(1L);

        // Assert
        assertNotNull(actualResponse, "AddressResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(addressRepository, times(1)).findById(1L);
        verify(converter, times(1)).toResponse(addressEntity, AddressResponse.class);
    }

    @Test
    @DisplayName("findAddressById - Exceção ao tentar buscar endereço inexistente")
    void findAddressById_NotFoundExceptionHandling() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert and Assert
        assertThrows(NotFoundException.class, () -> addressService.findAddressById(1L), "Expected NotFoundException for non-existent address");

        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateAddress - Atualização bem-sucedida retorna endereço atualizado")
    void updateAddress_SuccessfulUpdate_ReturnsAddressResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);

        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);
        AddressResponse expectedResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        when(addressRepository.findById(addressEntity.getId())).thenReturn(Optional.of(addressEntity));
        when(userService.findUserEntityById(addressRequest.getUserId())).thenReturn(userEntity);
        when(converter.toEntity(addressRequest, AddressEntity.class)).thenReturn(addressEntity);
        doNothing().when(userService).saveUserAddress(userEntity);
        when(addressRepository.save(addressEntity)).thenReturn(addressEntity);
        when(converter.toResponse(addressEntity, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.updateAddress(addressRequest);

        // Assert
        assertNotNull(actualResponse, "AddressResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(addressRepository, times(1)).findById(addressEntity.getId());
        verify(userService, times(1)).findUserEntityById(addressRequest.getUserId());
        verify(converter, times(1)).toEntity(addressRequest, AddressEntity.class);
        verify(userService, times(1)).saveUserAddress(userEntity);
        verify(addressRepository, times(1)).save(addressEntity);
        verify(converter, times(1)).toResponse(addressEntity, AddressResponse.class);
    }

    @Test
    @DisplayName("updateAddress - Exceção ao tentar atualizar endereço inexistente")
    void updateAddress_NotFoundExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);

        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);

        when(addressRepository.findById(addressEntity.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> addressService.updateAddress(addressRequest), "Expected NotFoundException for update failure");

        verify(addressRepository, times(1)).findById(addressEntity.getId());
    }

    @Test
    @DisplayName("deleteAddress - Exclusão bem-sucedida do endereço")
    void deleteAddress_DeletesAddressSuccessfully() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressEntity));

        // Act
        addressService.deleteAddress(1L);

        // Assert
        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteAddress - Exceção ao tentar excluir endereço inexistente")
    void deleteAddress_NotFoundExceptionHandling() {
        // Arrange

        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(NotFoundException.class, () -> addressService.deleteAddress(1L), "Expected NotFoundException for non-existent address");

        // Assert
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deleteAddress - Exceção no repositório ao tentar excluir endereço inexistente")
    void deleteAddress_RepositoryExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        AddressEntity addressEntity = new AddressEntity(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressEntity.setUserEntity(userEntity);

        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);

        when(addressRepository.findById(addressEntity.getId())).thenReturn(Optional.of(addressEntity));
        doThrow(PersistenceException.class).when(addressRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.deleteAddress(1L), "Expected RepositoryException for delete failure");

        verify(addressRepository, times(1)).findById(addressEntity.getId());
    }
}
