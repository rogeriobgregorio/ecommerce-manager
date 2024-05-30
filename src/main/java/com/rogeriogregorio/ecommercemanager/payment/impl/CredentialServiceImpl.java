package com.rogeriogregorio.ecommercemanager.payment.impl;

import com.rogeriogregorio.ecommercemanager.payment.CredentialService;
import com.rogeriogregorio.ecommercemanager.payment.config.CredentialConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialServiceImpl implements CredentialService {

    private final CredentialConfig pixCredential;

    @Autowired
    public CredentialServiceImpl(CredentialConfig pixCredential) {
        this.pixCredential = pixCredential;
    }

    public JSONObject getOptions() {

        JSONObject options = new JSONObject();
        options.put("client_id", pixCredential.getClientId());
        options.put("client_secret", pixCredential.getClientSecret());
        options.put("certificate", pixCredential.getCertificate());
        options.put("sandbox", pixCredential.isSandbox());

        return options;
    }
    
    public String getKeyEVP() {
        return pixCredential.getKeyEVP();
    }
}
