package com.thoughtworks.fast.rent.service.listner;

import com.thoughtworks.fast.rent.model.thirdparty.request.InsuranceRequest;
import com.thoughtworks.fast.rent.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.thoughtworks.fast.rent.enums.Constant.INSURANCE_APPLY_TOPIC_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceListener {

    private final InsuranceService insuranceService;

    @KafkaListener(topics = INSURANCE_APPLY_TOPIC_NAME,
            id = "fast-rent-service.insurance-apply.listener",
            properties = {
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.thoughtworks.fast.rent.model.thirdparty.request.InsuranceRequest"
            })
    public void listenInsuranceApply(InsuranceRequest insuranceRequest) {
        log.info("InsuranceRequest is {}", insuranceRequest);
        insuranceService.sendToInsuranceSystem(insuranceRequest);
    }

}
