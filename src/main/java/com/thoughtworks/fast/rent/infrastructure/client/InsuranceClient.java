package com.thoughtworks.fast.rent.infrastructure.client;


import com.thoughtworks.fast.rent.model.thirdparty.request.InsuranceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "insuranceClient", url = "https://insurance.com/apply")
public interface InsuranceClient {

    @PostMapping("/insurance/apply")
    Void apply(InsuranceRequest insuranceRequest);

}
