package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {

        if (userRepository.findAll().isEmpty()){
            throw new RuntimeException("Nenhum usuário encontrado.");
        }

        return userRepository
                .findAll()
                .stream()
                .map(userEntity -> modelMapper.map(userEntity, UserResponse.class))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);

        try {
            userEntity = userRepository.save(userEntity);
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao criar o usuário: " + exception.getMessage() + ".");
        }

        return modelMapper.map(userEntity, UserResponse.class);
    }


    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {

        return userRepository
                .findById(id)
                .map(userEntity -> modelMapper.map(userEntity, UserResponse.class))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);

        if (!userRepository.existsById(userEntity.getId())) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + userEntity.getId() + ".");
        }

        try {
            userEntity = userRepository.save(userEntity);
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao atualizar usuário: " + exception.getMessage());
        }

        return modelMapper.map(userEntity, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id + ".");
        }

        userRepository.deleteById(id);
    }
}
