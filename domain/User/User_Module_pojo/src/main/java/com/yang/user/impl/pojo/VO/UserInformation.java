package com.yang.user.impl.pojo.VO;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "user_information")
public class UserInformation {
    @Id
    private Integer id;

    private String email;

    private String address;

    /**
     * 0:无风险
1:低风险
2:中风险
3:高风险
     */
    @Column(name = "risk_areas")
    private Integer riskAreas;

    /**
     * 0:健康 1:确诊 2:死亡
     */
    private Integer health;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取0:无风险
        1:低风险
        2:中风险
        3:高风险
     *
     * @return risk_areas - 0:无风险
                            1:低风险
                            2:中风险
                            3:高风险
     */
    public Integer getRiskAreas() {
        return riskAreas;
    }

    /**
     * 设置0:无风险
1:低风险
2:中风险
3:高风险
     *
     * @param riskAreas 0:无风险
1:低风险
2:中风险
3:高风险
     */
    public void setRiskAreas(Integer riskAreas) {
        this.riskAreas = riskAreas;
    }

    /**
     * 获取0:健康 1:确诊 2:死亡
     *
     * @return health - 0:健康 1:确诊 2:死亡
     */
    public Integer getHealth() {
        return health;
    }

    /**
     * 设置0:健康 1:确诊 2:死亡
     *
     * @param health 0:健康 1:确诊 2:死亡
     */
    public void setHealth(Integer health) {
        this.health = health;
    }
}