package com.thoughtworks.fast.rent.facade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.fast.rent.FastRentServiceApplicationTests;
import com.thoughtworks.fast.rent.model.dto.CommonResult;
import com.thoughtworks.fast.rent.model.dto.payment.PaymentInfo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static com.thoughtworks.fast.rent.enums.PaymentStatus.FAILED;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.INSUFFICIENT_BALANCE;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.PENDING;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.SUCCESS;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(value = OrderAnnotation.class)
class PaymentControllerTest extends FastRentServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    @Order(1)
    @DisplayName("当第三方支付系统服务可用并且账户余额充足,用户调用支付接口的时候,返回状态码200,支付状态:SUCCESS,消息文本:支付成功")
    void should_get_status_200_and_pay_success_when_call_payment() {
        // given
        var paymentInfo = PaymentInfo.builder().amount(BigDecimal.ONE).build();
        when(paymentService.pay(argThat(request -> request.getContractId().equalsIgnoreCase("C-0000000001"))))
                .thenReturn(CommonResult.builder().code(SUCCESS.name()).message("支付成功").build());

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rent-contracts/C-0000000001/rental-payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(paymentInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code",is("SUCCESS")))
                .andExpect(jsonPath("$.message",is("支付成功")))
                .andReturn();
    }

    @SneakyThrows
    @Test
    @Order(2)
    @DisplayName("当第三方支付系统服务可用但是账户余额不足足,用户调用调用支付接口的时候,返回状态码200,支付状态:INSUFFICIENT_BALANCE,消息文本:支付失败,账户余额不足")
    void should_get_status_200_and_insufficient_balance_when_call_payment() {
        // given
        var paymentInfo = PaymentInfo.builder().amount(BigDecimal.ONE).build();
        when(paymentService.pay(argThat(request -> request.getContractId().equalsIgnoreCase("C-0000000001"))))
                .thenReturn(CommonResult.builder().code(INSUFFICIENT_BALANCE.name()).message("支付失败,账户余额不足").build());

        // when & then
        final MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rent-contracts/C-0000000001/rental-payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(paymentInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code",is("INSUFFICIENT_BALANCE")))
                .andExpect(jsonPath("$.message",is("支付失败,账户余额不足")))
                .andReturn();
    }

    @SneakyThrows
    @Test
    @Order(3)
    @DisplayName(
            "当第三方支付系统服务不可用或者返回异常,当调用支付接口的时候,返回状态码200,支付状态:FAILED,消息文本:第三方支付系统异常,请重新支付")
    void should_get_status_200_and_pay_failed_when_call_payment() {
        // given
        var paymentInfo = PaymentInfo.builder().amount(BigDecimal.ONE).build();
        when(paymentService.pay(argThat(request -> request.getContractId().equalsIgnoreCase("C-0000000001"))))
                .thenReturn(CommonResult.builder().code(FAILED.name()).message("第三方支付系统异常,请重新支付").build());

        // when & then
        final MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rent-contracts/C-0000000001/rental-payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(paymentInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code",is("FAILED")))
                .andExpect(jsonPath("$.message",is("第三方支付系统异常,请重新支付")))
                .andReturn();
    }


    @SneakyThrows
    @Test
    @Order(4)
    @DisplayName("当第三方支付系统服务正常,调用支付接口的时候返回超时,返回状态码200,支付状态:PENDING,消息文本:支付中,请稍后查询支付状态")
    void should_get_status_200_and_pending_when_call_payment() {
        // given
        var paymentInfo = PaymentInfo.builder().amount(BigDecimal.ONE).build();
        when(paymentService.pay(argThat(request -> request.getContractId().equalsIgnoreCase("C-0000000001"))))
                .thenReturn(CommonResult.builder().code(PENDING.name()).message("支付中,请稍后查询支付状态")
                        .build());

        // when & then
        final MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/rent-contracts/C-0000000001/rental-payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(paymentInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code",is("PENDING")))
                .andExpect(jsonPath("$.message",is("支付中,请稍后查询支付状态")))
                .andReturn();
    }

}