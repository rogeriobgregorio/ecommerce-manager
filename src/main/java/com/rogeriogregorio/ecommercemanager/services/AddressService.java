package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface AddressService {

    Page<AddressResponse> findAllAddresses(Pageable pageable);

    AddressResponse createAddress(AddressRequest addressRequest);

    AddressResponse findAddressById(Long id);

    AddressResponse updateAddress(AddressRequest addressRequest);

    void deleteAddress(Long id);
}
