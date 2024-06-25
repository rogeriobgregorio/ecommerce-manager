package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.UserServiceImpl;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeFunction;
import com.rogeriogregorio.ecommercemanager.utils.CatchError.SafeProcedure;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.PasswordHelper;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordHelper passwordHelper;

    @Mock
    private CatchError catchError;

    @Mock
    private DataMapper dataMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private static User user;
    private static UserRequest userRequest;
    private static UserResponse userResponse;

    UserServiceImplTest() {
    }

    @BeforeEach
    void setUp() {

        user = User.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withName("Admin").withEmail("admin@email.com").withPhone("11912345678")
                .withCpf("72482581052").withPassword("Password123$").withRole(UserRole.ADMIN)
                .build();

        Address address = Address.newBuilder()
                .withId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .withStreet("Rua ABC, 123").withCity("São Paulo").withState("SP")
                .withCep("01234-567").withCountry("Brasil").withUser(user)
                .build();

        userRequest = new UserRequest(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "Admin", "admin@email.com", "11912345678",
                "72482581052", "Password123$", UserRole.ADMIN);

        userResponse = new UserResponse(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "Admin", "admin@email.com", "11912345678",
                "72482581052", address, UserRole.ADMIN);

        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, mailService, passwordHelper, catchError, dataMapper);
    }

    @Test
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista de usuários")
    void findAllUsers_SuccessfulSearch_ReturnsUserList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Collections.singletonList(user);
        List<UserResponse> expectedResponses = Collections.singletonList(userResponse);
        PageImpl<User> page = new PageImpl<>(userList, pageable, userList.size());

        when(dataMapper.map(user, UserResponse.class)).thenReturn(userResponse);
        when(userRepository.findAll(pageable)).thenReturn(page);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findAll(pageable));

        // Act
        Page<UserResponse> actualResponses = userService.findAllUsers(pageable);

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.getContent().size(), "Expected a list with one object");
        assertIterableEquals(expectedResponses, actualResponses, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(user, UserResponse.class);
        verify(userRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findAllUsers - Exceção ao tentar buscar lista de usuários")
    void findAllUsers_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findAll(pageable));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.findAllUsers(pageable),
                "Expected RepositoryException to be thrown");
        verify(userRepository, times(1)).findAll(pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("registerUser - Registro bem-sucedida retorna usuário registrado")
    void registerUser_SuccessfulRegister_ReturnsUser() {
        // Arrange
        UserResponse expectedResponse = userResponse;

        doNothing().when(passwordHelper).validate(userRequest.getPassword());
        when(passwordHelper.enconde(userRequest.getPassword())).thenReturn(String.valueOf(String.class));
        when(dataMapper.map(userRequest, User.class)).thenReturn(user);
        when(dataMapper.map(user, UserResponse.class)).thenReturn(expectedResponse);
        when(userRepository.save(user)).thenReturn(user);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.save(user));

        // Act
        UserResponse actualResponse = userService.registerUser(userRequest);

        // Assert
        assertNotNull(actualResponse, "User should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(passwordHelper, times(1)).validate(userRequest.getPassword());
        verify(passwordHelper, times(1)).enconde(userRequest.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(dataMapper, times(1)).map(userRequest, User.class);
        verify(dataMapper, times(1)).map(user, UserResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("registerUser - Exceção no repositório ao tentar registrar usuário")
    void registerUser_RepositoryExceptionHandling() {
        // Arrange
        doNothing().when(passwordHelper).validate(userRequest.getPassword());
        when(passwordHelper.enconde(userRequest.getPassword())).thenReturn(String.valueOf(String.class));
        when(dataMapper.map(userRequest, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.save(user));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.registerUser(userRequest),
                "Expected RepositoryException to be thrown");
        verify(passwordHelper, times(1)).validate(userRequest.getPassword());
        verify(passwordHelper, times(1)).enconde(userRequest.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(dataMapper, times(1)).map(userRequest, User.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findUserById - Busca bem-sucedida retorna usuário")
    void findUserById_SuccessfulSearch_ReturnsUser() {
        // Arrange
        UserResponse expectedResponse = userResponse;

        when(dataMapper.map(user, UserResponse.class)).thenReturn(expectedResponse);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findById(user.getId()));

        // Act
        UserResponse actualResponse = userService.findUserById(user.getId());

        // Assert
        assertNotNull(actualResponse, "User should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(dataMapper, times(1)).map(user, UserResponse.class);
        verify(userRepository, times(1)).findById(user.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findUserById - Exceção ao tentar buscar usuário inexistente")
    void findUserById_NotFoundExceptionHandling() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findById(user.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.findUserById(user.getId()),
                "Expected NotFoundException to be thrown");
        verify(userRepository, times(1)).findById(user.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findUserById - Exceção no repositório ao tentar buscar usuário")
    void findUserById_RepositoryExceptionHandling() {
        // Arrange
        when(userRepository.findById(user.getId())).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findById(user.getId()));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.findUserById(user.getId()),
                "Expected RepositoryException to be thrown");
        verify(userRepository, times(1)).findById(user.getId());
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateUser - Atualização bem-sucedida retorna usuário atualizado")
    void updateUser_SuccessfulUpdate_ReturnsUser() {
        // Arrange
        UserResponse expectedResponse = userResponse;

        doNothing().when(passwordHelper).validate(userRequest.getPassword());
        when(passwordHelper.enconde(userRequest.getPassword())).thenReturn(String.valueOf(String.class));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(dataMapper.map(eq(userRequest), any(User.class))).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(dataMapper.map(eq(user), eq(UserResponse.class))).thenReturn(expectedResponse);
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation.getArgument(0, SafeFunction.class).execute());

        // Act
        UserResponse actualResponse = userService.updateUser(user.getId(), userRequest);

        // Assert
        assertNotNull(actualResponse, "User should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(expectedResponse, actualResponse, "Expected and actual should match");
        verify(passwordHelper, times(1)).validate(userRequest.getPassword());
        verify(passwordHelper, times(1)).enconde(userRequest.getPassword());
        verify(userRepository, times(1)).findById(user.getId());
        verify(dataMapper, times(1)).map(eq(userRequest), any(User.class));
        verify(userRepository, times(1)).save(user);
        verify(dataMapper, times(1)).map(eq(user), eq(UserResponse.class));
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateUser - Exceção ao tentar atualizar usuário inexistente")
    void updateUser_NotFoundExceptionHandling() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findById(user.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.findUserById(user.getId()),
                "Expected NotFoundException to be thrown");
        verify(userRepository, times(1)).findById(userRequest.getId());
        verify(userRepository, never()).save(user);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("updateUser - Exceção no repositório ao tentar atualizar usuário")
    void updateUser_RepositoryExceptionHandling() {
        // Arrange
        doNothing().when(passwordHelper).validate(userRequest.getPassword());
        when(passwordHelper.enconde(userRequest.getPassword())).thenReturn(String.valueOf(String.class));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(dataMapper.map(eq(userRequest), any(User.class))).thenReturn(user);
        when(userRepository.save(user)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).then(invocation -> invocation.getArgument(0, SafeFunction.class).execute());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.updateUser(user.getId(), userRequest),
                "Expected RepositoryException to be thrown");
        verify(passwordHelper, times(1)).validate(userRequest.getPassword());
        verify(passwordHelper, times(1)).enconde(userRequest.getPassword());
        verify(dataMapper, times(1)).map(eq(userRequest), any(User.class));
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
        verify(catchError, times(2)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteUser - Exclusão bem-sucedida do usuário")
    void deleteUser_DeletesSuccessfully() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> userRepository.findById(user.getId()));
        doAnswer(invocation -> {
            userRepository.delete(user);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doNothing().when(userRepository).delete(user);

        // Act
        userService.deleteUser(user.getId());

        // Assert
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).delete(user);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("deleteUser - Exceção ao tentar excluir usuário inexistente")
    void deleteUser_NotFoundExceptionHandling() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(catchError.run(any(SafeFunction.class))).then(invocation -> userRepository.findById(user.getId()));

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.deleteUser(user.getId()),
                "Expected NotFoundException to be thrown");
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, never()).delete(user);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("deleteUser - Exceção no repositório ao tentar excluir usuário")
    void deleteUser_RepositoryExceptionHandling() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(catchError.run(any(SafeFunction.class))).then(invocation -> userRepository.findById(user.getId()));
        doAnswer(invocation -> {
            userRepository.delete(user);
            return null;
        }).when(catchError).run(any(SafeProcedure.class));
        doThrow(RepositoryException.class).when(userRepository).delete(user);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.deleteUser(user.getId()),
                "Expected RepositoryException to be thrown");
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).delete(user);
        verify(catchError, times(1)).run(any(SafeFunction.class));
        verify(catchError, times(1)).run(any(SafeProcedure.class));
    }

    @Test
    @DisplayName("findUserByName - Busca bem-sucedida pelo nome retorna lista de usuários")
    void findUserByName_SuccessfulSearch_ReturnsUserList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Collections.singletonList(user);
        List<UserResponse> expectedResponses = Collections.singletonList(userResponse);
        PageImpl<User> page = new PageImpl<>(userList, pageable, userList.size());

        when(dataMapper.map(user, UserResponse.class)).thenReturn(userResponse);
        when(userRepository.findByName(user.getName(), pageable)).thenReturn(page);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findByName(user.getName(), pageable));

        // Act
        Page<UserResponse> actualResponses = userService.findUserByName(user.getName(), pageable);

        // Assert
        assertNotNull(actualResponses, "User should not be null");
        assertEquals(expectedResponses, actualResponses.getContent(), "Expected and actual responses should be equal");
        assertEquals(user.getName(), actualResponses.getContent().get(0).getName(), "Names should match");
        verify(userRepository, times(1)).findByName(user.getName(), pageable);
        verify(dataMapper, times(1)).map(user, UserResponse.class);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }

    @Test
    @DisplayName("findUserByName - Exceção no repositório ao tentar buscar usuário pelo nome")
    void findUserByName_RepositoryExceptionHandling() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByName(user.getName(), pageable)).thenThrow(RepositoryException.class);
        when(catchError.run(any(SafeFunction.class))).thenAnswer(invocation -> userRepository.findByName(user.getName(), pageable));

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.findUserByName(user.getName(), pageable),
                "Expected RepositoryException to be thrown");
        verify(userRepository, times(1)).findByName(user.getName(), pageable);
        verify(catchError, times(1)).run(any(SafeFunction.class));
    }
}
