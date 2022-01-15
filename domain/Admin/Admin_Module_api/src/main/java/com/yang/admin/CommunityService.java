package com.yang.admin;

import com.yang.pojo.BO.CommunityBO;
import com.yang.pojo.BO.CommunityBO2;
import com.yang.pojo.BO.PatientsBO;
import com.yang.pojo.VO.Community;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityService {

    /**
     * 批量上传中高风险地区   上传的地区不能重名
     * @param file
     * @return
     */
    public boolean comUploadByExcel(MultipartFile file);

    /**
     * 本地案例单个上传接口   上传的地区不能重名
     * @param communityBO
     * @return
     */
    public boolean comUpload(CommunityBO communityBO);

    /**
     * 通过市级地区获取风险小区信息
     * @param address
     * @return
     */
    public List<CommunityBO> getComInfoByAdd(String address);

    /**
     *通过市级地区获取风险小区信息,version-2
     * @param address
     * @return
     */
    public List<CommunityBO2> getComInfoByAdd2(String address);


    /**
     * 根据地址获取用户当前所在地址的风险情况 0:无风险 1:低风险 2:中风险 3:高风险
     * @param address
     * @return
     */
    public Integer getAddressRisk(String address);


}
