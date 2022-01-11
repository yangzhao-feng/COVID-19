package com.yang.admin;

import com.yang.pojo.BO.BorderInfoBO;
import com.yang.pojo.BO.BorderpatientsBO;
import com.yang.pojo.BO.CityRiskBO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BorderInfoService {

    /**
     * 通过Excel批量上传境外患者信息
     * @param file
     * @return
     */
    public boolean uploadByExcel(MultipartFile file);

    /**
     * 单个上传本地境外患者
     * @param borderpatientsBO
     * @return
     */
    public boolean upload(BorderpatientsBO borderpatientsBO);

    /**
     * 通过城市名获取所有经过这个城市的患者信息
     * @param City
     * @return
     */
    public List<BorderInfoBO> borderInfo(String City);

    /**
     * 获取在当前城市确诊的患者
     * @param City
     * @return
     */
    public List<BorderInfoBO> targetCityPatient(String City);

    /**
     * 获取所有的有境外确诊患者的城市 1:有确诊 2:标识无确诊但是有从该地出发
     * @return
     */
    public List<CityRiskBO> cityRisk();


}
