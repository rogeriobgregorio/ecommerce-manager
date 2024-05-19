package com.rogeriogregorio.ecommercemanager.pix;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public interface CredentialService {

    JSONObject options();

    String keyEVP();
}
