package com.rogeriogregorio.ecommercemanager.pix;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public interface PixService {

    String createPixEVP();

    JSONObject createImmediatePixCharge(Order order);

    String generatePixQRCodeLink(JSONObject pixCharge);

    String listPaidPixCharges(String startDate, String endDate);
}
