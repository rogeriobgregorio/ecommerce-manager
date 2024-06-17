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
import com.rogeriogregorio.ecommercemanager.utils.CatchError.FunctionWithException;
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

        addressRequest = AddressRequest.newBuilder()
                .withUserId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withStreet("Rua ABC, 123").withCity("São Paulo").withState("SP")
                .withCep("01234-567").withCountry("Brasil")
                .build();

        addressResponse = AddressResponse.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withStreet("Rua ABC, 123").withCity("São Paulo").withState("SP")
                .withCep("01234-567").withCountry("Brasil").withUser(user)
                .build();

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
        when(catchError.run(any(FunctionWithException.class))).thenAnswer(invocation -> addressRepository.findAll(pageable));

        // Act
        Page<AddressResponse> actualResponse = addressService.findAllAddresses(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponse.getContent().size(), "Expected a list with one address");
        assertIterableEquals(expectedResponses, actualResponse.getContent(), "Expected a list with one address");
        verify(dataMapper, times(1)).map(address, AddressResponse.class);
        verify(addressRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(FunctionWithException.class));
    }

    @Test
    @DisplayName("findAllAddresses - Exceção ao tentar buscar lista de endereços")
    void findAllAddresses_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(addressRepository.findAll()).thenThrow(RepositoryException.class);
        doAnswer(invocation -> addressRepository.findAll()).when(catchError).run(any(FunctionWithException.class));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.findAllAddresses(pageable),
                "Expected PersistenceException to be thrown");
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createAddress - Criação bem-sucedida retorna endereço criado")
    void createAddress_SuccessfulCreation_ReturnsAddress() {
        // Arrange
        AddressResponse expectedResponse = addressResponse;

        when(userService.getUserIfExists(addressRequest.getUserId())).thenReturn(user);
        when(dataMapper.map(addressRequest, Address.class)).thenReturn(address);
        when(catchError.run(any(FunctionWithException.class))).thenAnswer(invocation -> addressRepository.save(address));
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
    }

    @Test
    @DisplayName("createAddress - Exceção no repositório ao tentar criar endereço")
    void createAddress_RepositoryExceptionHandling() {
        // Arrange
        when(userService.getUserIfExists(addressRequest.getUserId())).thenReturn(user);
        when(dataMapper.map(addressRequest, Address.class)).thenReturn(address);
        doAnswer(invocation -> addressRepository.save(address)).when(catchError).run(any(FunctionWithException.class));
        when(addressRepository.save(address)).thenThrow(RepositoryException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> addressService.createAddress(addressRequest),
                "Expected RepositoryException due to a PersistenceException");
        verify(userService, times(1)).getUserIfExists(addressRequest.getUserId());
        verify(dataMapper, times(1)).map(addressRequest, Address.class);
        verify(userService, times(1)).saveUserAddress(user);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    @DisplayName("findAddressById - Busca bem-sucedida retorna endereço")
    void findAddressById_SuccessfulSearch_ReturnsOrder() {
        // Arrange
        AddressResponse expectedResponse = addressResponse;

        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(dataMapper.map(address, AddressResponse.class)).thenReturn(expectedResponse);
        when(catchError.run(any(FunctionWithException.class))).thenAnswer(invocation -> addressRepository.findById(address.getId()));

        // Act
        AddressResponse actualResponse = addressService.findAddressById(address.getId());

        // Assert
        assertNotNull(actualResponse, "Address should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(dataMapper, times(1)).map(address, AddressResponse.class);
    }

    @Test
    @DisplayName("findAddressById - Exceção ao tentar buscar endereço inexistente")
    void findAddressById_NotFoundExceptionHandling() {
        // Arrange
        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());
        doAnswer(invocation ->addressRepository.findById(address.getId())).when(catchError).run(any(FunctionWithException.class));

        // Assert and Assert
        assertThrows(NotFoundException.class, () -> addressService.findAddressById(address.getId()),
                "Expected NotFoundException for non-existent address");
        verify(addressRepository, times(1)).findById(address.getId());
    }

    @Test
    @DisplayName("updateAddress - Atualização bem-sucedida retorna endereço atualizado")
    void updateAddress_SuccessfulUpdate_ReturnsAddress() {
        // Arrange
        AddressResponse expectedResponse = addressResponse;

        when(userService.getUserIfExists(addressRequest.getUserId())).thenReturn(user);
        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
        when(dataMapper.map(addressRequest, Address.class)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(catchError.run(any(FunctionWithException.class))).thenAnswer(invocation -> addressRepository.save(address));
        when(dataMapper.map(address, AddressResponse.class)).thenReturn(expectedResponse);

        // Act
        AddressResponse actualResponse = addressService.updateAddress(address.getId(), addressRequest);

        // Assert
        assertNotNull(actualResponse, "Address should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(addressRepository, times(1)).findById(address.getId());
        verify(userService, times(1)).getUserIfExists(addressRequest.getUserId());
        verify(dataMapper, times(1)).map(addressRequest, Address.class);
        verify(addressRepository, times(1)).save(address);
        verify(dataMapper, times(1)).map(address, AddressResponse.class);
    }

//
//    @Test
//    @DisplayName("updateAddress - Exceção ao tentar atualizar endereço inexistente")
//    void updateAddress_NotFoundExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//
//        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
//        address.setUser(user);
//
//        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);
//
//        when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());
//
//        // Act and Assert
//        assertThrows(NotFoundException.class, () -> addressService.updateAddress(addressRequest), "Expected NotFoundException for update failure");
//
//        verify(addressRepository, times(1)).findById(address.getId());
//    }
//
//    @Test
//    @DisplayName("deleteAddress - Exclusão bem-sucedida do endereço")
//    void deleteAddress_DeletesAddressSuccessfully() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//
//        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
//        address.setUser(user);
//
//        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
//
//        // Act
//        addressService.deleteAddress(1L);
//
//        // Assert
//        verify(addressRepository, times(1)).findById(1L);
//        verify(addressRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("deleteAddress - Exceção ao tentar excluir endereço inexistente")
//    void deleteAddress_NotFoundExceptionHandling() {
//        // Arrange
//
//        when(addressRepository.findById(1L)).thenReturn(Optional.empty());
//
//        // Act
//        assertThrows(NotFoundException.class, () -> addressService.deleteAddress(1L), "Expected NotFoundException for non-existent address");
//
//        // Assert
//        verify(addressRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    @DisplayName("deleteAddress - Exceção no repositório ao tentar excluir endereço inexistente")
//    void deleteAddress_RepositoryExceptionHandling() {
//        // Arrange
//        User user = new User(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
//
//        Address address = new Address(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil");
//        address.setUser(user);
//
//        AddressRequest addressRequest = new AddressRequest(1L, "Rua ABC, 123", "São Paulo", "SP", "01234-567", "Brasil", 1L);
//
//        when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
//        doThrow(PersistenceException.class).when(addressRepository).deleteById(1L);
//
//        // Act and Assert
//        assertThrows(RepositoryException.class, () -> addressService.deleteAddress(1L), "Expected RepositoryException for delete failure");
//
//        verify(addressRepository, times(1)).findById(address.getId());
//    }
}
