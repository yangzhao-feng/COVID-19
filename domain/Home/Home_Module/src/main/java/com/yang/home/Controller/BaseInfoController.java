package com.yang.home.Controller;

import com.yang.admin.PerInfoService;
import com.yang.pojo.BO.CityData;
import com.yang.pojo.BO.HomeInfoBO;
import com.yang.pojo.BO.MonthDataBO;
import com.yang.pojo.IMOOCJSONResult;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("home")
public class BaseInfoController {

    @Autowired
    private PerInfoService perInfoService;

    @GetMapping("/HomeInfo")
    public IMOOCJSONResult getPatientNumber()
    {
        HomeInfoBO homeInfoBO = perInfoService.getHomeInfo();
        return IMOOCJSONResult.ok(homeInfoBO);
    }

    @GetMapping("/MonthInfo")
    public IMOOCJSONResult getMonthData()
    {
        List<MonthDataBO> monthData = null;
        try {
            monthData = perInfoService.getMonthData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return IMOOCJSONResult.ok(monthData);
    }

    @GetMapping("/CityInfo")
    public IMOOCJSONResult getAllCityData()
    {
        List<CityData> allCityData = perInfoService.getAllCityData();
        return IMOOCJSONResult.ok(allCityData);
    }

    @GetMapping("/CityInfoByHealth")
    public IMOOCJSONResult getAllCityDataByHealth(@RequestParam("health") Integer healthCode)
    {
        List<CityData> allCityData = perInfoService.getCityDataByHealth(healthCode);
        return IMOOCJSONResult.ok(allCityData);
    }

    @GetMapping("/AllConfirm")
    public IMOOCJSONResult getAllConfirm()
    {
        int allconfirm = perInfoService.getAllconfirm();
        return IMOOCJSONResult.ok(allconfirm);
    }

}
