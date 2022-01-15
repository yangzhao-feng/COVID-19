package com.yang.user.Controller;

import com.imooc.utils.HttpUtil;
import com.yang.admin.CommunityService;
import com.yang.admin.PerInfoService;
import com.yang.user.impl.pojo.BO.UserTripBO;
import com.yang.user.impl.pojo.BO.UserTripBO2;
import com.yang.user.impl.pojo.VO.UserInformation;
import com.yang.user.impl.pojo.VO.Usertrip;
import com.yang.pojo.IMOOCJSONResult;
import com.yang.user.impl.UserInfoService;
import com.yang.user.impl.pojo.BO.UserBO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PerInfoService perInfoService;

    @Autowired
    private CommunityService communityService;

    private static final String LocateUrl = "https://api.map.baidu.com/location/ip?";


    /**
     * 文件批量上传接口
     * @param file
     * @return
     */
    @PostMapping(value = "/uploadByExcel")
    public IMOOCJSONResult upload(@RequestPart("file") MultipartFile file) {

        try {
            if(file == null)
            {
                return IMOOCJSONResult.errorMsg("文件不能为空");
            }

            if(userInfoService.userTripBatchInsert(file))
            {
                return IMOOCJSONResult.ok("添加成功");
            }
            return IMOOCJSONResult.errorMsg("添加失败,请重新上传");
        }catch (Exception e){
            e.printStackTrace();
            return IMOOCJSONResult.errorMsg(e.getMessage());
        }

    }

    /**
     * 单个上传接口
     * @param username
     * @param orignTime
     * @param destinationTime
     * @param place
     * @return
     */
    @PostMapping("/upload2")
    public IMOOCJSONResult upload2(@RequestParam("username") String username, @RequestParam("orignTime") String orignTime, @RequestParam("destinationTime")String destinationTime,
                          @RequestParam("place") String place ){

        if(username==""||orignTime==""||destinationTime==""||place=="")
        {
            return IMOOCJSONResult.errorMsg("信息不能为空");
        }

        try {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = ft.parse(orignTime);
            Date stopTime = ft.parse(destinationTime);

            Usertrip usertrip = new Usertrip();
            usertrip.setUsername(username);
            usertrip.setStarttime(startTime);
            usertrip.setStoptime(stopTime);
            usertrip.setPlace(place);

            userInfoService.userTripInsert(usertrip);

        }catch (Exception e){e.printStackTrace(); return IMOOCJSONResult.errorMsg(e.getMessage());}



        return IMOOCJSONResult.ok();
    }

    /**
     * 上传单个信息
     * @param userTripBO
     * @return
     */
    @PostMapping("/upload3")
    public IMOOCJSONResult upload3(@RequestBody UserTripBO userTripBO){

        if(userTripBO.getUsername()==""||userTripBO.getStarttime()==null||userTripBO.getStoptime()==null||userTripBO.getPlace()=="")
        {
            return IMOOCJSONResult.errorMsg("信息不能为空");
        }

        try {
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = ft.parse(userTripBO.getStarttime().toString());
            Date stopTime = ft.parse(userTripBO.getStoptime().toString());

            Usertrip usertrip = new Usertrip();
            usertrip.setUsername(userTripBO.getUsername());
            usertrip.setStarttime(startTime);
            usertrip.setStoptime(stopTime);
            usertrip.setPlace(userTripBO.getPlace());

            userInfoService.userTripInsert(usertrip);

        }catch (Exception e){e.printStackTrace(); return IMOOCJSONResult.errorMsg(e.getMessage());}



        return IMOOCJSONResult.ok();
    }

    /**
     * 轨迹查询接口
     * @param username
     * @return
     */
    @GetMapping("/tripInfo")
    public IMOOCJSONResult tripInfo(@RequestParam String username)
    {
        if(StringUtils.isEmpty(username))
        {
            return IMOOCJSONResult.errorMsg("查询信息有误");
        }
        UserBO userBO = new UserBO();
        userBO.setUsername(username);
        List<UserTripBO> usertripList = userInfoService.userTrip(userBO);

        return IMOOCJSONResult.ok(usertripList);

    }

    /**
     * 更新当前住址接口
     * @param userBO
     * @param Address
     * @return
     */
    @PostMapping("/uploadAddress")
    public IMOOCJSONResult uploadAddress(@RequestBody UserBO userBO,@RequestParam("Address")String Address)
    {
        if(StringUtils.isEmpty(userBO.getEmail()) || StringUtils.isEmpty(userBO.getPassword()) ||
        StringUtils.isEmpty(Address))
        {
            return IMOOCJSONResult.errorMsg("上传信息有误");
        }

        // 从管理员上传的表中查询,如果没有信息则安全
        Integer health = perInfoService.getPatientHealth(userBO.getRealname());

        // 根据Address获取所在地址的坐标存入Cookie中
        Integer addressRisk = communityService.getAddressRisk(Address);

        // TODO 查询到的坐标保存到数据库和redis
        userInfoService.insertPlaceLocation(Address);

        UserInformation userInformation = new UserInformation();
        userInformation.setAddress(Address);
        userInformation.setHealth(health);
        userInformation.setEmail(userBO.getEmail());
        userInformation.setRiskAreas(addressRisk);

        //更新用户信息
        userInfoService.inserUserInfo(userInformation);

        return IMOOCJSONResult.ok(userInformation);

    }

    @GetMapping("/Information")
    public IMOOCJSONResult getInformation(@RequestParam String email)
    {
        UserInformation userInfo = userInfoService.getUserInfo(email);

       return IMOOCJSONResult.ok(userInfo);
    }

    /**
     * 轨迹查询接口
     * @param username
     * @return
     */
    @GetMapping("/tripInfo2")
    public IMOOCJSONResult tripInfo(@RequestParam String username,@RequestParam String address)
    {
        if(StringUtils.isEmpty(username))
        {
            return IMOOCJSONResult.errorMsg("查询信息有误");
        }
        List<UserTripBO2> usertripList = userInfoService.userTrip2(username,address);

        return IMOOCJSONResult.ok(usertripList);

    }

}
