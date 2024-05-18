package com.rogeriogregorio.ecommercemanager.pix;

import org.springframework.stereotype.Component;

@Component
public interface PixService {

    String createPixEVP();

    String createImmediatePixCharge();

    String generatePixQRCodeLink();
}
