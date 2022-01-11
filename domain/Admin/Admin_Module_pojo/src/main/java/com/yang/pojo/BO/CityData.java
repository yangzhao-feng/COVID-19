package com.yang.pojo.BO;

import lombok.Data;

@Data
public class CityData {

    private String province;//省份名
    private Integer confirm;//确诊人数
    private Integer cure;//治愈人数
    private Integer death;//死亡人数


    public CityData() {
        this.confirm = 0;
        this.cure = 0;
        this.death = 0;
    }

    public void autoAddConfirm()
    {
        confirm++;
    }

    public void autoAddCure()
    {
        cure++;
    }

    public void autoAdddeath()
    {
        death++;
    }

}
