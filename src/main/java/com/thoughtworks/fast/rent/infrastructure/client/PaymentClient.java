package com.thoughtworks.fast.rent.infrastructure.client;


import com.thoughtworks.fast.rent.model.thirdparty.request.PaymentRequest;
import com.thoughtworks.fast.rent.model.thirdparty.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "paymentClient", url = "https://payment.com/payment",configuration = PaymentClientConfiguration.class)
public interface PaymentClient {

    @PostMapping("/payment")
    PaymentResponse pay(PaymentRequest paymentRequest);

    @GetMapping("/payment/{contractId}")
    PaymentResponse getPayResult(@PathVariable(name = "contractId") String contractId);

}
