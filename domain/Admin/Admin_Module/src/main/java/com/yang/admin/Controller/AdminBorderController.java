package com.yang.admin.Controller;

import com.yang.admin.BorderInfoService;
import com.yang.admin.listener.BorderDataListener;
import com.yang.pojo.BO.BorderInfoBO;
import com.yang.pojo.BO.BorderpatientsBO;
import com.yang.pojo.BO.CityRiskBO;
import com.yang.pojo.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("border")
public class AdminBorderController {

    @Autowired
    private BorderInfoService borderInfoService;

    @PostMapping("/uploadByExcel")
    public IMOOCJSONResult uploadByExcel(@RequestPart("file") MultipartFile file)
    {
        if (borderInfoService.uploadByExcel(file))
        {
            Integer realUpload = BorderDataListener.getDataNum();
            Integer Upload = BorderDataListener.getAllNum();
            return IMOOCJSONResult.ok("解析到"+Upload+"条数据,成功上传"+realUpload+"条数据,有"+(Upload-realUpload)+"条数据重复");
        }
        else {
            return IMOOCJSONResult.errorMsg("插入失败,请重试");
        }
    }

    @PostMapping("/upload")
    public IMOOCJSONResult upload(@RequestBody BorderpatientsBO borderpatientsBO)
    {
        if (borderInfoService.upload(borderpatientsBO))
        {
            return IMOOCJSONResult.ok("插入成功");
        }
        else {
            return IMOOCJSONResult.errorMsg("插入失败,请重试");
        }
    }

    @GetMapping("/borderInfo")
    public IMOOCJSONResult getBorderInfo(@RequestParam String city)
    {
        //获取所有境外输入的疫情信息
        //返回一个List,将航班信息拆分,返回给前端
        List<BorderInfoBO> borderInfoBOS = borderInfoService.borderInfo(city);

        return IMOOCJSONResult.ok(borderInfoBOS);
    }

    @GetMapping("/targetCityPatient")
    public IMOOCJSONResult targetCityPatient(@RequestParam String city)
    {
        //获取所有境外输入的疫情信息
        //返回一个List,将航班信息拆分,返回给前端
        List<BorderInfoBO> borderInfoBOS = borderInfoService.targetCityPatient(city);

        return IMOOCJSONResult.ok(borderInfoBOS);
    }

    @GetMapping("/cityRisk")
    public IMOOCJSONResult cityRisk()
    {
        //获取所有境外输入的疫情信息
        //返回一个List,将航班信息拆分,返回给前端
        List<CityRiskBO> cityRiskBOList = borderInfoService.cityRisk();

        return IMOOCJSONResult.ok(cityRiskBOList);
    }
}
