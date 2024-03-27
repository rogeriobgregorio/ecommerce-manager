package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.User;
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
class AddressServiceImplTest {

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
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);
        List<Address> addressList = Collections.singletonList(address);

        AddressResponse addressResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        addressResponse.setUser(user);
        List<AddressResponse> expectedResponses = Collections.singletonList(addressResponse);

        when(converter.toResponse(address, AddressResponse.class)).thenReturn(addressResponse);
        when(addressRepository.findAll()).thenReturn(addressList);

        // Act
        List<AddressResponse> actualResponse = addressService.findAllAddresses();

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.size(), "Expected a list of responses with one address");
        assertIterableEquals(expectedResponses, actualResponse, "Expected a list of responses with one address");

        verify(converter, times(1)).toResponse(address, AddressResponse.class);
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllAddresses - Busca bem-sucedida retorna lista contendo múltiplos endereços")
    void findAllAddresses_SuccessfulSearch_ReturnsListResponse_MultipleAddresses() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        List<Address> addressList = new ArrayList<>();
        List<AddressResponse> expectedResponses = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Address address = new Address((long) i, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
            address.setUser(user);
            addressList.add(address);

            AddressResponse addressResponse = new AddressResponse((long) i, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
            addressResponse.setUser(user);
            expectedResponses.add(addressResponse);

            when(converter.toResponse(address, AddressResponse.class)).thenReturn(addressResponse);
        }

        when(addressRepository.findAll()).thenReturn(addressList);

        // Act
        List<AddressResponse> actualResponses = addressService.findAllAddresses();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with multiple address");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with multiple address");

        verify(converter, times(10)).toResponse(any(Address.class), eq(AddressResponse.class));
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
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);

        AddressRequest addressRequest = new AddressRequest("Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);
        AddressResponse expectedResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        when(userService.findUserById(addressRequest.getUserId())).thenReturn(user);
        when(converter.toEntity(addressRequest, Address.class)).thenReturn(address);
        doNothing().when(userService).saveUserAddress(user);
        when(addressRepository.save(address)).thenReturn(address);
        when(converter.toResponse(address, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.createAddress(addressRequest);

        // Assert
        assertNotNull(actualResponse, "AddressResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(userService, times(1)).findUserById(addressRequest.getUserId());
        verify(converter, times(1)).toEntity(addressRequest, Address.class);
        verify(userService, times(1)).saveUserAddress(user);
        verify(addressRepository, times(1)).save(address);
        verify(converter, times(1)).toResponse(address, AddressResponse.class);
    }

    @Test
    @DisplayName("createAddress - Exceção no repositório ao tentar criar endereço")
    void createAddress_RepositoryExceptionHandling() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);

        AddressRequest addressRequest = new AddressRequest("Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);

        when(userService.findUserById(addressRequest.getUserId())).thenReturn(user);
        when(converter.toEntity(addressRequest, Address.class)).thenReturn(address);
        doNothing().when(userService).saveUserAddress(user);
        when(addressRepository.save(address)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.createAddress(addressRequest), "Expected RepositoryException due to a PersistenceException");

        verify(userService, times(1)).findUserById(addressRequest.getUserId());
        verify(converter, times(1)).toEntity(addressRequest, Address.class);
        verify(userService, times(1)).saveUserAddress(user);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    @DisplayName("findAddressById - Busca bem-sucedida retorna endereço")
    void findAddressById_SuccessfulSearch_ReturnsOrderResponse() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);
        AddressResponse expectedResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(converter.toResponse(address, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.findAddressResponseById(1L);

        // Assert
        assertNotNull(actualResponse, "AddressResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(addressRepository, times(1)).findById(1L);
        verify(converter, times(1)).toResponse(address, AddressResponse.class);
    }

    @Test
    @DisplayName("findAddressById - Exceção ao tentar buscar endereço inexistente")
    void findAddressById_NotFoundExceptionHandling() {
        // Arrange
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert and Assert
        assertThrows(NotFoundException.class, () -> addressService.findAddressResponseById(1L), "Expected NotFoundException for non-existent address");

        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateAddress - Atualização bem-sucedida retorna endereço atualizado")
    void updateAddress_SuccessfulUpdate_ReturnsAddressResponse() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);

        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);
        AddressResponse expectedResponse = new AddressResponse(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(userService.findUserById(addressRequest.getUserId())).thenReturn(user);
        when(converter.toEntity(addressRequest, Address.class)).thenReturn(address);
        doNothing().when(userService).saveUserAddress(user);
        when(addressRepository.save(address)).thenReturn(address);
        when(converter.toResponse(address, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.updateAddress(addressRequest);

        // Assert
        assertNotNull(actualResponse, "AddressResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(addressRepository, times(1)).findById(address.getId());
        verify(userService, times(1)).findUserById(addressRequest.getUserId());
        verify(converter, times(1)).toEntity(addressRequest, Address.class);
        verify(userService, times(1)).saveUserAddress(user);
        verify(addressRepository, times(1)).save(address);
        verify(converter, times(1)).toResponse(address, AddressResponse.class);
    }

    @Test
    @DisplayName("updateAddress - Exceção ao tentar atualizar endereço inexistente")
    void updateAddress_NotFoundExceptionHandling() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);

        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> addressService.updateAddress(addressRequest), "Expected NotFoundException for update failure");

        verify(addressRepository, times(1)).findById(address.getId());
    }

    @Test
    @DisplayName("deleteAddress - Exclusão bem-sucedida do endereço")
    void deleteAddress_DeletesAddressSuccessfully() {
        // Arrange
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

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
        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
        address.setUser(user);

        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        doThrow(PersistenceException.class).when(addressRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.deleteAddress(1L), "Expected RepositoryException for delete failure");

        verify(addressRepository, times(1)).findById(address.getId());
    }
}
