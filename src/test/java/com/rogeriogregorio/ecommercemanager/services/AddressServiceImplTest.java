package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.AddressRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.AddressServiceImpl;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserService userService;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private static User user;
    private static Address address;
    private static AddressRequest addressRequest;
    private static AddressResponse addressResponse;

    AddressServiceImplTest() {
    }

    @BeforeEach
    void setUp() {

        user = User.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withName("Admin").withEmail("admin@email.com").withPhone("11912345678")
                .withCpf("72482581052").withPassword("Password123$").withRole(UserRole.ADMIN)
                .build();

        address = Address.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withStreet("Rua ABC, 123").withCity("São Paulo").withState("SP")
                .withCep("01234-567").withCountry("Brasil").withUser(user)
                .build();

        addressRequest = new AddressRequest("Rua ABC, 123", "São Paulo", "SP", "01234-567",
                "Brasil", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        addressResponse = new AddressResponse(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", user);

        MockitoAnnotations.openMocks(this);
        addressService = new AddressServiceImpl(addressRepository, userService, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllAddresses - Busca bem-sucedida retorna lista de endereços")
    void findAllAddresses_SuccessfulSearch_ReturnsAddressesList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Address> addressList = Collections.singletonList(address);
        List<AddressResponse> expectedResponses = Collections.singletonList(addressResponse);
        PageImpl<Address> page = new PageImpl<>(addressList, pageable, addressList.size());

        when(dataMapper.map(address, AddressResponse.class)).thenReturn(addressResponse);
        when(addressRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> addressRepository.findAll(pageable));

        // Act
        Page<AddressResponse> actualResponse = addressService.findAllAddresses(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponse.getContent(), "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(address, AddressResponse.class);
        verify(addressRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllAddresses - Exceção no repositório tentar buscar lista de endereços")
    void findAllAddresses_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(addressRepository.findAll()).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> addressRepository.findAll());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.findAllAddresses(pageable),
                "Expected PersistenceException to be thrown");
        verify(addressRepository, times(1)).findAll();
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createAddress - Criação bem-sucedida retorna endereço criado")
    void createAddress_SuccessfulCreation_ReturnsAddress() {
        // Arrange
        AddressResponse expectedResponse = addressResponse;

        when(userService.getUserIfExists(addressRequest.getUserId())).thenReturn(user);
        when(dataMapper.map(addressRequest, Address.class)).thenReturn(address);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> addressRepository.save(address));
        when(addressRepository.save(address)).thenReturn(address);
        when(dataMapper.map(address, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.createAddress(addressRequest);

        // Assert
        assertNotNull(actualResponse, "Address should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(userService, times(1)).getUserIfExists(addressRequest.getUserId());
        verify(dataMapper, times(1)).map(addressRequest, Address.class);
        verify(userService, times(1)).saveUserAddress(user);
        verify(addressRepository, times(1)).save(address);
        verify(dataMapper, times(1)).map(address, AddressResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("createAddress - Exceção no repositório ao tentar criar endereço")
    void createAddress_RepositoryExceptionHandling() {
        // Arrange
        when(userService.getUserIfExists(addressRequest.getUserId())).thenReturn(user);
        when(dataMapper.map(addressRequest, Address.class)).thenReturn(address);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> addressRepository.save(address));
        when(addressRepository.save(address)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.createAddress(addressRequest),
                "Expected RepositoryException to be thrown");
        verify(userService, times(1)).getUserIfExists(addressRequest.getUserId());
        verify(dataMapper, times(1)).map(addressRequest, Address.class);
        verify(userService, times(1)).saveUserAddress(user);
        verify(addressRepository, times(1)).save(address);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAddressById - Busca bem-sucedida retorna endereço")
    void findAddressById_SuccessfulSearch_ReturnsAddress() {
        // Arrange
        AddressResponse expectedResponse = addressResponse;

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(dataMapper.map(address, AddressResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> addressRepository.findById(address.getId()));

        // Act
        AddressResponse actualResponse = addressService.findAddressById(address.getId());

        // Assert
        assertNotNull(actualResponse, "Address should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(dataMapper, times(1)).map(address, AddressResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAddressById - Exceção ao tentar buscar endereço inexistente")
    void findAddressById_NotFoundExceptionHandling() {
        // Arrange
        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> addressRepository.findById(address.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> addressService.findAddressById(address.getId()),
                "Expected NotFoundException to be thrown");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAddressById - Exceção no repositório ao tentar buscar endereço")
    void findAddressById_RepositoryExceptionHandling() {
        // Arrange
        when(addressRepository.findById(address.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> addressRepository.findById(address.getId()));

        // Assert and Assert
        assertThrows(RepositoryException.class, () -> addressService.findAddressById(address.getId()),
                "Expected RepositoryException to be thrown");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateAddress - Atualização bem-sucedida retorna endereço atualizado")
    void updateAddress_SuccessfulUpdate_ReturnsAddress() {
        // Arrange
        AddressResponse expectedResponse = addressResponse;

        when(userService.getUserIfExists(addressRequest.getUserId())).thenReturn(user);
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation.getArgument(0, SafeFunction.class).execute());
        when(dataMapper.map(eq(addressRequest), any(Address.class))).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(dataMapper.map(eq(address), eq(AddressResponse.class))).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.updateAddress(address.getId(), addressRequest);

        // Assert
        assertNotNull(actualResponse, "Address should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(userService, times(1)).getUserIfExists(addressRequest.getUserId());
        verify(dataMapper, times(1)).map(eq(addressRequest), any(Address.class));
        verify(addressRepository, times(1)).save(address);
        verify(dataMapper, times(1)).map(eq(address), eq(AddressResponse.class));
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateAddress - Exceção ao tentar atualizar endereço inexistente")
    void updateAddress_NotFoundExceptionHandling() {
        // Arrange
        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> addressRepository.findById(address.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> addressService.updateAddress(address.getId(), addressRequest),
                "Expected NotFoundException to be thrown");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(addressRepository, never()).save(address);
    }

    @Test
    @DisplayName("updateAddress - Exceção no repositório ao tentar atualizar endereço")
    void updateAddress_RepositoryExceptionHandling() {
        // Arrange
        when(userService.getUserIfExists(addressRequest.getUserId())).thenReturn(user);
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation.getArgument(0, SafeFunction.class).execute());
        when(dataMapper.map(eq(addressRequest), any(Address.class))).thenReturn(address);
        when(addressRepository.save(address)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.updateAddress(address.getId(), addressRequest),
                "Expected RepositoryException to be thrown");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteAddress - Exclusão bem-sucedida do endereço")
    void deleteAddress_DeletesAddressSuccessfully() {
        // Arrange
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> addressRepository.findById(address.getId()));
        doAnswer(invocation -> {
            addressRepository.delete(address);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doNothing().when(addressRepository).delete(address);

        // Act
        addressService.deleteAddress(address.getId());

        // Assert
        verify(addressRepository, times(1)).findById(address.getId());
        verify(addressRepository, times(1)).delete(address);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }


    @Test
    @DisplayName("deleteAddress - Exceção ao tentar excluir endereço inexistente")
    void deleteAddress_NotFoundExceptionHandling() {
        // Arrange
        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> addressRepository.findById(address.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> addressService.deleteAddress(address.getId()),
                "Expected NotFoundException to be thrown");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(addressRepository, never()).delete(address);
    }

    @Test
    @DisplayName("deleteAddress - Exceção no repositório ao tentar excluir endereço")
    void deleteAddress_RepositoryExceptionHandling() {
        // Arrange
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> addressRepository.findById(address.getId()));
        doAnswer(invocation -> {
            addressRepository.delete(address);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doThrow(RepositoryException.class).when(addressRepository).delete(address);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.deleteAddress(address.getId()),
                "Expected RepositoryException to be thrown");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(addressRepository, times(1)).delete(address);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }
}
