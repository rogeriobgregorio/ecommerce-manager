package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.DataIntegrityException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
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
    private final Converter<UserRequest, UserEntity, UserResponse> userConverter;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Converter<UserRequest, UserEntity, UserResponse> userConverter) {
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
            logger.error("Erro ao tentar buscar usuários: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar usuários.", exception);
        }
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {

        return userRepository
                .findById(id)
                .map(userConverter::entityToResponse)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com o ID: {}", id);
                    return new NotFoundException("Usuário não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        userRequest.setId(null);

        UserEntity userEntity = userConverter.requestToEntity(userRequest);

        try {
            userRepository.save(userEntity);
            logger.info("Usuário criado: {}", userEntity.toString());

        } catch (DataIntegrityViolationException exception) {
            logger.error("Erro ao tentar criar o usuário: E-mail já cadastrado.", exception);
            throw new DataIntegrityException("Erro ao tentar criar o usuário: E-mail já cadastrado.");

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar o usuário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o usuário.", exception);
        }

        return userConverter.entityToResponse(userEntity);
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        UserEntity userEntity = userConverter.requestToEntity(userRequest);

        userRepository.findById(userEntity.getId()).orElseThrow(() -> {
            logger.warn("Usuário não encontrado com o ID: {}", userEntity.getId());
            return new NotFoundException("Usuário não encontrado com o ID: " + userEntity.getId() + ".");
        });

        try {
            userRepository.save(userEntity);
            logger.info("Usuário atualizado: {}", userEntity.toString());

        } catch (DataIntegrityViolationException exception) {
            logger.error("Erro ao tentar atualizar usuário: E-mail já cadastrado.", exception);
            throw new DataIntegrityException("Erro ao tentar atualizar o usuário: E-mail já cadastrado.");

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar o usuário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o usuário.", exception);
        }

        return userConverter.entityToResponse(userEntity);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        userRepository.findById(id).orElseThrow(() -> {
            logger.warn("Usuário não encontrado com o ID: {}", id);
            return new NotFoundException("Usuário não encontrado com o ID: " + id + ".");
        });

        try {
            userRepository.deleteById(id);
            logger.warn("Usuário removido: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir o usuário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o usuário: ", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findUserByName(String name) {

        List<UserEntity> users;

        try {
            users = userRepository.findByName(name);

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar usuários: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar usuários.", exception);
        }

        if (users.isEmpty()) {
            logger.warn("Nenhum usuário encontrado com o nome: {}", name);
            throw new NotFoundException("Nenhum usuário com nome " + name + " encontrado.");
        }

        return users.stream()
                .map(userConverter::entityToResponse)
                .collect(Collectors.toList());
    }
}
