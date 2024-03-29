package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AddressService {

    List<AddressResponse> findAllAddresses();

    AddressResponse createAddress(AddressRequest addressRequest);

    AddressResponse findAddressResponseById(Long id);

    Address findAddressById(Long id);

    AddressResponse updateAddress(AddressRequest addressRequest);

    void deleteAddress(Long id);
}
