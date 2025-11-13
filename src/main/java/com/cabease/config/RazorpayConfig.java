package com.cabease.config;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RazorpayConfig {
    @Value("${razorpay.key.id:rzp_test_demo}")
    private String razorpayKeyId;
    @Value("${razorpay.key.secret:demo_secret}")
    private String razorpayKeySecret;
    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }
    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }
    public String getRazorpayKeySecret() {
        return razorpayKeySecret;
    }
}
