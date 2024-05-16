package com.rogeriogregorio.ecommercemanager.pix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

@Service
public class AuthenticatePixServiceImpl implements AuthenticatePixService{


    public String generatePixAuthenticationToken() {

        String clientId = "Client_Id_7e61a07b26886c83429c48d484c01d9dd3fa7bb8";
        String clientSecret = "Client_Secret_0e08cbf3c9fdacff29d16da5a8c3f818a7585fd7";

        try {
            String basicAuth = Base64.getEncoder().encodeToString((clientId + ':' + clientSecret).getBytes());

            System.setProperty("javax.net.ssl.keyStore", "Production-Ecommerce-Manager.p12");
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            URL url = new URL("https://pix.api.efipay.com.br/oauth/token");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + basicAuth);
            conn.setSSLSocketFactory(sslsocketfactory);
            String input = "{\"grant_type\": \"client_credentials\"}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(reader);

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            conn.disconnect();

            return response.toString();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}