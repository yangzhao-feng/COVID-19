package com.yang.pojo.BO;

import com.yang.user.impl.pojo.BO.UserTripBO2;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解决多个患者到一个地点的问题
 * 以地址作为一条数据的标识,一个地址上有附带两条链表,并带有一个标识
 * 患者链表,用户链表,以一个是否为疫情小区的标识
 */
@Data
public class RiskCheckBO {

    String address;
    List<RiskCheckPatientBO> riskCheckPatientBOList;
    List<RiskCheckUserTripBO> riskCheckUserTripBOList;
    Boolean isRiskArea;
    Integer patientNumber;
    Integer UserTripNumber;
    String lng;
    String lat;

    public RiskCheckBO() {
        riskCheckPatientBOList = new ArrayList<>();
        riskCheckUserTripBOList = new ArrayList<>();
        patientNumber = 0;
        UserTripNumber = 0;
    }
}
