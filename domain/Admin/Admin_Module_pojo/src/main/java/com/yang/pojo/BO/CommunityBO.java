package com.yang.pojo.BO;



import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
@Data
public class CommunityBO {

    private String place;

    @Column(name = "risk_level")
    private Integer riskLevel;

    @Column(name = "Unit")
    private String unit;

    @Id
    private Integer id;


}