package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AddressService {

    List<AddressResponse> findAllAdresses();

    AddressResponse createAddress(AddressRequest addressRequest);

    AddressResponse findAddressById(Long id);

    AddressResponse updateAddress(AddressRequest addressRequest);

    void deleteAddress(Long id);
}
