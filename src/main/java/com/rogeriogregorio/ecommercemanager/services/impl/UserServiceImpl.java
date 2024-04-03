package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ErrorHandlerTemplateImpl implements UserService {

    private final UserRepository userRepository;
    private final Converter converter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           Converter converter) {

        this.userRepository = userRepository;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable) {

        return handleError(() -> userRepository.findAll(pageable),
                "Erro ao tentar buscar todos os usuários: ")
                .map(user -> converter.toResponse(user, UserResponse.class));
    }

    @Transactional(readOnly = true)
    public UserResponse findUserResponseById(Long id) {

        return handleError(() -> userRepository.findById(id),
                "Erro ao tentar buscar o usuário pelo id: " + id)
                .map(user -> converter.toResponse(user, UserResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com o ID: {}", id);
                    return new NotFoundException("Usuário não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        userRequest.setId(null);
        User user = buildUser(userRequest);

        handleError(() -> userRepository.save(user),
                "Erro ao tentar criar o usuário: ");

        logger.info("Usuário criado: {}", user);
        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        findUserById(userRequest.getId());
        User user = buildUser(userRequest);

        handleError(() -> userRepository.save(user),
                "Erro ao tentar atualizar o usuário: ");

        logger.info("Usuário atualizado: {}", user);
        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        User user = findUserById(id);

        handleError(() -> {
            userRepository.deleteById(id);
            return null;
        }, "Erro ao tentar excluir o usuário: ");

        logger.warn("Usuário removido: {}", user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findUserByName(String name, Pageable pageable) {

        return handleError(() -> userRepository.findByName(name, pageable),
                "Erro ao tentar buscar o usuário pelo nome: {}").
                map(user -> converter.toResponse(user, UserResponse.class));
    }

    public User findUserById(Long id) {

        return handleError(() -> userRepository.findById(id),
                "Erro ao tentar buscar o usuário pelo id: " + id)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com o ID: {}", id);
                    return new NotFoundException("Usuário não encontrado com o ID: " + id + ".");
                });
    }

    public void saveUserAddress(User user) {

        handleError(() -> {
            userRepository.save(user);
            return null;
        }, "Erro ao tentar atualizar o endereço do usuário: ");

        logger.info("Endereço do usuário atualizado: {}", user);
    }

    public User buildUser(UserRequest userRequest) {

        return converter.toEntity(userRequest, User.class);
    }
}
