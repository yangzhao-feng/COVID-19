package com.yang.pojo.VO;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

public class Borderpatients {

    private String realname;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "city_list")
    private String cityList;

    @Column(name = "number_list")
    private String numberList;

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
     * @return city_list
     */
    public String getCityList() {
        return cityList;
    }

    /**
     * @param cityList
     */
    public void setCityList(String cityList) {
        this.cityList = cityList;
    }

    /**
     * @return number_list
     */
    public String getNumberList() {
        return numberList;
    }

    /**
     * @param numberList
     */
    public void setNumberList(String numberList) {
        this.numberList = numberList;
    }

    public List<String> getStartAndEnd_City()
    {
        List<String> citys = new ArrayList<>();
        String cityList = this.getCityList();
        if (!cityList.contains("-"))
        {
            return null;
        }

        String[] split = cityList.split("-");
        for(int i = 0;i<split.length;i++)
        {
            if(!split.equals(""))
            {
                citys.add(split[i]);
            }
        }
        return citys;
    }
}