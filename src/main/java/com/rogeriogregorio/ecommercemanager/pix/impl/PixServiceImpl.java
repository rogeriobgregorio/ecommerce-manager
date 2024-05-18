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
    private HashMap<String, String> params = new HashMap<>();
    private JSONObject body = new JSONObject();

    @Autowired
    public PixServiceImpl(PixCredentialConfig credentials) {
        this.credentials = credentials;
    }

    public String createPixEVP() {

        try {
            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject pixEVP = efiPay.call("pixCreateEvp", params, body);

            return pixEVP.toString();

        } catch (Exception ex) {
            throw new PixException("Error while trying to create Pix EVP", ex);
        }
    }
}