package com.rogeriogregorio.ecommercemanager.pix.impl;

import br.com.efi.efisdk.EfiPay;
import com.rogeriogregorio.ecommercemanager.exceptions.PixException;
import com.rogeriogregorio.ecommercemanager.pix.PixService;
import com.rogeriogregorio.ecommercemanager.pix.config.PixCredentialConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PixServiceImpl implements PixService {

    private final PixCredentialConfig credentials;

    @Autowired
    public PixServiceImpl(PixCredentialConfig credentials) {
        this.credentials = credentials;
    }

    public String createPixEVP() {

        JSONObject options = new JSONObject();
        options.put("client_id", credentials.getClientId());
        options.put("client_secret", credentials.getClientSecret());
        options.put("certificate", credentials.getCertificate());
        options.put("sandbox", credentials.isSandbox());

        try {
            EfiPay efiPay = new EfiPay(options);
            JSONObject response = efiPay.call("pixCreateEvp", new HashMap<String, String>(), new JSONObject());
            return response.toString();

        } catch (Exception ex) {
            throw new PixException("Error while trying to create Pix EVP", ex);
        }
    }
}