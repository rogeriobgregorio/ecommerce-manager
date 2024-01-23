package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.*;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.UserServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.UserConverter;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, userConverter);
    }

    @Test
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista de usuários")
    void findAllUsers_SuccessfulSearch_ReturnsListResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");
        List<UserEntity> userEntityList = Collections.singletonList(userEntity);

        UserResponse userResponse = new UserResponse(null, "João Silva", "joao@email.com", "11912345678");
        List<UserResponse> expectedResponses = Collections.singletonList(userResponse);

        when(userConverter.entityToResponse(userEntity)).thenReturn(userResponse);
        when(userRepository.findAll()).thenReturn(userEntityList);

        // Act
        List<UserResponse> actualResponses = userService.findAllUsers();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size());
        assertIterableEquals(expectedResponses, actualResponses);

        verify(userConverter, times(1)).entityToResponse(any());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista de usuários vazia")
    void findAllUsers_SuccessfulSearch_ReturnsEmptyList() {
        /// Arrange
        List<UserEntity> emptyUserEntityList = Collections.emptyList();

        when(userRepository.findAll()).thenReturn(emptyUserEntityList);

        // Act
        List<UserResponse> actualResponses = userService.findAllUsers();

        // Assert
        assertEquals(0, actualResponses.size(), "Expected an empty list of responses");
        assertIterableEquals(emptyUserEntityList, actualResponses, "Expected an empty list of responses");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllUsers - Exceção ao consultar lista de usuários")
    void findAllUsers_UserQueryExceptionHandling() {
        // Arrange
        when(userRepository.findAll()).thenThrow(new RuntimeException("Simulating a repository exception"));

        // Act and Assert
        assertThrows(UserQueryException.class, () -> userService.findAllUsers(), "Expected UserQueryException to be thrown");
    }

    @Test
    @DisplayName("createUser - Criação bem-sucedida retorna usuário criado")
    void createUser_SuccessfulCreation_ReturnsUserResponse() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse expectedResponse = new UserResponse(null, "João Silva", "joao@email.com", "11912345678");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userConverter.entityToResponse(userEntity)).thenReturn(expectedResponse);
        when(userRepository.save(any())).thenReturn(userEntity);

        // Act
        UserResponse actualResponse = userService.createUser(userRequest);

        // Assert
        assertNotNull(actualResponse, "UserResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(userRepository, times(1)).save(any());
        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userConverter, times(1)).entityToResponse(userEntity);
    }

    @Test
    @DisplayName("createUser - Exceção ao criar usuário com e-mail já registrado")
    void createUser_UserCreateException_EmailAlreadyRegistered() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(DataIntegrityViolationException.class);

        // Act and Assert
        assertThrows(UserCreateException.class, () -> userService.createUser(userRequest), "Expected UserCreateException due to duplicate email");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createUser - Exceção genérica ao criar usuário")
    void createUser_UserCreateException_Generic() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(UserCreateException.class, () -> userService.createUser(userRequest), "Expected UserCreateException due to a generic runtime exception");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("findUserById - Busca bem-sucedida retorna usuário")
    void findUserById_SuccessfulSearch_ReturnsUserResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse expectedResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");

        when(userConverter.entityToResponse(userEntity)).thenReturn(expectedResponse);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Act
        UserResponse actualResponse = userService.findUserById(1L);

        // Assert
        assertNotNull(actualResponse, "UserResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");
        verify(userConverter, times(1)).entityToResponse(userEntity);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findUserById - Exceção ao buscar usuário inexistente")
    void findUserById_UserNotFoundExceptionHandling() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L), "Expected UserNotFoundException for non-existent user");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateUser - Atualização bem-sucedida retorna usuário atualizado")
    void updateUser_SuccessfulUpdate_ReturnsUserResponse() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse expectedResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.existsById(userEntity.getId())).thenReturn(true);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userConverter.entityToResponse(userEntity)).thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse = userService.updateUser(userRequest);

        // Assert
        assertNotNull(actualResponse, "UserResponse should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(userRequest.getName(), actualResponse.getName(), "Names should match");
        assertEquals(userRequest.getEmail(), actualResponse.getEmail(), "Emails should match");
        assertEquals(userRequest.getPhone(), actualResponse.getPhone(), "Phones should match");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).existsById(userEntity.getId());
        verify(userRepository, times(1)).save(userEntity);
        verify(userConverter, times(1)).entityToResponse(userEntity);
    }

    @Test
    @DisplayName("updateUser - Exceção ao atualizar usuário inexistente")
    void updateUser_UserNotFoundExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.existsById(userEntity.getId())).thenReturn(false);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userRequest), "Expected UserNotFoundException for non-existent user");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).existsById(userEntity.getId());
    }

    @Test
    @DisplayName("updateUser - Exceção ao atualizar usuário")
    void updateUser_UserUpdateExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L,"João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.existsById(userEntity.getId())).thenReturn(true);
        when(userRepository.save(userEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(UserUpdateException.class, () -> userService.updateUser(userRequest), "Expected UserUpdateException for update failure");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).existsById(userEntity.getId());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("deleteUser - Exclusão bem-sucedida do usuário")
    void deleteUser_DeletesUserSuccessfully() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }


    @Test
    @DisplayName("deleteUser - Exceção ao excluir usuário inexistente")
    void deleteUser_UserNotFoundExceptionHandling() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId), "Expected UserNotFoundException for non-existent user");
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName("deleteUser - Exceção ao excluir usuário")
    void deleteUser_UserDeleteExceptionHandling() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doThrow(RuntimeException.class).when(userRepository).deleteById(userId);

        // Act and Assert
        assertThrows(UserDeleteException.class, () -> userService.deleteUser(userId), "Expected UserDeleteException for delete failure");
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("findUserByName - Busca bem-sucedida pelo nome retorna lista de usuários")
    void findUserByName_SuccessfulSearch_ReturnsListUsersResponse() {
        // Arrange
        String userName = "João Silva";
        List<UserEntity> userEntities = List.of(
                new UserEntity(userName, "joao@email.com", "11912345678", "senha123")
        );

        when(userRepository.findByName(userName)).thenReturn(userEntities);
        when(userConverter.entityToResponse(any())).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            return new UserResponse(entity.getId(), entity.getName(), entity.getEmail(), entity.getPhone());
        });

        // Act
        List<UserResponse> userResponses = userService.findUserByName(userName);

        // Assert
        assertNotNull(userResponses, "UserResponses should not be null");
        assertFalse(userResponses.isEmpty(), "UserResponses should not be empty");
        assertEquals(userEntities.size(), userResponses.size(), "Size of UserResponses should match size of UserEntities");
        assertEquals(userName, userResponses.get(0).getName(), "Names should match");

        verify(userRepository, times(1)).findByName(userName);
        verify(userConverter, times(1)).entityToResponse(any());
    }

    @Test
    @DisplayName("findUserByName - Exceção ao buscar usuário inexistente pelo nome")
    void findUserByName_UserNotFoundExceptionHandling() {
        // Arrange
        String userName = "Inexistente";
        when(userRepository.findByName(userName)).thenReturn(Collections.emptyList());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserByName(userName), "Expected UserNotFoundException for non-existent user");

        verify(userRepository, times(1)).findByName(userName);
    }

    @Test
    @DisplayName("findUserByName - Exceção ao buscar usuário pelo nome")
    void findUserByName_UserQueryExceptionHandling() {
        // Arrange
        String userName = "Erro";
        when(userRepository.findByName(userName)).thenReturn(List.of(new UserEntity(userName, "email", "phone", "password")));
        when(userConverter.entityToResponse(any())).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(UserQueryException.class, () -> userService.findUserByName(userName), "Expected UserQueryException for conversion failure");

        verify(userRepository, times(1)).findByName(userName);
        verify(userConverter, times(1)).entityToResponse(any());
    }

    @Test
    @DisplayName("validateUser - Validação bem-sucedida do usuário")
    void validateUser_SuccessfulValidation() {
        // Arrange
        UserEntity validUser = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        // Act and Assert
        assertDoesNotThrow(() -> userService.validateUser(validUser), "Validation should not throw an exception");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(validUser);

        assertTrue(violations.isEmpty(), "No constraint violations expected for a valid user");
    }

    @Test
    @DisplayName("validateUser - Exceção ao validar usuário com violação de restrição")
    void validateUser_ConstraintViolationExceptionHandling() {
        // Arrange
        UserEntity invalidUser = new UserEntity("", "invalid_email", "invalid_phone", "");

        // Act and Assert
        assertThrows(
                ConstraintViolationException.class,
                () -> userService.validateUser(invalidUser),
                "Expected ConstraintViolationException for invalid user"
        );
    }
}
