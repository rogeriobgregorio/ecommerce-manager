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
    void findAllUsers_returnsListUsersResponse_OnSuccessfulSearch() {
        // Arrange
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(userEntity);

        UserResponse userResponse = new UserResponse(null, "João Silva", "joao@email.com", "11912345678");
        List<UserResponse> userResponseList = new ArrayList<>();
        userResponseList.add(userResponse);

        when(userConverter.entityToResponse(userEntity)).thenReturn(userResponse);
        when(userRepository.findAll()).thenReturn(userEntityList);

        // Act
        List<UserResponse> returnsListUserResponse = userService.findAllUsers();

        // Assert
        assertEquals(userResponseList.size(), returnsListUserResponse.size());
        assertIterableEquals(userResponseList, returnsListUserResponse);
        verify(userConverter, times(1)).entityToResponse(any());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAllUsers_returnsEmptyList_OnSuccessfulSearch() {
        // Arrange
        List<UserEntity> userEntityListEmpty = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(userEntityListEmpty);

        // Act
        List<UserResponse> returnsListUserResponse = userService.findAllUsers();

        // Assert
        assertEquals(userEntityListEmpty.size(), returnsListUserResponse.size());
        assertIterableEquals(userEntityListEmpty, returnsListUserResponse);
        verify(userRepository, times(1)).findAll();
    }


    @Test
    void findAllUsers_handlesUserQueryException() {
        // Arrange
        when(userRepository.findAll()).thenThrow(new RuntimeException("Simulando uma exceção do repositório"));

        // Act and Assert
        assertThrows(UserQueryException.class, () -> userService.findAllUsers());
    }

    @Test
    void createUser_returnsUserResponse_OnSuccessfulCreated() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse userResponse = new UserResponse(null, "João Silva", "joao@email.com", "11912345678");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userConverter.entityToResponse(userEntity)).thenReturn(userResponse);
        when(userRepository.save(any())).thenReturn(userEntity);

        // Act
        UserResponse returnsUserResponse = userService.createUser(userRequest);

        // Assert
        assertNotNull(returnsUserResponse);
        assertEquals(userResponse, returnsUserResponse);
        verify(userRepository, times(1)).save(any());
        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userConverter, times(1)).entityToResponse(userEntity);
    }

    @Test
    void createUser_handlesUserCreateException_EmailAlreadyRegistered() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(DataIntegrityViolationException.class);

        // Act and Assert
        assertThrows(UserCreateException.class, () -> userService.createUser(userRequest));
        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createUser_handlesUserCreateException_Generic() {
        // Arrange
        UserRequest userRequest = new UserRequest("João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(UserCreateException.class, () -> userService.createUser(userRequest));
        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void findUserById_returnsUserResponse_OnSuccessfulSearchById() {
        // Arrange
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse userResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");

        when(userConverter.entityToResponse(userEntity)).thenReturn(userResponse);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // Act
        UserResponse returnsUserResponse = userService.findUserById(1L);

        // Assert
        assertNotNull(returnsUserResponse);
        assertEquals(userResponse, returnsUserResponse);
        verify(userConverter, times(1)).entityToResponse(userEntity);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findUserById_handlesUserNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUser_returnsUserResponse_OnSuccessfulUpdate() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserResponse userResponse = new UserResponse(1L, "João Silva", "joao@email.com", "11912345678");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.existsById(userEntity.getId())).thenReturn(true);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userConverter.entityToResponse(userEntity)).thenReturn(userResponse);

        // Act
        UserResponse returnsUserResponse = userService.updateUser(userRequest);

        // Assert
        assertNotNull(returnsUserResponse);
        assertEquals(userEntity.getId(), returnsUserResponse.getId());
        assertEquals(userRequest.getName(), returnsUserResponse.getName());
        assertEquals(userRequest.getEmail(), returnsUserResponse.getEmail());
        assertEquals(userRequest.getPhone(), returnsUserResponse.getPhone());
        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).existsById(userEntity.getId());
        verify(userRepository, times(1)).save(userEntity);
        verify(userConverter, times(1)).entityToResponse(userEntity);
    }

    @Test
    void updateUser_handlesUserNotFoundException() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L, "João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.existsById(userEntity.getId())).thenReturn(false);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userRequest));
        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).existsById(userEntity.getId());
    }

    @Test
    void updateUser_handlesUserUpdateException() {
        // Arrange
        UserRequest userRequest = new UserRequest(1L,"João Silva", "joao@email.com", "11912345678", "senha123");
        UserEntity userEntity = new UserEntity(1L, "João Silva", "joao@email.com", "11912345678", "senha123");

        when(userConverter.requestToEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.existsById(userEntity.getId())).thenReturn(true);
        when(userRepository.save(userEntity)).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(UserUpdateException.class, () -> userService.updateUser(userRequest));
        verify(userConverter, times(1)).requestToEntity(userRequest);
        verify(userRepository, times(1)).existsById(userEntity.getId());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void deleteUser_deletesUserSuccessfully() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }


    @Test
    void deleteUser_handlesUserNotFoundException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void deleteUser_handlesUserDeleteException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doThrow(RuntimeException.class).when(userRepository).deleteById(userId);

        // Act and Assert
        assertThrows(UserDeleteException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void findUserByName_returnsListUsersResponse_OnSuccessfulSearch() {
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
        assertNotNull(userResponses);
        assertFalse(userResponses.isEmpty());
        assertEquals(userEntities.size(), userResponses.size());
        assertEquals(userName, userResponses.get(0).getName());
        verify(userRepository, times(1)).findByName(userName);
        verify(userConverter, times(1)).entityToResponse(any());
    }

    @Test
    void findUserByName_handlesUserNotFoundException() {
        // Arrange
        String userName = "Inexistente";
        when(userRepository.findByName(userName)).thenReturn(Collections.emptyList());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserByName(userName));
        verify(userRepository, times(1)).findByName(userName);
    }

    @Test
    void findUserByName_handlesUserQueryException() {
        // Arrange
        String userName = "Erro";
        when(userRepository.findByName(userName)).thenReturn(List.of(new UserEntity(userName, "email", "phone", "password")));
        when(userConverter.entityToResponse(any())).thenThrow(RuntimeException.class);

        // Act and Assert
        assertThrows(UserQueryException.class, () -> userService.findUserByName(userName));
        verify(userRepository, times(1)).findByName(userName);
        verify(userConverter, times(1)).entityToResponse(any());
    }

    @Test
    void validateUser_validateUserSuccessfully() {
        // Arrange
        UserEntity validUser = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");

        // Act
        assertDoesNotThrow(() -> userService.validateUser(validUser));

        // Assert
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(validUser);
        assertTrue(violations.isEmpty(), "No constraint violations expected for a valid user");
    }

    @Test
    void validateUser_handlesConstraintViolationException() {
        // Arrange
        UserEntity invalidUser = new UserEntity("", "invalid_email", "invalid_phone", "");

        // Act and Assert
        assertThrows(ConstraintViolationException.class, () -> userService.validateUser(invalidUser));
    }
}
