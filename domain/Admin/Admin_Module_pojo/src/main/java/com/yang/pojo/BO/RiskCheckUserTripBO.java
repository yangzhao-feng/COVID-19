package com.yang.pojo.BO;

import lombok.Data;

import java.util.Date;

@Data
public class RiskCheckUserTripBO {

    private String username;
    private String starttime;
    private String stoptime;
    private String place;
    private int timeIndex;

}
