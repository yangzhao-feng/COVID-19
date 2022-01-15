package com.yang.pojo.VO;

import javax.persistence.Column;
import javax.persistence.Id;

public class Borderpatientstatus {
    @Id
    private Integer id;

    @Column(name = "idNumber")
    private String idnumber;

    private String city;

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
     * @return idNumber
     */
    public String getIdnumber() {
        return idnumber;
    }

    /**
     * @param idnumber
     */
    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    /**
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     */
    public void setCity(String city) {
        this.city = city;
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