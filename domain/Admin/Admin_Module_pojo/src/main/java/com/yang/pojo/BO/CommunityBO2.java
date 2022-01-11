package com.yang.pojo.BO;



import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class CommunityBO2 {

    private String place;
    private Integer riskLevel;
    private String unit;
    private Integer id;
    private String lng;
    private String lat;
}