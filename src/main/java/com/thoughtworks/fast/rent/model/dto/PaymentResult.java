package com.thoughtworks.fast.rent.model.dto;

import com.thoughtworks.fast.rent.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResult {

    private PaymentStatus paymentStatus;

    private String message;
}
