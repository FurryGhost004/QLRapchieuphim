package com.hutech.demo.service;

import com.hutech.demo.config.MoMoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MoMoService {

    @Autowired
    private MoMoConfig moMoConfig;

    public Map<String, Object> createPaymentRequest(String orderId, String orderInfo, long amount, String extraData)
            throws Exception {
        String requestId = String.valueOf(System.currentTimeMillis());
        String requestType = "captureWallet";

        // Tạo dữ liệu để ký (signature) theo thứ tự alphabet
        String rawHash = "accessKey=" + moMoConfig.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + moMoConfig.getNotifyUrl() +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + moMoConfig.getPartnerCode() +
                "&redirectUrl=" + moMoConfig.getReturnUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = signHmacSHA256(rawHash, moMoConfig.getSecretKey());

        System.out.println("MoMo rawHash: " + rawHash);
        System.out.println("MoMo signature: " + signature);

        // Tạo payload dưới dạng Map
        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", moMoConfig.getPartnerCode());
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", moMoConfig.getReturnUrl());
        body.put("ipnUrl", moMoConfig.getNotifyUrl());
        body.put("requestType", requestType);
        body.put("extraData", extraData);
        body.put("lang", "vi");
        body.put("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                moMoConfig.getEndpoint(),
                org.springframework.http.HttpMethod.POST,
                requestEntity,
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                });

        return response.getBody();
    }

    public boolean verifySignature(Map<String, String> returnParams) throws Exception {
        String rawHash = "accessKey=" + moMoConfig.getAccessKey() +
                "&amount=" + returnParams.getOrDefault("amount", "") +
                "&extraData=" + returnParams.getOrDefault("extraData", "") +
                "&message=" + returnParams.getOrDefault("message", "") +
                "&orderId=" + returnParams.getOrDefault("orderId", "") +
                "&orderInfo=" + returnParams.getOrDefault("orderInfo", "") +
                "&orderType=" + returnParams.getOrDefault("orderType", "") +
                "&partnerCode=" + returnParams.getOrDefault("partnerCode", "") +
                "&payType=" + returnParams.getOrDefault("payType", "") +
                "&requestId=" + returnParams.getOrDefault("requestId", "") +
                "&responseTime=" + returnParams.getOrDefault("responseTime", "") +
                "&resultCode=" + returnParams.getOrDefault("resultCode", "") +
                "&transId=" + returnParams.getOrDefault("transId", "");

        String expectedSignature = signHmacSHA256(rawHash, moMoConfig.getSecretKey());
        return expectedSignature.equals(returnParams.getOrDefault("signature", ""));
    }

    private String signHmacSHA256(String data, String secretKey) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
