package com.thoughtworks.fast.rent.model.dto.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResult {

    private String code;

    private String message;
}
