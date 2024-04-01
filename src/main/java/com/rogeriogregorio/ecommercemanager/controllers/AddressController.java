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

@RestController
@RequestMapping(value = "/api")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping(value = "/addresses")
    public ResponseEntity<List<AddressResponse>> getAllAddresses(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addressService.findAllAddresses(pageable).getContent());
    }

    @PostMapping(value = "/addresses")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest addressRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(addressRequest));
    }

    @GetMapping(value = "/addresses/{id}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addressService.findAddressResponseById(id));
    }

    @PutMapping(value = "/addresses")
    public ResponseEntity<AddressResponse> updateAddress(@Valid @RequestBody AddressRequest addressRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(addressService.updateAddress(addressRequest));
    }

    @DeleteMapping(value = "/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {

        addressService.deleteAddress(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
