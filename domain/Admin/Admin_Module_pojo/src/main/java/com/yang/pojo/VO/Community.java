package com.yang.pojo.VO;



import javax.persistence.Column;
import javax.persistence.Id;

public class Community {

    private String place;

    @Column(name = "risk_level")
    private Integer riskLevel;

    @Column(name = "Unit")
    private String unit;

    @Id
    private Integer id;

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
     * @return place
     */
    public String getPlace() {
        return place;
    }

    /**
     * @param place
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * @return risk_level
     */
    public Integer getRiskLevel() {
        return riskLevel;
    }

    /**
     * @param riskLevel
     */
    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    /**
     * @return Unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}