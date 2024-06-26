package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.AddressRepository;
import com.rogeriogregorio.ecommercemanager.services.AddressService;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(AddressServiceImpl.class);

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository,
                              UserService userService,
                              CatchError catchError,
                              DataMapper dataMapper) {

        this.addressRepository = addressRepository;
        this.userService = userService;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<AddressResponse> findAllAddresses(Pageable pageable) {

        return catchError.run(() -> addressRepository.findAll(pageable))
                .map(address -> dataMapper.map(address, AddressResponse.class));
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest addressRequest) {

        User user = userService.getUserIfExists(addressRequest.getUserId());
        Address address = dataMapper.map(addressRequest, Address.class);
        address.setUser(user);
        user.setAddress(address);
        userService.saveUserAddress(user);

        Address savedAddress = catchError.run(() -> addressRepository.save(address));
        LOGGER.info("Address created: {}", savedAddress);
        return dataMapper.map(savedAddress, AddressResponse.class);
    }

    @Transactional(readOnly = true)
    public AddressResponse findAddressById(UUID id) {

        return catchError.run(() -> addressRepository.findById(id))
                .map(address -> dataMapper.map(address, AddressResponse.class))
                .orElseThrow(() -> new NotFoundException("Address not found with ID: " + id + "."));
    }

    @Transactional
    public AddressResponse updateAddress(UUID id, AddressRequest addressRequest) {

        User user = userService.getUserIfExists(addressRequest.getUserId());
        Address currentAddress = getAddressIfExists(id);
        dataMapper.map(addressRequest, currentAddress);
        currentAddress.setUser(user);
        user.setAddress(currentAddress);
        userService.saveUserAddress(user);

        Address updatedAddress = catchError.run(() -> addressRepository.save(currentAddress));
        LOGGER.info("Address updated: {}", updatedAddress);
        return dataMapper.map(updatedAddress, AddressResponse.class);
    }

    @Transactional
    public void deleteAddress(UUID id) {

        Address address = getAddressIfExists(id);
        catchError.run(() -> addressRepository.delete(address));
        LOGGER.warn("Address deleted: {}", address);
    }

    private Address getAddressIfExists(UUID id) {

        return catchError.run(() -> addressRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("Address not found with ID: " + id + "."));
    }
}
