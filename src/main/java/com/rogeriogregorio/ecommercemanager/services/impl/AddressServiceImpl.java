package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.AddressEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.AddressRepository;
import com.rogeriogregorio.ecommercemanager.services.AddressService;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, UserService userService, Converter converter) {
        this.addressRepository = addressRepository;
        this.userService = userService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> findAllAddresses() {

        try {
            return addressRepository
                    .findAll()
                    .stream()
                    .map(AddressEntity -> converter.toResponse(AddressEntity, AddressResponse.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todos os endereços: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todas os endereços: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public AddressResponse createAddress(AddressRequest addressRequest) {

        addressRequest.setId(null);

        AddressEntity addressEntity = buildAddressFromRequest(addressRequest);

        try {
            addressRepository.save(addressEntity);
            logger.info("Endereço criado: {}", addressEntity.toString());
            return converter.toResponse(addressEntity, AddressResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o endereço: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o endereço: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public AddressResponse findAddressById(Long id) {

        return addressRepository
                .findById(id)
                .map(addressEntity -> converter.toResponse(addressEntity, AddressResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Endereço não encontrado com o ID: {}", id);
                    return new NotFoundException("Endereço não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = true)
    public AddressEntity findAddressEntityById(Long id) {

        return addressRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Endereço não encontrado com o ID: {}", id);
                    return new NotFoundException("Endereço não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public AddressResponse updateAddress(AddressRequest addressRequest) {

        findAddressEntityById(addressRequest.getId());

        AddressEntity addressEntity = buildAddressFromRequest(addressRequest);

        try {
            addressRepository.save(addressEntity);
            logger.info("Endereço atualizado: {}", addressEntity.toString());
            return converter.toResponse(addressEntity, AddressResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o endereço: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o endereço: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteAddress(Long id) {

        AddressEntity addressEntity = findAddressEntityById(id);

        try {
            addressRepository.deleteById(id);
            logger.warn("Endereço removido: {}", addressEntity.toString());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o endereço: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o endereço: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public AddressEntity buildAddressFromRequest(AddressRequest addressRequest) {

        UserEntity user = userService.findUserEntityById(addressRequest.getUserId());

        AddressEntity addressEntity = converter.toEntity(addressRequest, AddressEntity.class);
        addressEntity.setUserEntity(user);

        user.setAddressEntity(addressEntity);
        userService.saveUserAddress(user);

        return addressEntity;
    }
}
