package com.rogeriogregorio.ecommercemanager.pix.impl;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import com.rogeriogregorio.ecommercemanager.pix.Credentials;
import com.rogeriogregorio.ecommercemanager.pix.PixService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PixServiceImpl implements PixService {

    public String createPixEVP() {

        Credentials credentials = new Credentials();

        JSONObject options = new JSONObject();
        options.put("client_id", credentials.getClientId());
        options.put("client_secret", credentials.getClientSecret());
        options.put("certificate", credentials.getCertificate());
        options.put("sandbox", credentials.isSandbox());

        try {
            EfiPay efiPay = new EfiPay(options);
            JSONObject response = efiPay.call("pixCreateEvp", new HashMap<String, String>(), new JSONObject());
            System.out.println(response);
            return response.toString();

        } catch (EfiPayException ex) {
            throw new RuntimeException("Error while trying to create Pix EVP", ex);

        } catch (Exception ex) {
            throw new RuntimeException("Error while trying to create Pix EVP", ex);
        }
    }
}