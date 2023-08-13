package com.thoughtworks.fast.rent.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {

    public static final String PENDING_PAYMENT_TOPIC_NAME = "fast-rent-service.pending-payment";

    public static final String INSURANCE_APPLY_TOPIC_NAME = "fast-rent-service.insurance-apply";
}
