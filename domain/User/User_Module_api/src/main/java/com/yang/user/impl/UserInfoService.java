package com.yang.user.impl;

import com.yang.user.impl.pojo.BO.UserBO;
import com.yang.user.impl.pojo.BO.UserTripBO;
import com.yang.user.impl.pojo.BO.UserTripBO2;
import com.yang.user.impl.pojo.VO.UserInformation;
import com.yang.user.impl.pojo.VO.Usertrip;
import com.yang.pojo.PlaceLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserInfoService {

    public UserInformation getUserInfo(String email);

    /**
     * 获取用户历史轨迹
     * @param userBO
     * @return
     */
    public List<UserTripBO> userTrip(UserBO userBO);

    /**
     * 获取本市的用户轨迹
     * @param username
     * @param address
     * @return
     */
    public List<UserTripBO2> userTrip2(String username, String address);

    /**
     * 获取本市的用户轨迹,与userTrip2相比,日期格式不同,日期格式为YYYY-MM-dd HH:mm:ss
     * @param username
     * @param address
     * @return
     */
    public List<UserTripBO2> userTrip3(String username, String address);

    /**
     * 批量导入用户的轨迹
     * @param file
     * @return
     */
    public boolean userTripBatchInsert(MultipartFile file) throws IOException;

    /**
     * 插入单个用户轨迹
     * @param usertrip
     * @return
     * @throws IOException
     */
    public boolean userTripInsert(Usertrip usertrip) throws IOException;

    /**
     * 根据地址获取百度的定位
     * @param Address
     * @return
     */
    public PlaceLocation getLocationFromAddress(String Address);

    /**
     * 将用户信息更新到数据表中
     * @param userInformation
     * @return
     */
    public boolean inserUserInfo(UserInformation userInformation);

    /**
     * 插入地点坐标
     * @param address
     * @return
     */
    public boolean insertPlaceLocation(String address);

}
