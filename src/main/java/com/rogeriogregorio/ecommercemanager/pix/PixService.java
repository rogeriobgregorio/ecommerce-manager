package com.rogeriogregorio.ecommercemanager.pix;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public interface PixService {

    String createPixEVP();

    String createImmediatePixCharge(Order order);

    String generatePixQRCodeLink(String id);

    String listPaidPixCharges(String startDate, String endDate);
}
