package com.yang.pojo.BO;

import lombok.Data;

@Data
public class BorderInfoBO {

    //真实姓名
    private String realName;
    //开始城市
    private String startCity;
    //目的城市
    private String endCity;
    //航班号/地铁号
    private String number;
    //航班号标识1,高铁号标识2
    private Integer flag;
    private  String startLat;
    private String startLng;
    private String endLat;
    private String endLng;
}
