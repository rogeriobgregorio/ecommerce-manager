package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.DataIntegrityException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.UserServiceImpl;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Converter converter;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, converter);
    }

    @Test
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista contendo um usuário")
    void findAllUsers_SuccessfulSearch_ReturnsListResponse_OneUser() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        List<UserEntity> userEntityList = Collections.singletonList(userEntity);

        UserResponse userResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");
        List<UserResponse> expectedResponses = Collections.singletonList(userResponse);

        when(converter.toResponse(userEntity, UserResponse.class)).thenReturn(userResponse);
        when(userRepository.findAll()).thenReturn(userEntityList);

        // Act
        List<UserResponse> actualResponses = userService.findAllUsers();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with one user");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with one user");

        verify(converter, times(1)).toResponse(userEntity, UserResponse.class);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista contendo múltiplos usuários")
    void findAllUsers_SuccessfulSearch_ReturnsListResponse_MultipleUsers() {
        // Arrange
        List<UserEntity> userEntityList = new ArrayList<>();
        List<UserResponse> expectedResponses = new ArrayList<>();

        for (long i = 1; i <= 10; i++) {
            UserEntity userEntity = new UserEntity(i, "Usuário " + i, "user" + i + "@email.com",
                    "1191234567" + i, "senha123");
            userEntityList.add(userEntity);

            UserResponse userResponse = new UserResponse(i, "Usuário " + i, "user" + i + "@email.com",
                    "1191234567" + i);
            expectedResponses.add(userResponse);

            when(converter.toResponse(userEntity, UserResponse.class)).thenReturn(userResponse);
        }

        when(userRepository.findAll()).thenReturn(userEntityList);

        // Act
        List<UserResponse> actualResponses = userService.findAllUsers();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size(), "Expected a list of responses with ten users");
        assertIterableEquals(expectedResponses, actualResponses, "Expected a list of responses with ten users");

        verify(converter, times(10)).toResponse(any(UserEntity.class), eq(UserResponse.class));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista de usuários vazia")
    void findAllUsers_SuccessfulSearch_ReturnsEmptyList() {
        // Arrange
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
    @DisplayName("findAllUsers - Exceção ao tentar buscar lista de usuários")
    void findAllUsers_RepositoryExceptionHandling() {
        // Arrange
        when(userRepository.findAll()).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.findAllUsers(), "Expected RepositoryException to be thrown");

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("createUser - Criação bem-sucedida retorna usuário criado")
    void createUser_SuccessfulCreation_ReturnsUserResponse() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse expectedResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");

        when(converter.toEntity(userRequest, UserEntity.class)).thenReturn(userEntity);
        when(converter.toResponse(userEntity, UserResponse.class)).thenReturn(expectedResponse);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        UserResponse actualResponse = userService.createUser(userRequest);

        // Assert
        assertNotNull(actualResponse, "UserResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(userRepository, times(1)).save(userEntity);
        verify(converter, times(1)).toEntity(userRequest, UserEntity.class);
        verify(converter, times(1)).toResponse(userEntity, UserResponse.class);
    }

    @Test
    @DisplayName("createUser - Exceção ao tentar criar usuário com e-mail já registrado")
    void createUser_DataException_EmailAlreadyRegistered() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(converter.toEntity(userRequest, UserEntity.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(DataIntegrityViolationException.class);

        // Act and Assert
        assertThrows(DataIntegrityException.class, () -> userService.createUser(userRequest), "Expected DataIntegrityException due to duplicate email");

        verify(converter, times(1)).toEntity(userRequest, UserEntity.class);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("createUser - Exceção no repositório ao tentar criar usuário")
    void createUser_RepositoryExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(converter.toEntity(userRequest, UserEntity.class)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.createUser(userRequest), "Expected RepositoryException due to a generic runtime exception");

        verify(converter, times(1)).toEntity(userRequest, UserEntity.class);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("findUserById - Busca bem-sucedida retorna usuário")
    void findUserById_SuccessfulSearch_ReturnsUserResponse() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse expectedResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");

        when(converter.toResponse(userEntity, UserResponse.class)).thenReturn(expectedResponse);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Act
        UserResponse actualResponse = userService.findUserById(1L);

        // Assert
        assertNotNull(actualResponse, "UserResponse should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected and actual responses should be equal");

        verify(converter, times(1)).toResponse(userEntity, UserResponse.class);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findUserById - Exceção ao tentar buscar usuário inexistente")
    void findUserById_NotFoundExceptionHandling() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.findUserById(1L), "Expected NotFoundException for non-existent user");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateUser - Atualização bem-sucedida retorna usuário atualizado")
    void updateUser_SuccessfulUpdate_ReturnsUserResponse() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse expectedResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");

        when(converter.toEntity(userRequest, UserEntity.class)).thenReturn(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(converter.toResponse(userEntity, UserResponse.class)).thenReturn(expectedResponse);

        // Act
        UserResponse actualResponse = userService.updateUser(userRequest);

        // Assert
        assertNotNull(actualResponse, "UserResponse should not be null");
        assertEquals(expectedResponse.getId(), actualResponse.getId(), "IDs should match");
        assertEquals(userRequest.getName(), actualResponse.getName(), "Names should match");
        assertEquals(userRequest.getEmail(), actualResponse.getEmail(), "Emails should match");
        assertEquals(userRequest.getPhone(), actualResponse.getPhone(), "Phones should match");

        verify(converter, times(1)).toEntity(userRequest, UserEntity.class);
        verify(userRepository, times(1)).findById(userEntity.getId());
        verify(userRepository, times(1)).save(userEntity);
        verify(converter, times(1)).toResponse(userEntity, UserResponse.class);
    }

    @Test
    @DisplayName("updateUser - Exceção ao tentar atualizar usuário inexistente")
    void updateUser_NotFoundExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(converter.toEntity(userRequest, UserEntity.class)).thenReturn(userEntity);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.updateUser(userRequest), "Expected NotFoundException for non-existent user");

        verify(converter, times(1)).toEntity(userRequest, UserEntity.class);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("updateUser - Exceção no repositório ao tentar atualizar usuário")
    void updateUser_RepositoryExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(converter.toEntity(userRequest, UserEntity.class)).thenReturn(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.updateUser(userRequest), "Expected RepositoryException for update failure");

        verify(converter, times(1)).toEntity(userRequest, UserEntity.class);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("updateUser - Exceção ao tentar criar usuário com e-mail já registrado")
    void updateUser_DateException_EmailAlreadyRegistered() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(converter.toEntity(userRequest, UserEntity.class)).thenReturn(userEntity);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenThrow(DataIntegrityViolationException.class);

        // Act and Assert
        assertThrows(DataIntegrityException.class, () -> userService.updateUser(userRequest), "Expected UserUpdateException due to duplicate email");

        verify(converter, times(1)).toEntity(userRequest, UserEntity.class);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("deleteUser - Exclusão bem-sucedida do usuário")
    void deleteUser_DeletesSuccessfully() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser - Exceção ao tentar excluir usuário inexistente")
    void deleteUser_NotFoundExceptionHandling() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L), "Expected NotFoundException for non-existent user");

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("deleteUser - Exceção no repositório ao tentar excluir usuário")
    void deleteUser_RepositoryExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        doThrow(PersistenceException.class).when(userRepository).deleteById(1L);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.deleteUser(userEntity.getId()), "Expected RepositoryException for delete failure");

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("findUserByName - Busca bem-sucedida pelo nome retorna lista contendo um usuário")
    void findUserByName_SuccessfulSearch_ReturnsListResponse_OneUser() {
        // Arrange
        String userName = "João Silva";

        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        List<UserEntity> userEntityList = Collections.singletonList(userEntity);

        UserResponse userResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");
        List<UserResponse> expectedResponses = Collections.singletonList(userResponse);

        when(converter.toResponse(eq(userEntity), eq(UserResponse.class))).thenReturn(userResponse);
        when(userRepository.findByName(eq(userName))).thenReturn(userEntityList);

        // Act
        List<UserResponse> actualResponses = userService.findUserByName("João Silva");

        // Assert
        assertNotNull(actualResponses, "UserResponses should not be null");
        assertEquals(expectedResponses, actualResponses, "Size of UserResponses should match size of UserEntities");
        assertEquals(userName, actualResponses.get(0).getName(), "Names should match");

        verify(userRepository, times(1)).findByName(eq(userName));
        verify(converter, times(1)).toResponse(eq(userEntity), eq(UserResponse.class));
    }

    @Test
    @DisplayName("findUserByName - Busca bem-sucedida pelo nome retorna lista contendo múltiplos usuários")
    void findUserByName_SuccessfulSearch_ReturnsListResponse_MultipleUsers() {
        // Arrange
        String userName = "João Silva";

        List<UserEntity> userEntityList = new ArrayList<>();
        List<UserResponse> expectedResponses = new ArrayList<>();

        for (long i = 1; i <= 10; i++) {
            UserEntity userEntity = new UserEntity(i, "João Silva", "joao" + i + "@email.com",
                    "1191234567" + i, "senha123");
            userEntityList.add(userEntity);

            UserResponse userResponse = new UserResponse(i, "João Silva", "joao" + i + "@email.com",
                    "1191234567" + i);
            expectedResponses.add(userResponse);

            when(converter.toResponse(eq(userEntity), eq(UserResponse.class))).thenReturn(userResponse);
        }

        when(userRepository.findByName(eq(userName))).thenReturn(userEntityList);

        // Act
        List<UserResponse> actualResponses = userService.findUserByName("João Silva");

        // Assert
        assertNotNull(actualResponses, "UserResponses should not be null");
        assertEquals(expectedResponses, actualResponses, "Size of UserResponses should match size of UserEntities");

        verify(userRepository, times(1)).findByName(eq(userName));
        verify(converter, times(10)).toResponse(any(UserEntity.class), eq(UserResponse.class));
    }


    @Test
    @DisplayName("findUserByName - Exceção ao tentar buscar usuário inexistente pelo nome")
    void findUserByName_NotFoundExceptionHandling() {
        // Arrange
        String userName = "Inexistente";
        when(userRepository.findByName(userName)).thenReturn(Collections.emptyList());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.findUserByName(userName), "Expected NotFoundException for non-existent user");

        verify(userRepository, times(1)).findByName(userName);
    }

    @Test
    @DisplayName("findUserByName - Exceção no repositório ao tentar buscar usuário pelo nome")
    void findUserByName_RepositoryExceptionHandling() {
        // Arrange
        String userName = "Erro";

        when(userRepository.findByName(userName)).thenThrow(PersistenceException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.findUserByName("Erro"), "Expected RepositoryException for repository error");

        verify(userRepository, times(1)).findByName(userName);
    }
}
