package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.AddressRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.AddressResponse;
import com.rogeriogregorio.ecommercemanager.services.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/addresses")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addressService.findAllAddresses(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<AddressResponse> postAddress(
            @Valid @RequestBody AddressRequest addressRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(addressRequest));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable UUID id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addressService.findAddressById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AddressResponse> putAddress(@PathVariable UUID id,
            @Valid @RequestBody AddressRequest addressRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addressService.updateAddress(id, addressRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {

        addressService.deleteAddress(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
