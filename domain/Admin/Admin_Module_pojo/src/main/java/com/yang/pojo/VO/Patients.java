package com.yang.pojo.VO;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

public class
Patients {

    private String realname;

    /**
     * 身份证号
     */
    @Column(name = "Id_number")
    private String idNumber;

    /**
     * 0:治愈 1:确诊 2:死亡
     */
    private Integer health;


    /**
     * 出发时间
     */
    @Column(name = "startTime")
    private Date starttime;

    /**
     * 离开时间
     */
    @Column(name = "stopTime")
    private Date stoptime;

    /**
     * 地方
     */
    private String place;

    /**
     * 负责单位
     */
    @Column(name = "Unit")
    private String unit;

    /**
     * 负责单位
     */
    @Column(name = "updateTime")
    private Date updateTime;

    @Id
    private Integer id;

    /**
     * @return id
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param id
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

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
     * @return realname
     */
    public String getRealname() {
        return realname;
    }

    /**
     * @param realname
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * 获取身份证号
     *
     * @return Id_number - 身份证号
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * 设置身份证号
     *
     * @param idNumber 身份证号
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * 获取0:治愈 1:确诊 2:死亡
     *
     * @return health - 0:治愈 1:确诊 2:死亡
     */
    public Integer getHealth() {
        return health;
    }

    /**
     * 设置0:治愈 1:确诊 2:死亡
     *
     * @param health 0:治愈 1:确诊 2:死亡
     */
    public void setHealth(Integer health) {
        this.health = health;
    }

    /**
     * 获取负责单位
     *
     * @return Unit - 负责单位
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置负责单位
     *
     * @param unit 负责单位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取出发时间
     *
     * @return startTime - 出发时间
     */
    public Date getStarttime() {
        return starttime;
    }

    /**
     * 设置出发时间
     *
     * @param starttime 出发时间
     */
    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    /**
     * 获取离开时间
     *
     * @return stopTime - 离开时间
     */
    public Date getStoptime() {
        return stoptime;
    }

    /**
     * 设置离开时间
     *
     * @param stoptime 离开时间
     */
    public void setStoptime(Date stoptime) {
        this.stoptime = stoptime;
    }

    /**
     * 获取地方
     *
     * @return place - 地方
     */
    public String getPlace() {
        return place;
    }

    /**
     * 设置地方
     *
     * @param place 地方
     */
    public void setPlace(String place) {
        this.place = place;
    }
}