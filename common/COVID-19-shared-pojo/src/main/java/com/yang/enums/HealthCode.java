package com.yang.enums;

public enum HealthCode {

    CURE(0, "治愈"),
    Confirmed(1, "确诊"),
    Death(2, "死亡"),
    Health(3,"健康");

    public final Integer type;
    public final String value;

    HealthCode(Integer type, String value){
        this.type = type;
        this.value = value;
    }

}
