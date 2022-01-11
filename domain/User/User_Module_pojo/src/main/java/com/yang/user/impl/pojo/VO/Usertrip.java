package com.yang.user.impl.pojo.VO;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

public class Usertrip {

    /**
     * 用户名
     */
    private String username;

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
     * 地点
     */
    private String place;

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
     * 获取用户名
     *
     * @return username - 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
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
     * 获取地点
     *
     * @return place - 地点
     */
    public String getPlace() {
        return place;
    }

    /**
     * 设置地点
     *
     * @param place 地点
     */
    public void setPlace(String place) {
        this.place = place;
    }
}