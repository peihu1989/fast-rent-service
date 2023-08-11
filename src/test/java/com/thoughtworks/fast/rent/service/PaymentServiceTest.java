package com.thoughtworks.fast.rent.service;

import com.thoughtworks.fast.rent.enums.PaymentStatus;
import com.thoughtworks.fast.rent.infrastructure.client.PaymentClient;
import com.thoughtworks.fast.rent.infrastructure.repository.PaymentRepository;
import com.thoughtworks.fast.rent.model.thirdparty.request.PaymentRequest;
import com.thoughtworks.fast.rent.model.thirdparty.response.PaymentResponse;
import feign.FeignException;
import feign.FeignException.FeignClientException;
import feign.Request;
import feign.Request.Body;
import feign.Request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static com.thoughtworks.fast.rent.enums.PaymentStatus.INSUFFICIENT_BALANCE;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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


    @Test
    @DisplayName("当第三方支付系统服务可用,使用feignClient调用接口,mock第三方返回支付成功,能够正常返回")
    void should_return_success_when_payment_client_return_pay_success() {
        // given
        var paymentRequest = PaymentRequest.builder().contractId("C-0000000001").amount(BigDecimal.ONE).build();
        when(paymentClient.pay(any())).thenReturn(PaymentResponse.succeed());

        // when
        var payResult = paymentService.pay(paymentRequest);

        // then
        verify(paymentClient, only()).pay(paymentRequest);
        verify(paymentRepository, only()).save(any());
        assertEquals(SUCCESS, payResult.getPaymentStatus());
        assertEquals("支付成功", payResult.getMessage());
    }

    @Test
    @DisplayName("当第三方支付系统服务可用,使用feignClient调用接口,mock第三方余额不足支付失败,能够正常返回")
    void should_return_success_when_payment_client_return_() {
        // given
        var paymentRequest = PaymentRequest.builder().contractId("C-0000000001").amount(BigDecimal.ONE).build();
        when(paymentClient.pay(any())).thenReturn(PaymentResponse.insufficientBalance());

        // when
        var payResult = paymentService.pay(paymentRequest);

        // then
        verify(paymentClient, only()).pay(paymentRequest);
        verify(paymentRepository, only()).save(any());
        assertEquals(INSUFFICIENT_BALANCE, payResult.getPaymentStatus());
        assertEquals("支付失败,账户余额不足", payResult.getMessage());
    }




    @Test
    @DisplayName("当第三方支付系统服务不可用,使用feignClient调用接口,mock第三方服务不可用,返回支付失败")
    void should_return_insufficient_balance_when_feign_client_return_400() {
        // given
        var paymentRequest = PaymentRequest.builder().contractId("C-0000000001").amount(BigDecimal.ONE).build();
        when(paymentClient.pay(any())).thenReturn(PaymentResponse.succeed());
        var request = Request.create(HttpMethod.POST, "fakeUrl", Map.of(), Body.create(""), null);
        when(paymentClient.pay(paymentRequest)).thenThrow(FeignException.ServiceUnavailable
                new FeignClientException(400, "Bad Request", request, null, null));


        // when
        var payResult = paymentService.pay(paymentRequest);

        // then
        verify(paymentClient, times(1)).pay(paymentRequest);
        verify(paymentRepository, times(1)).save(paymentEntity);
        verify(paymentRepository, times(1)).findByContractIdAndPayId(paymentRequest.getContractId(), paymentRequest.getPayId());
        assertEquals(PaymentStatus.INSUFFICIENT_BALANCE.getMessage(), payResult);
    }

    //
    //@Test
    //void should_return_failed_when_feign_client_return_500() {
    //    // given
    //    var paymentRequest = PaymentRequest.builder().contractId(3L).payId(3L).amount(BigDecimal.ONE).build();
    //    var paymentEntity = PaymentEntity.builder().build();
    //    when(paymentMapper.toEntity(paymentRequest)).thenReturn(paymentEntity);
    //    var request = Request.create(HttpMethod.POST, "fakeUrl", Map.of(), Body.create(""), null);
    //    when(paymentClient.pay(paymentRequest)).thenThrow(
    //            new FeignClientException(500, "Server Error", request, null, null));
    //    when(paymentRepository.findByContractIdAndPayId(paymentRequest.getContractId(), paymentRequest.getPayId()))
    //            .thenReturn(PaymentEntity.builder()
    //                    .contractId(paymentRequest.getContractId())
    //                    .payId(paymentRequest.getPayId())
    //                    .status(PaymentStatus.FAILED)
    //                    .build()
    //            );
    //
    //    // when
    //    var payResult = paymentService.pay(paymentRequest);
    //
    //    // then
    //    verify(paymentClient, times(1)).pay(paymentRequest);
    //    verify(paymentRepository, times(1)).save(paymentEntity);
    //    verify(paymentRepository, times(1)).findByContractIdAndPayId(paymentRequest.getContractId(), paymentRequest.getPayId());
    //    assertEquals(PaymentStatus.FAILED.getMessage(), payResult);
    //}
}