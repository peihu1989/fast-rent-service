package com.thoughtworks.fast.rent.service.listner;

import com.thoughtworks.fast.rent.FastRentServiceApplicationTests;
import com.thoughtworks.fast.rent.model.thirdparty.request.InsuranceRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import static com.thoughtworks.fast.rent.enums.Constant.INSURANCE_APPLY_TOPIC_NAME;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class InsuranceListenerTest extends FastRentServiceApplicationTests {

    @Autowired
    private KafkaTemplate<String, InsuranceRequest> kafkaTemplate;

    @SneakyThrows
    @Test
    @DisplayName("当监听到topic:fast-rent-service.insurance-apply中有新的消息时,应该调用insuranceService的sendToInsuranceSystem方通知保险公司")
    void listenInsuranceApply() {
        // given
        final String INSURANCE_ID = "IN-001";
        final InsuranceRequest insuranceRequest = InsuranceRequest.builder().insuranceContractId(INSURANCE_ID).applyId("applyId")
                .address("address").build();

        // when
        kafkaTemplate.send(INSURANCE_APPLY_TOPIC_NAME, INSURANCE_ID, insuranceRequest).get();

        //then
        verify(insuranceService, timeout(5000))
                .sendToInsuranceSystem(insuranceRequest);
    }
}