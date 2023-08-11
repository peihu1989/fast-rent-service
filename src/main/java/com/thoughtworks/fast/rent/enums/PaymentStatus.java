package com.thoughtworks.fast.rent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    SUCCESS,
    INSUFFICIENT_BALANCE,
    PENDING,
    FAILED;
}
