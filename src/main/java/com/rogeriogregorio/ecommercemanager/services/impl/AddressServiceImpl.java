package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.AddressRepository;
import com.rogeriogregorio.ecommercemanager.services.AddressService;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.util.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;
    private final ErrorHandler errorHandler;
    private final Mapper mapper;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository, UserService userService,
                              ErrorHandler errorHandler, Mapper mapper) {

        this.addressRepository = addressRepository;
        this.userService = userService;
        this.errorHandler = errorHandler;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<AddressResponse> findAllAddresses(Pageable pageable) {

        return errorHandler.catchException(() -> addressRepository.findAll(pageable),
                        "Error while trying to fetch all addresses: ")
                .map(address -> mapper.toResponse(address, AddressResponse.class));
    }

    @Transactional(readOnly = true)
    public AddressResponse findAddressById(UUID id) {

        return errorHandler.catchException(() -> addressRepository.findById(id),
                        "Error while trying to find the address by ID: ")
                .map(address -> mapper.toResponse(address, AddressResponse.class))
                .orElseThrow(() -> new NotFoundException("Address not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public AddressResponse createAddress(AddressRequest addressRequest) {

        addressRequest.setId(null);
        Address address = buildAddress(addressRequest);

        errorHandler.catchException(() -> addressRepository.save(address),
                "Error while trying to create the address: ");
        logger.info("Address created: {}", address);

        return mapper.toResponse(address, AddressResponse.class);
    }

    @Transactional(readOnly = false)
    public AddressResponse updateAddress(AddressRequest addressRequest) {

        isAddressExists(addressRequest.getId());
        Address address = buildAddress(addressRequest);

        errorHandler.catchException(() -> addressRepository.save(address),
                "Error while trying to update the address: ");
        logger.info("Address updated: {}", address);

        return mapper.toResponse(address, AddressResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteAddress(UUID id) {

        isAddressExists(id);

        errorHandler.catchException(() -> {
            addressRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the address: ");
        logger.warn("Address removed with ID: {}", id);
    }

    private void isAddressExists(UUID id) {

        boolean isAddressExists = errorHandler.catchException(() -> addressRepository.existsById(id),
                "Error while trying to check the presence of the address: ");

        if (!isAddressExists) {
            throw new NotFoundException("Address not found with ID: " + id + ".");
        }
    }

    private Address buildAddress(AddressRequest addressRequest) {

        UUID userId = addressRequest.getUserId();
        User user = userService.findUserById(userId);

        Address address = mapper.toEntity(addressRequest, Address.class);
        address.setUser(user);

        user.setAddress(address);
        userService.saveUserAddress(user);

        return address;
    }
}
