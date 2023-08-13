package com.thoughtworks.fast.rent.model.dto;

import lombok.Builder;
import lombok.Data;

import static com.thoughtworks.fast.rent.enums.InsuranceApplyStatus.SENDING;

@Data
@Builder
public class CommonResult {

    private String code;

    private String message;

    public static CommonResult defaultInstance(){
        return CommonResult.builder()
                .code(SENDING.name())
                .message(SENDING.getMessage())
                .build();
    }
}
