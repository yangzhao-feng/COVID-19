package com.yang.pojo.BO;

import lombok.Data;

@Data
public class OverSeaInfoBO {

    private Integer allConfirm;//所有境外的确诊数量
    private Integer allCure;//所有境外的治愈数量
    private Integer allDeath;//所欲境外的死亡数量

    public OverSeaInfoBO() {
        this.allConfirm = 0;
        this.allCure = 0;
        this.allDeath = 0;
    }

    public void autoAddConfirm()
    {
        allConfirm++;
    }

    public void autoAddCure()
    {
        allCure++;
    }

    public void autoAddDeath()
    {
        allDeath++;
    }

}
