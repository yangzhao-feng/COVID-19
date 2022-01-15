package com.yang.pojo.VO;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

public class Patientstatus {
    @Id
    private Integer id;

    @Column(name = "idNumber")
    private String idNumber;

    private String province;

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
     * @return id_number
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * @param idNumber
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * @return province
     */
    public String getProvince() {
        return province;
    }

    /**
     * @param province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * @return health
     */
    public Integer getHealth() {
        return health;
    }

    /**
     * @param health
     */
    public void setHealth(Integer health) {
        this.health = health;
    }
}