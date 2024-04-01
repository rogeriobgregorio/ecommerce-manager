package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.entities.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface AddressService {

    Page<AddressResponse> findAllAddresses(Pageable pageable);

    AddressResponse createAddress(AddressRequest addressRequest);

    AddressResponse findAddressResponseById(Long id);

    Address findAddressById(Long id);

    AddressResponse updateAddress(AddressRequest addressRequest);

    void deleteAddress(Long id);
}
