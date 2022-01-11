package com.yang.pojo.BO;

import lombok.Data;

@Data
public class HomeInfoBO {

    private int T_confirmNumber;//今天的确诊人数
    private int Y_confirmNumber;//昨天的确诊人数
    private int T_Y_confirm;//今天减去昨天的确诊数

    private int T_cureNumber;//今天的确诊人数
    private int Y_cureNumber;//昨天的确诊人数
    private int T_Y_cure;//今天减去昨天的治愈数

    private int T_deathNumber;//今天的确诊人数
    private int Y_deathNumber;//昨天的确诊人数
    private int T_Y_death;//今天减去昨天的死亡数

    private int all_confirm;//全部的确诊人数
    private int all_cure;//全部的治愈人数
    private int all_death;//全部的死亡人数

}
