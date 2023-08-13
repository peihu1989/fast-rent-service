package com.thoughtworks.fast.rent.service;


import com.thoughtworks.fast.rent.enums.InsuranceApplyStatus;
import com.thoughtworks.fast.rent.infrastructure.client.InsuranceClient;
import com.thoughtworks.fast.rent.infrastructure.repository.InsuranceApplyRepository;
import com.thoughtworks.fast.rent.mapper.InsuranceMapper;
import com.thoughtworks.fast.rent.model.thirdparty.request.InsuranceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.mapstruct.factory.Mappers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.thoughtworks.fast.rent.enums.Constant.INSURANCE_APPLY_TOPIC_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceApplyRepository insuranceApplyRepository;
    private final KafkaTemplate<String, InsuranceRequest> kafkaTemplate;
    private final InsuranceClient insuranceClient;
    private final InsuranceMapper insuranceMapper = Mappers.getMapper(InsuranceMapper.class);

    public void apply(InsuranceRequest insuranceRequest) {
        var insuranceApplyEntity = insuranceMapper.toEntity(insuranceRequest);
        kafkaTemplate.send(INSURANCE_APPLY_TOPIC_NAME, insuranceRequest.getInsuranceContractId(), insuranceRequest);
        insuranceApplyRepository.save(insuranceApplyEntity);
    }


    public void sendToInsuranceSystem(InsuranceRequest insuranceRequest) {
        try {
            insuranceClient.apply(insuranceRequest);
            var applyEntity = insuranceApplyRepository.findByApplyId(insuranceRequest.getApplyId());
            applyEntity.setStatus(InsuranceApplyStatus.SEND);
            insuranceApplyRepository.save(applyEntity);
        }catch (Exception e){
            kafkaTemplate.send(INSURANCE_APPLY_TOPIC_NAME, insuranceRequest.getInsuranceContractId(), insuranceRequest);
        }
    }

}
