package com.thoughtworks.fast.rent.model.thirdparty.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.fast.rent.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static com.thoughtworks.fast.rent.enums.PaymentStatus.INSUFFICIENT_BALANCE;
import static com.thoughtworks.fast.rent.enums.PaymentStatus.SUCCESS;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private PaymentStatus paymentStatus;

    private String message;

    @JsonIgnore
    public boolean isSuccess(){
        return StringUtils.equalsIgnoreCase("SUCCESS", paymentStatus.toString());
    }


    public static PaymentResponse succeed(){
        return PaymentResponse.builder().paymentStatus(SUCCESS).message("支付成功").build();
    }

    public static PaymentResponse insufficientBalance(){
        return PaymentResponse.builder().paymentStatus(INSUFFICIENT_BALANCE).message("支付失败,账户余额不足").build();
    }

}
