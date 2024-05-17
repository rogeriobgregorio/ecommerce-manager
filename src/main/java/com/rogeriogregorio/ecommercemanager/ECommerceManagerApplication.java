package com.rogeriogregorio.ecommercemanager;

import com.rogeriogregorio.ecommercemanager.pix.config.PixCredentialConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;

@SpringBootApplication
public class ECommerceManagerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ECommerceManagerApplication.class, args);
	}
}
