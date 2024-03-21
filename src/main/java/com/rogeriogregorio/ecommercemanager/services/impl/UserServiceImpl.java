package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Converter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {

        try {
            return userRepository
                    .findAll()
                    .stream()
                    .map(userEntity -> converter.toResponse(userEntity, UserResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todos os usuários: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todos os usuários: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {

        return userRepository
                .findById(id)
                .map(userEntity -> converter.toResponse(userEntity, UserResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com o ID: {}", id);
                    return new NotFoundException("Usuário não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        userRequest.setId(null);

        UserEntity userEntity = converter.toEntity(userRequest, UserEntity.class);

        try {
            userRepository.save(userEntity);
            logger.info("Usuário criado: {}", userEntity);
            return converter.toResponse(userEntity, UserResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o usuário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o usuário: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        findUserEntityById(userRequest.getId());

        UserEntity userEntity = converter.toEntity(userRequest, UserEntity.class);

        try {
            userRepository.save(userEntity);
            logger.info("Usuário atualizado: {}", userEntity);
            return converter.toResponse(userEntity, UserResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o usuário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o usuário: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        UserEntity userEntity = findUserEntityById(id);

        try {
            userRepository.deleteById(id);
            logger.warn("Usuário removido: {}", userEntity);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o usuário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o usuário: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findUserByName(String name) {

        try {
            return userRepository.findByName(name)
                    .stream()
                    .map(userEntity -> converter.toResponse(userEntity, UserResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar usuário pelo nome: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar usuário pelo nome: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void saveUserAddress(UserEntity userEntity) {

        try {
            userRepository.save(userEntity);
            logger.info("Endereço do usuário atualizado: {}", userEntity);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o endereço do usuário: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o endereço do usuário: " + exception);
        }

    }

    public UserEntity findUserEntityById(Long id) {

        return userRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com o ID: {}", id);
                    return new NotFoundException("Usuário não encontrado com o ID: " + id + ".");
                });
    }

    public boolean isAddressPresent(UserEntity userEntity) {

        return userEntity.getAddressEntity() != null;
    }
}
