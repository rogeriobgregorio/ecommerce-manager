package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentConverter implements Converter<PaymentRequest, PaymentEntity, PaymentResponse> {

    private final ModelMapper modelMapper;

    @Autowired
    public PaymentConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public PaymentEntity requestToEntity(PaymentRequest paymentRequest) {
        return modelMapper.map(paymentRequest, PaymentEntity.class);
    }

    @Override
    public PaymentResponse entityToResponse(PaymentEntity paymentEntity) {
        return modelMapper.map(paymentEntity, PaymentResponse.class);
    }

    @Override
    public PaymentEntity responseToEntity(PaymentResponse paymentResponse) {
        return modelMapper.map(paymentResponse, PaymentEntity.class);
    }
}
