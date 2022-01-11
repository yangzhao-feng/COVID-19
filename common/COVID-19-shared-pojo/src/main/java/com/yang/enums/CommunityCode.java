package com.yang.enums;

public enum CommunityCode {

    Hight(3, "治愈"),
    Medium(2, "确诊"),
    Low(1, "死亡"),
    Zero(0,"健康");

    public final Integer type;
    public final String value;

    CommunityCode(Integer type, String value){
        this.type = type;
        this.value = value;
    }

}
