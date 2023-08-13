package com.thoughtworks.fast.rent.facade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.fast.rent.FastRentServiceApplicationTests;
import com.thoughtworks.fast.rent.model.dto.insurance.InsuranceApply;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InsuranceControllerTest extends FastRentServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    @DisplayName("当用户发送申请出险请求,应该请求成功,并且返回Code = SENDING,消息为:正在通知保险公司,请稍后查询出现状态")
    void insuranceApply() {
        // given
        var insuranceApply = InsuranceApply.builder().vehicleId("V01").address("xx street").build();
        doNothing().when(insuranceService).apply(any());

        // when & then
        final MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/insurance-contracts/INSURANCE-0001/accident")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(insuranceApply)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code",is("SENDING")))
                .andExpect(jsonPath("$.message",is("正在通知保险公司,请稍后查询出现状态")))
                .andReturn();
    }
}