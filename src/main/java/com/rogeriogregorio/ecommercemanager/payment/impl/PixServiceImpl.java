package com.rogeriogregorio.ecommercemanager.payment.impl;

import br.com.efi.efisdk.EfiPay;
import com.rogeriogregorio.ecommercemanager.dto.*;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.exceptions.PaymentException;
import com.rogeriogregorio.ecommercemanager.payment.CredentialService;
import com.rogeriogregorio.ecommercemanager.payment.PixService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.DateFormatter;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(PixServiceImpl.class);

    @Autowired
    public PixServiceImpl(CredentialService credentials, DateFormatter dateFormatter,
                          CatchError catchError, DataMapper dataMapper) {

        this.credentials = credentials;
        this.dateFormatter = dateFormatter;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public EvpKeyDto createEvpKey() {

        return catchError.run(() -> {

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            JSONObject efiPayResponse = efiPay.call(CREATE_EVP, new HashMap<>(), new JSONObject());

            EvpKeyDto evpKey = dataMapper.fromJson(efiPayResponse, EvpKeyDto.class);
            LOGGER.info("EVP key created: {}", evpKey);
            return evpKey;
        });
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixChargeDto createImmediatePixCharge(Order order) {

        return catchError.run(() -> {

            JSONObject body = buildChargeBody(order);

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            JSONObject efiPayResponse = efiPay.call(CREATE_IMMEDIATE_CHARGE, new HashMap<>(), body);

            PixChargeDto pixCharge = dataMapper.fromJson(efiPayResponse, PixChargeDto.class);
            LOGGER.info("Immediate charge Pix created: {}", pixCharge);
            return pixCharge;
        });
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixQRCodeDto generatePixQRCode(PixChargeDto pixCharge) {

        return catchError.run(() -> {

            String locId = String.valueOf(pixCharge.getLoc().getId());

            Map<String, String> params = new HashMap<>();
            params.put("id", locId);

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            Map<String, Object> efiPayResponse = efiPay.call(GENERATE_QRCODE, params, new HashMap<>());

            PixQRCodeDto pixQRCode = dataMapper.fromMap(efiPayResponse, PixQRCodeDto.class);
            LOGGER.info("Generated QRCode Pix: {}", pixQRCode);
            return pixQRCode;
        });
    }

    @Retryable(retryFor = { Exception.class }, maxAttempts = 10,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public PixListChargeDto listPixCharges(String startDate, String endDate) {

        return catchError.run(() -> {

            Map<String, String> params = new HashMap<>();
            params.put("inicio", dateFormatter.toISO8601(startDate));
            params.put("fim", dateFormatter.toISO8601(endDate));

            EfiPay efiPay = new EfiPay(credentials.getOptions());
            JSONObject efiPayResponse = efiPay.call(LIST_CHARGES, params, new JSONObject());
            return dataMapper.fromJson(efiPayResponse, PixListChargeDto.class);
        });
    }

    @Recover
    public EvpKeyDto recoverCreateEvpKey(Exception ex) {

        LOGGER.error("Failed to create EVP after retries: {}", ex.getMessage());
        throw new PaymentException("Unable to create EVP after multiple attempts", ex);
    }

    @Recover
    public PixChargeDto recoverCreateImmediatePixCharge(Exception ex, Order order) {

        LOGGER.error("Failed to create immediate Pix charge after retries: {}", ex.getMessage());
        throw new PaymentException("Unable to create Pix charge after multiple attempts", ex);
    }

    @Recover
    public PixQRCodeDto recoverGeneratePixQRCode(Exception ex, PixChargeDto pixCharge) {

        LOGGER.error("Failed to generate Pix QRCode link after retries: {}", ex.getMessage());
        throw new PaymentException("Unable to generate Pix QRCode link after multiple attempts", ex);
    }

    @Recover
    public PixListChargeDto recoverListPixCharges(Exception ex, String startDate, String endDate) {

        LOGGER.error("Failed to list Pix charges after retries: {}", ex.getMessage());
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