package com.rogeriogregorio.ecommercemanager.pix;

import com.rogeriogregorio.ecommercemanager.dto.PixChargeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixEVPKeyDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixListChargeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixQRCodeDTO;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

@Component
public interface PixService {

    PixEVPKeyDTO createPixEVPKey();

    PixChargeDTO createImmediatePixCharge(Order order);

    PixQRCodeDTO generatePixQRCode(PixChargeDTO pixCharge);

    PixListChargeDTO listPixCharges(String startDate, String endDate);
}
