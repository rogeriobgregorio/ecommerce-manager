package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.AddressRepository;
import com.rogeriogregorio.ecommercemanager.services.AddressService;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl extends ErrorHandlerTemplateImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;
    private final Converter converter;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository,
                              UserService userService,
                              Converter converter) {

        this.addressRepository = addressRepository;
        this.userService = userService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<AddressResponse> findAllAddresses(Pageable pageable) {

        return handleError(() -> addressRepository.findAll(pageable),
                "Error while trying to fetch all addresses: ")
                .map(address -> converter.toResponse(address, AddressResponse.class));
    }

    @Transactional(readOnly = true)
    public AddressResponse findAddressResponseById(Long id) {

        return handleError(() -> addressRepository.findById(id),
                "Error while trying to find the address by ID: ")
                .map(address -> converter.toResponse(address, AddressResponse.class))
                .orElseThrow(() -> new NotFoundException("Address not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public AddressResponse createAddress(AddressRequest addressRequest) {

        addressRequest.setId(null);
        Address address = buildAddress(addressRequest);

        handleError(() -> addressRepository.save(address),
                "Error while trying to create the address: ");
        logger.info("Address created: {}", address);

        return converter.toResponse(address, AddressResponse.class);
    }

    @Transactional(readOnly = false)
    public AddressResponse updateAddress(AddressRequest addressRequest) {

        findAddressById(addressRequest.getId());
        Address address = buildAddress(addressRequest);

        handleError(() -> addressRepository.save(address),
                "Error while trying to update the address: ");
        logger.info("Address updated: {}", address);

        return converter.toResponse(address, AddressResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteAddress(Long id) {

        Address address = findAddressById(id);

        handleError(() -> {
            addressRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the address: ");
        logger.warn("Address removed: {}", address);
    }

    public Address findAddressById(Long id) {

        return handleError(() -> addressRepository.findById(id),
                "Error while trying to find the address by ID:")
                .orElseThrow(() -> new NotFoundException("Address not found with ID: {}" + id + "."));
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
