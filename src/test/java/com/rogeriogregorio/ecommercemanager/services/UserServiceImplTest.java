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
    private Converter<UserRequest, UserEntity, UserResponse> userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, userConverter);
    }

    @Test
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista contendo um usuário")
    void findAllUsers_SuccessfulSearch_ReturnsListResponse_OneUser() {
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
    @DisplayName("findAllUsers - Busca bem-sucedida retorna lista contendo múltiplos usuários")
    void findAllUsers_SuccessfulSearch_ReturnsListResponse_MultipleUsers() {
        // Arrange
        List<UserEntity> userEntityList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            userEntityList.add(new UserEntity("User" + i, "user" + i + "@email.com",
                    "111111111" + i, "password" + i));
        }

        List<UserResponse> expectedResponses = new ArrayList<>();
        for (UserEntity userEntity : userEntityList) {
            expectedResponses.add(new UserResponse(null, userEntity.getName(), userEntity.getEmail(), userEntity.getPhone()));
        }

        when(userConverter.entityToResponse(any(UserEntity.class)))
                .thenAnswer(invocation -> {
                    UserEntity userEntity = invocation.getArgument(0);
                    return new UserResponse(null, userEntity.getName(), userEntity.getEmail(), userEntity.getPhone());
                });

        when(userRepository.findAll()).thenReturn(userEntityList);

        // Act
        List<UserResponse> actualResponses = userService.findAllUsers();

        // Assert
        assertEquals(expectedResponses.size(), actualResponses.size());
        for (int i = 0; i < expectedResponses.size(); i++) {
            assertEquals(expectedResponses.get(i).getName(), actualResponses.get(i).getName());
            assertEquals(expectedResponses.get(i).getEmail(), actualResponses.get(i).getEmail());
            assertEquals(expectedResponses.get(i).getPhone(), actualResponses.get(i).getPhone());
        }

        verify(userConverter, times(userEntityList.size())).entityToResponse(any(UserEntity.class));
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
    @DisplayName("findAllUsers - Exceção ao tentar buscar lista de usuários")
    void findAllUsers_UserRepositoryExceptionHandling() {
        // Arrange
        when(userRepository.findAll()).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.findAllUsers(), "Expected RepositoryException to be thrown");
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
    @DisplayName("createUser - Exceção ao tentar criar usuário com e-mail já registrado")
    void createUser_UserDataException_EmailAlreadyRegistered() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(DataIntegrityViolationException.class);

        // Act and Assert
        assertThrows(DataIntegrityException.class, () -> userService.createUser(userRequest), "Expected DataIntegrityException due to duplicate email");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("createUser - Exceção no repositório ao tentar criar usuário")
    void createUser_UserRepositoryExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.createUser(userRequest), "Expected RepositoryException due to a generic runtime exception");

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
    @DisplayName("findUserById - Exceção ao tentar buscar usuário inexistente")
    void findUserById_UserNotFoundExceptionHandling() {
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

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
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
        verify(userRepository, times(1)).findById(userEntity.getId());
        verify(userRepository, times(1)).save(userEntity);
        verify(userConverter, times(1)).entityToResponse(userEntity);
    }

    @Test
    @DisplayName("updateUser - Exceção ao tentar atualizar usuário inexistente")
    void updateUser_UserNotFoundExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.updateUser(userRequest), "Expected NotFoundException for non-existent user");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).findById(userEntity.getId());
    }

    @Test
    @DisplayName("updateUser - Exceção no repositório ao tentar atualizar usuário")
    void updateUser_UserRepositoryExceptionHandling() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.updateUser(userRequest), "Expected RepositoryException for update failure");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).findById(userEntity.getId());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    @DisplayName("updateUser - Exceção ao tentar criar usuário com e-mail já registrado")
    void update_UserUpdateException_EmailAlreadyRegistered() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L,"João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L,"João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenThrow(DataIntegrityViolationException.class);

        // Act and Assert
        assertThrows(DataIntegrityException.class, () -> userService.updateUser(userRequest), "Expected UserUpdateException due to duplicate email");

        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).findById(userEntity.getId());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("deleteUser - Exclusão bem-sucedida do usuário")
    void deleteUser_DeletesUserSuccessfully() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

        // Act
        userService.deleteUser(userEntity.getId());

        // Assert
        verify(userRepository, times(1)).deleteById(userEntity.getId());
    }

    @Test
    @DisplayName("deleteUser - Exceção ao tentar excluir usuário inexistente")
    void deleteUser_UserNotFoundExceptionHandling() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId), "Expected NotFoundException for non-existent user");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("deleteUser - Exceção no repositório ao tentar excluir usuário")
    void deleteUser_UserRepositoryExceptionHandling() {
        // Arrange
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        doThrow(RuntimeException.class).when(userRepository).deleteById(userEntity.getId());

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.deleteUser(userEntity.getId()), "Expected RepositoryException for delete failure");

        verify(userRepository, times(1)).findById(userEntity.getId());
        verify(userRepository, times(1)).deleteById(userEntity.getId());
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
    @DisplayName("findUserByName - Exceção ao tentar buscar usuário inexistente pelo nome")
    void findUserByName_UserNotFoundExceptionHandling() {
        // Arrange
        String userName = "Inexistente";
        when(userRepository.findByName(userName)).thenReturn(Collections.emptyList());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> userService.findUserByName(userName), "Expected NotFoundException for non-existent user");

        verify(userRepository, times(1)).findByName(userName);
    }

    @Test
    @DisplayName("findUserByName - Exceção no repositório ao tentar buscar usuário pelo nome")
    void findUserByName_UserRepositoryExceptionHandling() {
        // Arrange
        String userName = "Erro";
        when(userRepository.findByName(userName)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(RepositoryException.class, () -> userService.findUserByName(userName), "Expected RepositoryException for repository error");

        verify(userRepository, times(1)).findByName(userName);
        verify(userConverter, never()).entityToResponse(any());
    }
}
