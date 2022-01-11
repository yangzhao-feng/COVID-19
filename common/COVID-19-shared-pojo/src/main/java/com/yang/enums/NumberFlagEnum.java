package com.yang.enums;

/**
 * @Desc: 是否 枚举
 */
public enum NumberFlagEnum {
    flight(1, "航班号"),
    train(2, "高铁号");

    public final Integer type;
    public final String value;

    NumberFlagEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
