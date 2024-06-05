package com.rogeriogregorio.ecommercemanager.payment;

import com.rogeriogregorio.ecommercemanager.dto.EvpKeyDto;
import com.rogeriogregorio.ecommercemanager.dto.PixChargeDto;
import com.rogeriogregorio.ecommercemanager.dto.PixListChargeDto;
import com.rogeriogregorio.ecommercemanager.dto.PixQRCodeDto;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

@Component
public interface PixService {

    EvpKeyDto createEvpKey();

    PixChargeDto createImmediatePixCharge(Order order);

    PixQRCodeDto generatePixQRCode(PixChargeDto pixCharge);

    PixListChargeDto listPixCharges(String startDate, String endDate);
}
