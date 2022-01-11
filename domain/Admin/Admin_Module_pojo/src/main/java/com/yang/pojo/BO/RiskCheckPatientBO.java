package com.yang.pojo.BO;

import lombok.Data;

@Data
public class RiskCheckPatientBO {

    private String realname;
    private String idNumber;
    private Integer health;
    private String starttime;
    private String stoptime;
    private String place;
    private String unit;
    private int timeIndex;
}