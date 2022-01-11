package com.yang.enums;

/**
 * @Desc: 是否 枚举
 */
public enum CityRiskEnum {
    HavePatient(0, "有境外患者确诊"),
    Other(1, "无境外确诊患者，但有从此处出发的在别地确诊的患者");

    public final Integer type;
    public final String value;

    CityRiskEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
