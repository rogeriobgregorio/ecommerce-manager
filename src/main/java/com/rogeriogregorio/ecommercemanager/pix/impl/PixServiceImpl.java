package com.rogeriogregorio.ecommercemanager.pix.impl;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.exceptions.PixException;
import com.rogeriogregorio.ecommercemanager.pix.CredentialService;
import com.rogeriogregorio.ecommercemanager.pix.PixService;
import com.rogeriogregorio.ecommercemanager.util.DateFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PixServiceImpl implements PixService {

    private static final String CREATE_EVP = "pixCreateEvp";
    private static final String CREATE_IMMEDIATE_CHARGE = "pixCreateImmediateCharge";
    private static final String GENERATE_QRCODE = "pixGenerateQRCode";
    private static final String LIST_CHARGES = "pixListCharges";

    private final CredentialService credentials;
    private final DateFormatter dateFormatter;
    private final Logger logger = LogManager.getLogger(PixServiceImpl.class);

    @Autowired
    public PixServiceImpl(CredentialService credentials,
                          DateFormatter dateFormatter) {

        this.credentials = credentials;
        this.dateFormatter = dateFormatter;
    }

    public String createPixEVP() {

        try {
            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject pixEVP = efiPay.call(CREATE_EVP, new HashMap<>(), new JSONObject());
            String keyEVP = pixEVP.getString("chave");

            logger.info("EVP pix key created: {}", keyEVP);
            return keyEVP;

        } catch (EfiPayException ex) {
            logger.error("EfiPayException: {}, {}", ex.getError(), ex.getErrorDescription());
            throw new PixException("Error while trying to create Pix EVP", ex);

        } catch (Exception ex) {
            throw new PixException("Error while trying to create Pix EVP", ex);
        }
    }

    public String createImmediatePixCharge(Order order) {

        try {
            JSONObject body = buildChargeBody(order);

            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject pixCharge = efiPay.call(CREATE_IMMEDIATE_CHARGE, new HashMap<>(), body);
            String pixChargeId = pixCharge.getJSONObject("loc").getString("id");

            logger.info("Immediate charge Pix created: {}", pixCharge.toString());
            return pixChargeId;

        } catch (EfiPayException ex) {
            logger.error("EfiPayException: {}, {}", ex.getError(), ex.getErrorDescription());
            throw new PixException("Error while trying to create immediate Pix charge", ex);

        } catch (Exception ex) {
            throw new PixException("Error while trying to create immediate Pix charge", ex);
        }
    }

    public String generatePixQRCodeLink(String pixChargeId) {

        try {
            Map<String, String> params = Map.of("id", pixChargeId);

            EfiPay efiPay = new EfiPay(credentials.options());
            Map<String, Object> pixQRCode = efiPay.call(GENERATE_QRCODE, params, new HashMap<>());
            String pixQRCodeLink = pixQRCode.get("linkVisualizacao").toString();

            logger.info("Generated QRCode Pix link: {}", pixQRCodeLink);
            return pixQRCodeLink;

        } catch (EfiPayException ex) {
            logger.error("EfiPayException: {}, {}", ex.getError(), ex.getErrorDescription());
            throw new PixException("Error while trying to generate Pix QRCode link", ex);

        } catch (Exception ex) {
            throw new PixException("Error while trying to generate Pix QRCode link", ex);
        }
    }

    public String listPaidPixCharges(String startDate, String endDate) {

        try {
            Map<String, String> params = Map.of(
                    "inicio", dateFormatter.toISO8601(startDate),
                    "fim", dateFormatter.toISO8601(endDate)
            );

            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject pixListCharges = efiPay.call(LIST_CHARGES, params, new JSONObject());

            return pixListCharges.toString();

        } catch (EfiPayException ex) {
            logger.error("EfiPayException: {}, {}", ex.getError(), ex.getErrorDescription());
            throw new PixException("Error while trying to list paid pix charges", ex);

        } catch (Exception ex) {
            throw new PixException("Error while trying to list paid pix charges", ex);
        }
    }

    private JSONObject buildChargeBody(Order order) {

        String debtorCpf = order.getClient().getCpf();
        String debtorName = order.getClient().getName();
        String chargeAmount = order.getTotalFinal().toString();
        String orderNumber = order.getId().toString();
        String items = order.getProductQuantities().toString();

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", debtorCpf).put("nome", debtorName));
        body.put("valor", new JSONObject().put("original", chargeAmount));
        body.put("chave", credentials.keyEVP());

        JSONArray additionalInfo = new JSONArray();
        additionalInfo.put(new JSONObject().put("nome", "Número do Pedido").put("valor", orderNumber));
        additionalInfo.put(new JSONObject().put("nome", "Items do Pedido").put("valor", items));
        body.put("infoAdicionais", additionalInfo);

        return body;
    }
}