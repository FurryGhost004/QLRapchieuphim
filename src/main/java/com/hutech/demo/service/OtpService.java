package com.hutech.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private Map<String, String> otpStorage = new HashMap<>();

    public String generateOtp(String phone) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(phone, otp);
        return otp;
    }

    public void sendOtp(String phone) {
        String otp = generateOtp(phone);
        System.out.println("OTP gửi tới " + phone + ": " + otp);
    }

    public boolean verifyOtp(String phone, String otp) {
        return otp.equals(otpStorage.get(phone));
    }
}