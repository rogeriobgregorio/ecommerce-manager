package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.User;
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

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(AddressServiceImpl.class);

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository,
                              UserService userService,
                              Converter converter) {

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
                    .map(address -> converter.toResponse(address, AddressResponse.class))
                    .toList();

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar buscar todos os endereços: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar buscar todas os endereços: " + ex);
        }
    }

    @Transactional(readOnly = true)
    public AddressResponse findAddressResponseById(Long id) {

        return addressRepository
                .findById(id)
                .map(address -> converter.toResponse(address, AddressResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Endereço não encontrado com o ID: {}", id);
                    return new NotFoundException("Endereço não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public AddressResponse createAddress(AddressRequest addressRequest) {

        addressRequest.setId(null);
        Address address = buildAddress(addressRequest);

        try {
            addressRepository.save(address);
            logger.info("Endereço criado: {}", address);
            return converter.toResponse(address, AddressResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar criar o endereço: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar criar o endereço: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public AddressResponse updateAddress(AddressRequest addressRequest) {

        findAddressById(addressRequest.getId());
        Address address = buildAddress(addressRequest);

        try {
            addressRepository.save(address);
            logger.info("Endereço atualizado: {}", address);
            return converter.toResponse(address, AddressResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar atualizar o endereço: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar atualizar o endereço: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public void deleteAddress(Long id) {

        Address address = findAddressById(id);

        try {
            addressRepository.deleteById(id);
            logger.warn("Endereço removido: {}", address);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar excluir o endereço: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar excluir o endereço: " + ex);
        }
    }

    public Address findAddressById(Long id) {

        return addressRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Endereço não encontrado com o ID: {}", id);
                    return new NotFoundException("Endereço não encontrado com o ID: " + id + ".");
                });
    }

    public Address buildAddress(AddressRequest addressRequest) {

        Long userId = addressRequest.getUserId();
        User user = userService.findUserById(userId);

        Address address = converter.toEntity(addressRequest, Address.class);
        address.setUser(user);

        user.setAddress(address);
        userService.saveUserAddress(user);

        return address;
    }
}
