package com.rogeriogregorio.ecommercemanager.pix.impl;

import br.com.efi.efisdk.EfiPay;
import com.rogeriogregorio.ecommercemanager.dto.PixChargeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixEVPKeyDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixListChargeDTO;
import com.rogeriogregorio.ecommercemanager.dto.PixQRCodeDTO;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.exceptions.PixException;
import com.rogeriogregorio.ecommercemanager.pix.CredentialService;
import com.rogeriogregorio.ecommercemanager.pix.PixService;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.DateFormatter;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(PixServiceImpl.class);

    @Autowired
    public PixServiceImpl(CredentialService credentials, DateFormatter dateFormatter,
                          ErrorHandler errorHandler, DataMapper dataMapper) {

        this.credentials = credentials;
        this.dateFormatter = dateFormatter;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10, backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixEVPKeyDTO createPixEVPKey() {

        return errorHandler.catchException(() -> {

            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject efiPayResponse = efiPay.call(CREATE_EVP, new HashMap<>(), new JSONObject());

            PixEVPKeyDTO EVPKey = dataMapper.fromJson(efiPayResponse, PixEVPKeyDTO.class);
            logger.info("EVP pix key created: {}", EVPKey.toString());

            return EVPKey;
        }, "Error while trying to create Pix EVP: ");
    }

    @Recover
    public String recoverCreatePixEVP(Exception ex) {

        logger.error("Failed to create Pix EVP after retries: {}", ex.getMessage());
        throw new PixException("Unable to create Pix EVP after multiple attempts", ex);
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10, backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixChargeDTO createImmediatePixCharge(Order order) {

        return errorHandler.catchException(() -> {

            JSONObject body = buildChargeBody(order);

            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject efiPayResponse = efiPay.call(CREATE_IMMEDIATE_CHARGE, new HashMap<>(), body);

            PixChargeDTO pixCharge = dataMapper.fromJson(efiPayResponse, PixChargeDTO.class);
            logger.info("Immediate charge Pix created: {}", pixCharge.toString());

            return pixCharge;
        }, "Error while trying to create immediate Pix charge: ");
    }

    @Recover
    public PixChargeDTO recoverCreateImmediatePixCharge(Exception ex, Order order) {

        logger.error("Failed to create immediate Pix charge after retries: {}", ex.getMessage());
        throw new PixException("Unable to create Pix charge after multiple attempts", ex);
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10, backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixQRCodeDTO generatePixQRCode(PixChargeDTO pixCharge) {

        return errorHandler.catchException(() -> {

            String locId = String.valueOf(pixCharge.getLoc().getId());

            Map<String, String> params = new HashMap<>();
            params.put("id", locId);

            EfiPay efiPay = new EfiPay(credentials.options());
            Map<String, Object> efiPayResponse = efiPay.call(GENERATE_QRCODE, params, new HashMap<>());

            PixQRCodeDTO pixQRCode = dataMapper.fromHashMap(efiPayResponse, PixQRCodeDTO.class);
            logger.info("Generated QRCode Pix: {}", pixQRCode.toString());

            return pixQRCode;
        }, "Error while trying to generate Pix QRCode: ");
    }

    @Recover
    public PixChargeDTO recoverGeneratePixQRCode(Exception ex, PixChargeDTO pixCharge) {

        logger.error("Failed to generate Pix QRCode link after retries: {}", ex.getMessage());
        throw new PixException("Unable to generate Pix QRCode link after multiple attempts", ex);
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10, backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixListChargeDTO listPixCharges(String startDate, String endDate) {

        return errorHandler.catchException(() -> {

            Map<String, String> params = new HashMap<>();
            params.put("inicio", dateFormatter.toISO8601(startDate));
            params.put("fim", dateFormatter.toISO8601(endDate));

            EfiPay efiPay = new EfiPay(credentials.options());
            JSONObject efiPayResponse = efiPay.call(LIST_CHARGES, params, new JSONObject());

            return dataMapper.fromJson(efiPayResponse, PixListChargeDTO.class);
        }, "Error while trying to list paid pix charges: ");
    }

    @Recover
    public String recoverListPaidPixCharges(Exception ex, String startDate, String endDate) {

        logger.error("Failed to list paid Pix charges after retries: {}", ex.getMessage());
        throw new PixException("Unable to list paid Pix charges after multiple attempts", ex);
    }

    private JSONObject buildChargeBody(Order order) {

        String debtorCpf = order.getClient().getCpf();
        String debtorName = order.getClient().getName();
        String chargeAmount = order.getTotalFinal().toString();

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", debtorCpf).put("nome", debtorName));
        body.put("valor", new JSONObject().put("original", chargeAmount));
        body.put("chave", credentials.keyEVP());

        String orderNumber = order.getId().toString();
        String items = order.getProductQuantities().toString();

        JSONArray additionalInfo = new JSONArray();
        additionalInfo.put(new JSONObject().put("nome", "Número do Pedido").put("valor", orderNumber));
        additionalInfo.put(new JSONObject().put("nome", "Items do Pedido").put("valor", items));
        body.put("infoAdicionais", additionalInfo);

        return body;
    }
}