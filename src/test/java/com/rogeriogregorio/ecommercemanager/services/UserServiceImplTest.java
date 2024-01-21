package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.impl.UserServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.UserConverter;
import org.aspectj.bridge.IMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

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
    void findAllUsers_returnsListUsers() {

        //Arrange
        UserEntity userEntity = new UserEntity("João Silva", "joao@email.com", "11912345678", "senha123");
        List<UserEntity> userEntityList = new ArrayList<UserEntity>();
        userEntityList.add(userEntity);

        UserResponse userResponse = new UserResponse(null, "João Silva", "joao@email.com", "11912345678");
        List<UserResponse> userResponseList = new ArrayList<UserResponse>();
        userResponseList.add(userResponse);

        when(userConverter.entityToResponse(userEntity)).thenReturn(userResponse);
        when(userRepository.findAll()).thenReturn(userEntityList);

        //Act
        List<UserResponse> returnsListUser = userService.findAllUsers();

        //Assert
        assertEquals(userResponseList.size(), returnsListUser.size());
        assertIterableEquals(userResponseList, returnsListUser);
    }
}
