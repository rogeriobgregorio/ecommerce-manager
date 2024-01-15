package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.*;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.UserConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {

        try {
            return userRepository
                    .findAll()
                    .stream()
                    .map(userConverter::entityToResponse)
                    .collect(Collectors.toList());

        } catch (Exception exception) {
            logger.error("Erro ao buscar usuários: {}", exception.getMessage(), exception);
            throw new UserQueryException("Erro ao buscar usuários: " + exception.getMessage() + ".");
        }
    }

    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        UserEntity userEntity = userConverter.requestToEntity(userRequest);

        try {
            userRepository.save(userEntity);

        } catch (DataIntegrityViolationException exception) {
            logger.error("Erro ao criar o usuário: E-mail já cadastrado.", exception);
            throw new UserCreationException("Erro ao criar o usuário: E-mail já cadastrado.");

        } catch (Exception exception) {
            logger.error("Erro ao criar o usuário: {}", exception.getMessage(), exception);
            throw new UserCreationException("Erro ao criar o usuário: " + exception.getMessage() + ".");
        }

        return userConverter.entityToResponse(userEntity);
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {

            return userRepository
                    .findById(id)
                    .map(userConverter::entityToResponse)
                    .orElseThrow(() -> {
                        logger.error("Usuário não encontrado com o ID: {}", id);
                        return new UserNotFoundException("Usuário não encontrado com o ID: " + id + ".");
                    });
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        UserEntity userEntity = userConverter.requestToEntity(userRequest);

        if (!userRepository.existsById(userEntity.getId())) {
            logger.error("Usuário não encontrado com o ID: {}", userEntity.getId());
            throw new UserNotFoundException("Usuário não encontrado com o ID: " + userEntity.getId() + ".");
        }

        try {
            userRepository.save(userEntity);

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar o usuário: {}", exception.getMessage(), exception);
            throw new UserUpdateException("Erro ao tentar atualizar o usuário: " + exception.getMessage() + ".");
        }

        return userConverter.entityToResponse(userEntity);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            logger.error("Usuário não encontrado com o ID: {}", id);
            throw new UserNotFoundException("Usuário não encontrado com o ID: " + id + ".");
        }

        try {
            userRepository.deleteById(id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir o usuário: {}", exception.getMessage(), exception);
            throw new UserDeletionException("Erro ao tentar excluir o usuário: " + exception.getMessage() + ".");
        }
    }
}
