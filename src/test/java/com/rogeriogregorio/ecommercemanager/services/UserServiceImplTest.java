package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.UserCreateException;
import com.rogeriogregorio.ecommercemanager.exceptions.UserNotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.UserQueryException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.UserServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.UserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
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
    private UserConverter userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, userConverter);
    }

    @Test
    void findAllUsers_returnsListUsersResponse() {
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
    void findAllUsers_returnsEmptyList() {
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
    void createUser_returnsUserResponse() {
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
    void createUser_handlesUserCreateExceptionEmailAlreadyRegistered() {
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
    void createUser_handlesUserCreateExceptionGeneric() {
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
    void findUserById_returnsUserResponse() {
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
}
