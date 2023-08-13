package com.thoughtworks.fast.rent.service;

import com.thoughtworks.fast.rent.infrastructure.client.InsuranceClient;
import com.thoughtworks.fast.rent.infrastructure.repository.InsuranceApplyRepository;
import com.thoughtworks.fast.rent.mapper.InsuranceMapper;
import com.thoughtworks.fast.rent.model.entity.InsuranceApplyEntity;
import com.thoughtworks.fast.rent.model.thirdparty.request.InsuranceRequest;
import feign.FeignException.ServiceUnavailable;
import feign.Request;
import feign.Request.Body;
import feign.Request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static com.thoughtworks.fast.rent.enums.Constant.INSURANCE_APPLY_TOPIC_NAME;
import static com.thoughtworks.fast.rent.enums.InsuranceApplyStatus.SEND;
import static com.thoughtworks.fast.rent.enums.InsuranceApplyStatus.SENDING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsuranceServiceTest {

    @InjectMocks
    private InsuranceService insuranceService;

    @Mock
    private InsuranceApplyRepository insuranceApplyRepository;

    @Mock
    private InsuranceClient insuranceClient;

    @Mock
    private KafkaTemplate<String, InsuranceRequest> kafkaTemplate;

    private final InsuranceMapper insuranceMapper = Mappers.getMapper(InsuranceMapper.class);

    private final static String INSURANCE_ID = "IN-001";
    private final static InsuranceRequest INSURANCE_REQUEST = InsuranceRequest.builder().insuranceContractId(INSURANCE_ID)
            .applyId("applyId0001")
            .vehicleId("V-001").address("xxx street").build();
    @Test
    @DisplayName("当执行insuranceService.apply方法时,应该向消息队列发送一条消息,并且将请求数据保存数据库,且状态为SENDING")
    void apply() {
        // given & when
        insuranceService.apply(INSURANCE_REQUEST);

        //then
        verify(kafkaTemplate, only()).send(INSURANCE_APPLY_TOPIC_NAME, INSURANCE_REQUEST.getInsuranceContractId(),
                INSURANCE_REQUEST);
        verify(insuranceApplyRepository, only()).save(argThat(entity -> {
            assertEquals(INSURANCE_ID, entity.getInsuranceContractId());
            assertEquals(INSURANCE_REQUEST.getApplyId(), entity.getApplyId());
            assertEquals(INSURANCE_REQUEST.getAddress(), entity.getAddress());
            assertEquals(SENDING, entity.getStatus());
            return true;
        }));

    }

    @Test
    @DisplayName("当调用保险公司的保险出险请求时,如果对方服务正常,则将请求数据保存数据库,且状态为SEND")
    void sendToInsuranceSystemTest1() {
        // given
        doNothing().when(insuranceClient).apply(any());
        final InsuranceApplyEntity mapperEntity = insuranceMapper.toEntity(INSURANCE_REQUEST);
        when(insuranceApplyRepository.findByApplyId(INSURANCE_REQUEST.getApplyId()))
                .thenReturn(mapperEntity);

        // when
        insuranceService.sendToInsuranceSystem(INSURANCE_REQUEST);
        
        // then
        verify(kafkaTemplate, never()).send(any(),any(),any());
        verify(insuranceApplyRepository, times(1)).save(argThat(entity -> {
            assertEquals(INSURANCE_ID, entity.getInsuranceContractId());
            assertEquals(INSURANCE_REQUEST.getApplyId(), entity.getApplyId());
            assertEquals(INSURANCE_REQUEST.getAddress(), entity.getAddress());
            assertEquals(SEND, entity.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("当调用保险公司的保险出险请求时,如果调用过程中发现异常,则发送消息至消息队列,不更新数据库状态")
    void sendToInsuranceSystemTest2() {
        // given
        var request = Request.create(HttpMethod.POST, "fakeUrl", Map.of(), Body.create(""), null);
        when(insuranceClient.apply(any())).thenThrow(new ServiceUnavailable("service unavailable", request, null, null));

        // when
        insuranceService.sendToInsuranceSystem(INSURANCE_REQUEST);

        // then
        verify(kafkaTemplate, only()).send(INSURANCE_APPLY_TOPIC_NAME,
                INSURANCE_REQUEST.getInsuranceContractId(),
                INSURANCE_REQUEST);
        verify(insuranceApplyRepository, never()).findByApplyId(anyString());
        verify(insuranceApplyRepository, never()).save(any());
    }
}