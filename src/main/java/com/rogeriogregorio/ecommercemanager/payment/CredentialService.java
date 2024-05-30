package com.rogeriogregorio.ecommercemanager.payment;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public interface CredentialService {

    JSONObject getOptions();

    String getKeyEVP();
}
