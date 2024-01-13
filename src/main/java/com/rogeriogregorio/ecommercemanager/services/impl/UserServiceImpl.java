package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {

        if (userRepository.findAll().isEmpty()){
            throw new RuntimeException("Nenhum usuário encontrado.");
        }

        return userRepository
                .findAll()
                .stream()
                .map(userConverter::entityToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        UserEntity userEntity = userConverter.requestToEntity(userRequest);

        try {
            userRepository.save(userEntity);
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao criar o usuário: " + exception.getMessage() + ".");
        }

        return userConverter.entityToResponse(userEntity);
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {

        return userRepository
                .findById(id)
                .map(userConverter::entityToResponse)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        UserEntity userEntity = userConverter.requestToEntity(userRequest);

        if (!userRepository.existsById(userEntity.getId())) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + userEntity.getId() + ".");
        }

        try {
            userEntity = userRepository.save(userEntity);
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao atualizar usuário: " + exception.getMessage());
        }

        return userConverter.entityToResponse(userEntity);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id + ".");
        }

        userRepository.deleteById(id);
    }
}
