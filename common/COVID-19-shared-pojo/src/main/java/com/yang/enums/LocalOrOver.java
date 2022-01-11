package com.yang.enums;

public enum LocalOrOver {

    Local("0", "本地"),
    OVER_SEA("1", "境外");

    public final String type;
    public final String value;

    LocalOrOver(String type, String value) {
        this.type = type;
        this.value = value;
    }

}
