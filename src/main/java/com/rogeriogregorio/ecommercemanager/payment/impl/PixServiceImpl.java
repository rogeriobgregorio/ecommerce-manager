package com.rogeriogregorio.ecommercemanager.payment.impl;

import br.com.efi.efisdk.EfiPay;
import com.rogeriogregorio.ecommercemanager.dto.*;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.exceptions.PaymentException;
import com.rogeriogregorio.ecommercemanager.payment.CredentialService;
import com.rogeriogregorio.ecommercemanager.payment.PixService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.DateFormatter;
import com.rogeriogregorio.ecommercemanager.utils.ErrorHandler;
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
    private static final Logger logger = LogManager.getLogger(PixServiceImpl.class);

    @Autowired
    public PixServiceImpl(CredentialService credentials,
                          DateFormatter dateFormatter,
                          ErrorHandler errorHandler,
                          DataMapper dataMapper) {

        this.credentials = credentials;
        this.dateFormatter = dateFormatter;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public EvpKeyDto createEvpKey() {

        return errorHandler.catchException(() -> {

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            JSONObject efiPayResponse = efiPay.call(CREATE_EVP, new HashMap<>(), new JSONObject());

            EvpKeyDto evpKey = dataMapper.fromJson(efiPayResponse, EvpKeyDto.class);
            logger.info("EVP key created: {}", evpKey);

            return evpKey;
        }, "Error while trying to create EVP key: ");
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixChargeDto createImmediatePixCharge(Order order) {

        return errorHandler.catchException(() -> {

            JSONObject body = buildChargeBody(order);

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            JSONObject efiPayResponse = efiPay.call(CREATE_IMMEDIATE_CHARGE, new HashMap<>(), body);

            PixChargeDto pixCharge = dataMapper.fromJson(efiPayResponse, PixChargeDto.class);
            logger.info("Immediate charge Pix created: {}", pixCharge);

            return pixCharge;
        }, "Error while trying to create immediate Pix charge: ");
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixQRCodeDto generatePixQRCode(PixChargeDto pixCharge) {

        return errorHandler.catchException(() -> {

            String locId = String.valueOf(pixCharge.getLoc().getId());

            Map<String, String> params = new HashMap<>();
            params.put("id", locId);

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            Map<String, Object> efiPayResponse = efiPay.call(GENERATE_QRCODE, params, new HashMap<>());

            PixQRCodeDto pixQRCode = dataMapper.fromMap(efiPayResponse, PixQRCodeDto.class);
            logger.info("Generated QRCode Pix: {}", pixQRCode);

            return pixQRCode;
        }, "Error while trying to generate Pix QRCode: ");
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixListChargeDto listPixCharges(String startDate, String endDate) {

        return errorHandler.catchException(() -> {

            Map<String, String> params = new HashMap<>();
            params.put("inicio", dateFormatter.toISO8601(startDate));
            params.put("fim", dateFormatter.toISO8601(endDate));

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            JSONObject efiPayResponse = efiPay.call(LIST_CHARGES, params, new JSONObject());

            return dataMapper.fromJson(efiPayResponse, PixListChargeDto.class);
        }, "Error while trying to list pix charges: ");
    }

    @Recover
    public EvpKeyDto recoverCreateEvpKey(Exception ex) {

        logger.error("Failed to create EVP after retries: {}", ex.getMessage());
        throw new PaymentException("Unable to create EVP after multiple attempts", ex);
    }

    @Recover
    public PixChargeDto recoverCreateImmediatePixCharge(Exception ex, Order order) {

        logger.error("Failed to create immediate Pix charge after retries: {}", ex.getMessage());
        throw new PaymentException("Unable to create Pix charge after multiple attempts", ex);
    }

    @Recover
    public PixQRCodeDto recoverGeneratePixQRCode(Exception ex, PixChargeDto pixCharge) {

        logger.error("Failed to generate Pix QRCode link after retries: {}", ex.getMessage());
        throw new PaymentException("Unable to generate Pix QRCode link after multiple attempts", ex);
    }

    @Recover
    public PixListChargeDto recoverListPixCharges(Exception ex, String startDate, String endDate) {

        logger.error("Failed to list Pix charges after retries: {}", ex.getMessage());
        throw new PaymentException("Unable to list Pix charges after multiple attempts", ex);
    }

    private JSONObject buildChargeBody(Order order) {

        OrderDetailsDto orderDetails = new OrderDetailsDto(order);

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", orderDetails.getCpf()).put("nome", orderDetails.getName()));
        body.put("valor", new JSONObject().put("original", orderDetails.getAmount()));
        body.put("chave", credentials.getKeyEVP());

        JSONArray additionalInfo = new JSONArray();
        additionalInfo.put(new JSONObject().put("nome", "ID do Pedido").put("valor", orderDetails.getOrderId()));
        additionalInfo.put(new JSONObject().put("nome", "Items do Pedido").put("valor", orderDetails.getItems()));
        body.put("infoAdicionais", additionalInfo);

        return body;
    }
}