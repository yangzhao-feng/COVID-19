package com.yang.pojo.BO;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;
@Data
public class PatientsBO2 {

    private String realname;
    private String idNumber;
    private Integer health;
    private String starttime;
    private String stoptime;
    private String place;
    private String unit;
    private String Lng;
    private String Lat;
}