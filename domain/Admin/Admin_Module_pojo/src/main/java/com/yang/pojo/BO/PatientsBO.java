package com.yang.pojo.BO;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class PatientsBO {

    private String realname;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 0:治愈 1:确诊 2:死亡
     */
    private Integer health;


    /**
     * 出发时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date starttime;

    /**
     * 离开时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date stoptime;

    /**
     * 地方
     */
    private String place;

    /**
     * 负责单位
     */
    private String unit;




}
