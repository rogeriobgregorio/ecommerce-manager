package com.rogeriogregorio.ecommercemanager.pix.impl;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import com.rogeriogregorio.ecommercemanager.exceptions.PixException;
import com.rogeriogregorio.ecommercemanager.pix.PixService;
import com.rogeriogregorio.ecommercemanager.pix.config.PixCredentialConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PixServiceImpl implements PixService {

    private final PixCredentialConfig credentials;

    @Autowired
    public PixServiceImpl(PixCredentialConfig credentials) {
        this.credentials = credentials;
    }

    public String createPixEVP() {

        HashMap<String, String> params = new HashMap<>();
        JSONObject body = new JSONObject();

        try {
            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject pixEVP = efiPay.call("pixCreateEvp", params, body);

            return pixEVP.toString();

        } catch (Exception ex) {
            throw new PixException("Error while trying to create Pix EVP", ex);
        }
    }

    public String createImmediatePixCharge() {

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", "12345678909").put("nome", "Francisco da Silva"));
        body.put("valor", new JSONObject().put("original", "0.01"));
        body.put("chave", credentials.getKeyEVP());

        JSONArray infoAdicionais = new JSONArray();
        infoAdicionais.put(new JSONObject().put("nome", "Campo 1").put("valor", "Informação Adicional1 do PSP-Recebedor"));
        infoAdicionais.put(new JSONObject().put("nome", "Campo 2").put("valor", "Informação Adicional2 do PSP-Recebedor"));
        body.put("infoAdicionais", infoAdicionais);

        try {
            EfiPay efi = new EfiPay(credentials.options());
            JSONObject pixCharge = efi.call("pixCreateImmediateCharge", new HashMap<String, String>(), body);

            return pixCharge.toString();

        } catch (Exception ex) {
            throw new PixException("Error while trying to create immediate Pix charge", ex);
        }
    }

    public String generatePixQRCodeLink() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", "1");

        try {
            EfiPay efi = new EfiPay(credentials.options());
            Map<String, Object> pixQRCodeLink = efi.call("pixGenerateQRCode", params, new HashMap<String, Object>());

            return (String) pixQRCodeLink.get("linkVisualizacao");

        } catch (Exception ex) {
            throw new PixException("Error while trying to generate Pix QRCode link", ex);
        }
    }

    public String listPaidPixCharges() {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("inicio", "2024-05-18T18:01:35Z");
        params.put("fim", "2024-05-18T16:01:35Z");

        try {
            EfiPay efi = new EfiPay(credentials.options());
            JSONObject pixListCharges = efi.call("pixListCharges", params, new JSONObject());

            return pixListCharges.toString();

        }
        catch (Exception ex) {
            throw new PixException("Error while trying to generate Pix QRCode link", ex);
        }
    }
}