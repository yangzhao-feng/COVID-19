package com.yang.admin;

import com.yang.pojo.BO.*;
import com.yang.pojo.VO.Placelocation;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

public interface PerInfoService {

    /**
     * 本地案例批量上传接口   注意:上传的人不能重名
     * @param file
     * @return
     */
    public boolean perUploadByExcel(MultipartFile file);

    /**
     * 本地案例单个上传接口   注意:上传的人不能重名
     * @param patientsBO
     * @return
     */
    public boolean perUpload(PatientsBO patientsBO);

    /**
     * 根据HealthCode获取所有患者的轨迹
     * @param healthCode
     * @return
     */
    public List<PatientsBO> getInfoFromHealth(Integer healthCode);

    /**
     * 根据健康状态和地址获取患者信息
     * @param healthCode
     * @param address
     * @return
     */
    public List<PatientsBO> getInfoFromHealthandAddress(Integer healthCode,String address);

    /**
     * 根据健康状态和地址获取患者信息加上地址
     * @param healthCode
     * @param address
     * @return
     */
    public List<PatientsBO2> getInfoFromHealthandAddress2(Integer healthCode, String address);

    /**
     * 根据地址List来获取对应的坐标
     * @param address
     * @return
     */
    public List<Placelocation> getLocationFromList(List<String> address);

    /**
     * 根据真实姓名获取用户健康状态 0:治愈 1:确诊 2:死亡 3:健康
     * @param realname
     * @return
     */
    public Integer getPatientHealth(String realname);

    /**
     * 获取风险查询的信息
     * @param city
     * @param username
     * @return
     */
    public List<RiskCheckBO> getRiskCheckInfo(String city, String username);

    /**
     * 获取风险查询的信息,每个地区按出发时间排序
     * @param city
     * @param username
     * @return
     */
    public List<RiskCheckBO> getRiskCheckInfo2(String city, String username);

    /**
     * 获取当前确诊、治愈、死亡人数
     * @return
     */
    public int getNumber(int healthCode);

    /**
     * 根据healthCode获取确诊、死亡、治愈的人今天和和昨天的人数,以及两者的差
     * @return
     */
    public HomeInfoBO getHomeInfo();

    /**
     * 获取过去七个月的数据
     * @return
     */
    public List<MonthDataBO> getMonthData() throws ParseException;

    /**
     * 以省份单位获取各个身份为确诊人数,死亡人数,治愈人数
     * @return
     */
    public List<CityData> getAllCityData();

    /**
     * 以省份单位获取各个身份为确诊人数,死亡人数,治愈人数,带参
     * @return
     */
    public List<CityData> getCityDataByHealth(Integer healthCode);

    /**
     * 获取全部确诊人数
     * @return
     */
    public int getAllconfirm();

    /**
     * 获取境外的确诊、治愈、死亡数量
     * @return
     */
    public OverSeaInfoBO getAllOverSeaInfo();





}
