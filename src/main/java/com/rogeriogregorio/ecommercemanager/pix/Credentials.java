package com.rogeriogregorio.ecommercemanager.pix;

import com.rogeriogregorio.ecommercemanager.exceptions.PixException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Credentials implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String clientId;
    private String clientSecret;
    private String certificate;
    private boolean sandbox;
    private boolean debug;

    public Credentials() {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream credentialsFile = classLoader.getResourceAsStream("credentials.json");
        JSONTokener tokener = new JSONTokener(Objects.requireNonNull(credentialsFile));
        JSONObject credentials = new JSONObject(tokener);

        try {
            credentialsFile.close();
        } catch (IOException ex) {
            throw new PixException("Impossible to close file credentials.json", ex);
        }

        this.clientId = credentials.getString("client_id");
        this.clientSecret = credentials.getString("client_secret");
        this.certificate = credentials.getString("certificate");
        this.sandbox = credentials.getBoolean("sandbox");
        this.debug = credentials.getBoolean("debug");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCertificate() {
        return certificate;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public boolean isDebug() {
        return debug;
    }
}