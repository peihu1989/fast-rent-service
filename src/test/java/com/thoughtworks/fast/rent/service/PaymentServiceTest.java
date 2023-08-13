package com.thoughtworks.fast.rent.service;

import com.thoughtworks.fast.rent.infrastructure.client.PaymentClient;
import com.thoughtworks.fast.rent.infrastructure.repository.PaymentRepository;
import com.thoughtworks.fast.rent.model.entity.PaymentEntity;
import com.thoughtworks.fast.rent.model.thirdparty.request.PaymentRequest;
import com.thoughtworks.fast.rent.model.thirdparty.response.PaymentResponse;
import feign.FeignException.GatewayTimeout;
import feign.FeignException.ServiceUnavailable;
import feign.Request;
import feign.Request.Body;
import feign.Request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static com.thoughtworks.fast.rent.enums.Constant.PENDING_PAYMENT_TOPIC_NAME;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.FAILED;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.INSUFFICIENT_BALANCE;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.PENDING;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;


    @Test
    @DisplayName("当第三方支付系统服务可用,使用feignClient调用接口,mock第三方返回支付成功,能够正常返回,不向消息队列发送消息")
    void serviceTest1() {
        // given
        final String CONTRACT_ID = "C-0000000001";
        var paymentRequest = PaymentRequest.builder().contractId(CONTRACT_ID).amount(BigDecimal.ONE).build();
        when(paymentClient.pay(any())).thenReturn(PaymentResponse.succeed());

        // when
        var payResult = paymentService.pay(paymentRequest);

        // then
        verify(paymentClient, only()).pay(paymentRequest);
        verify(paymentRepository, only()).save(argThat(entity -> {
            assertEquals(CONTRACT_ID, entity.getContractId());
            assertEquals(SUCCESS, entity.getStatus());
            return true;
        }));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
        assertEquals(SUCCESS.name(), payResult.getCode());
        assertEquals("支付成功", payResult.getMessage());
    }

    @Test
    @DisplayName("当第三方支付系统服务可用,使用feignClient调用接口,mock第三方余额不足支付失败,能够正常返回,不向消息队列发送消息")
    void serviceTest2() {
        // given
        final String CONTRACT_ID = "C-0000000002";
        var paymentRequest = PaymentRequest.builder().contractId(CONTRACT_ID).amount(BigDecimal.ONE).build();
        when(paymentClient.pay(any())).thenReturn(PaymentResponse.insufficientBalance());

        // when
        var payResult = paymentService.pay(paymentRequest);

        // then
        verify(paymentClient, only()).pay(paymentRequest);
        verify(paymentRepository, only()).save(argThat(entity -> {
            assertEquals(CONTRACT_ID, entity.getContractId());
            assertEquals(INSUFFICIENT_BALANCE, entity.getStatus());
            return true;
        }));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
        assertEquals(INSUFFICIENT_BALANCE.name(), payResult.getCode());
        assertEquals("支付失败,账户余额不足", payResult.getMessage());
    }


    @Test
    @DisplayName(
            "当第三方支付系统服务不可用,使用feignClient调用接口,mock第三方服务不可用,返回支付服务异常,请重新支付,不向消息队列发送消息")
    void serviceTest3() {
        // given
        final String CONTRACT_ID = "C-0000000003";
        var paymentRequest = PaymentRequest.builder().contractId(CONTRACT_ID).amount(BigDecimal.ONE).build();
        var request = Request.create(HttpMethod.POST, "fakeUrl", Map.of(), Body.create(""), null);
        when(paymentClient.pay(any())).thenThrow(new ServiceUnavailable("service unavailable", request, null, null));

        // when
        var payResult = paymentService.pay(paymentRequest);

        // then
        verify(paymentClient, only()).pay(paymentRequest);
        verify(paymentRepository, only()).save(argThat(entity -> {
            assertEquals(CONTRACT_ID, entity.getContractId());
            assertEquals(FAILED, entity.getStatus());
            return true;
        }));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
        assertEquals(FAILED.name(), payResult.getCode());
        assertEquals("支付服务异常,请重新支付", payResult.getMessage());
    }

    @Test
    @DisplayName("当第三方支付系统服务不可用,使用feignClient调用接口,mock第三方服务返回超时,返回支付中,请稍后查询支付状态,并且向消息队列发送消息")
    void serviceTest4() {
        // given
        final String CONTRACT_ID = "C-0000000004";
        var paymentRequest = PaymentRequest.builder().contractId(CONTRACT_ID).amount(BigDecimal.ONE).build();
        var request = Request.create(HttpMethod.POST, "fakeUrl", Map.of(), Body.create(""), null);
        when(paymentClient.pay(any())).thenThrow(new GatewayTimeout("gateway timeout", request, null, null));

        // when
        var payResult = paymentService.pay(paymentRequest);

        // then
        verify(paymentClient, only()).pay(paymentRequest);
        verify(kafkaTemplate, only()).send(argThat(topic -> topic.equals(PENDING_PAYMENT_TOPIC_NAME)),
                argThat(key -> key.equals(CONTRACT_ID)),
                argThat(value -> value.equals(CONTRACT_ID)));
        verify(paymentRepository, only()).save(argThat(entity -> {
            assertEquals(CONTRACT_ID, entity.getContractId());
            assertEquals(PENDING, entity.getStatus());
            return true;
        }));
        assertEquals(PENDING.name(), payResult.getCode());
        assertEquals("支付中,请稍后查询支付状态", payResult.getMessage());
    }


    @Test
    @DisplayName("当使用feignClient查询支付结果接口,mock第三方返回支付成功,应该更新支付状态,不向消息队列发送消息")
    void serviceTest5() {
        // given
        final String CONTRACT_ID = "C-0000000005";
        when(paymentClient.getPayResult(CONTRACT_ID)).thenReturn(PaymentResponse.succeed());
        when(paymentRepository.findByContractId(CONTRACT_ID)).thenReturn(PaymentEntity.builder().id(1L).contractId(CONTRACT_ID).status(PENDING).build());

        // when
        paymentService.getPaymentResult(CONTRACT_ID);

        // then
        verify(paymentClient, only()).getPayResult(CONTRACT_ID);
        verify(paymentRepository, times(1)).save(argThat(entity -> {
            assertEquals(CONTRACT_ID, entity.getContractId());
            assertEquals(SUCCESS, entity.getStatus());
            return true;
        }));
        verify(kafkaTemplate, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("当使用feignClient查询支付结果接口,fake server出现feignException,不更新数据库状态,并且向消息队列发送消息")
    void serviceTest6() {
        // given
        final String CONTRACT_ID = "C-0000000006";
        var request = Request.create(HttpMethod.GET, "fakeUrl", Map.of(), Body.create(""), null);
        when(paymentClient.getPayResult(CONTRACT_ID)).thenThrow(new GatewayTimeout("gateway timeout", request, null, null));
        when(paymentRepository.findByContractId(CONTRACT_ID)).thenReturn(PaymentEntity.builder().id(1L).contractId(CONTRACT_ID).status(PENDING).build());

        // when
        paymentService.getPaymentResult(CONTRACT_ID);

        // then
        verify(paymentClient, only()).getPayResult(CONTRACT_ID);
        verify(paymentRepository, never()).save(any());
        verify(kafkaTemplate, only()).send(PENDING_PAYMENT_TOPIC_NAME, CONTRACT_ID, CONTRACT_ID);
    }

}