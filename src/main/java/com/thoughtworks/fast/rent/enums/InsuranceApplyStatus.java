package com.thoughtworks.fast.rent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InsuranceApplyStatus {
    SENDING("正在通知保险公司,请稍后查询出现状态"),
    SEND("已发送出现请求至保险公司,待保险公司确认"),
    CONFIRMED("保险公司已确认,正在出险中");

    private final String message;
}
