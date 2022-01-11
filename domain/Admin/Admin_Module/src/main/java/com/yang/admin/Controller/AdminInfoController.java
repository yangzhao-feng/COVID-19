package com.yang.admin.Controller;

import com.yang.admin.PerInfoService;
import com.yang.admin.listener.PatientsDataListener;
import com.yang.enums.HealthCode;
import com.yang.pojo.BO.PatientCountBO;
import com.yang.pojo.BO.PatientsBO;
import com.yang.pojo.BO.PatientsBO2;
import com.yang.pojo.BO.RiskCheckBO;
import com.yang.pojo.IMOOCJSONResult;
import com.yang.pojo.VO.Placelocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("adminInfo")
public class AdminInfoController {

    @Autowired
    private PerInfoService perInfoService;

    @PostMapping("/PersonUploadByExcel")
    public IMOOCJSONResult PersonUploadByExcel(@RequestPart("file") MultipartFile file){
        try {
            if(file == null)
            {
                return IMOOCJSONResult.errorMsg("文件不能为空");
            }

            if(perInfoService.perUploadByExcel(file))
            {
                Integer realUpload = PatientsDataListener.getDataNum();
                Integer Upload = PatientsDataListener.getAllNum();
                return IMOOCJSONResult.ok("解析到"+Upload+"条数据,成功上传"+realUpload+"条数据,有"+(Upload-realUpload)+"条数据重复");
            }
            return IMOOCJSONResult.errorMsg("添加失败,原因可能是地址格式不正确,必须省市级的详细地址,或者信息不完整!");
        }catch (Exception e){
            e.printStackTrace();
            return IMOOCJSONResult.errorMsg(e.getMessage());
        }
    }

    @PostMapping("/PersonUpload")
    public IMOOCJSONResult PersonUpload(@RequestBody PatientsBO patientsBO){
        if (StringUtils.isEmpty(patientsBO.getRealname())||StringUtils.isEmpty(patientsBO.getUnit())||
                patientsBO.getHealth()>2||patientsBO.getHealth()<0||StringUtils.isEmpty(patientsBO.getPlace()))
        {
            return IMOOCJSONResult.errorMsg("信息不能为空或信息有误");
        }

        if(perInfoService.perUpload(patientsBO))
        {
            return IMOOCJSONResult.ok("插入成功");
        }

        return IMOOCJSONResult.errorMsg("插入失败,请重试");

    }

    @GetMapping("/InfoByHealth")
    public IMOOCJSONResult getInfoForPer(@RequestParam("healthStatus") Integer healthStatus)
    {

        if(healthStatus<0||healthStatus>2||healthStatus==null)
        {
            return IMOOCJSONResult.errorMsg("参数错误");
        }

        List<PatientsBO> infos = perInfoService.getInfoFromHealth(healthStatus);

        if(infos.size()<=0||infos==null)
        {
            return IMOOCJSONResult.ok("信息为空");
        }

        return IMOOCJSONResult.ok(infos);

    }

    @GetMapping("/InfoByHealthandAdd")
    public IMOOCJSONResult getInfoByHealthAndAdd(@RequestParam Integer healthStatus,@RequestParam("address") String addrss)
    {
        if(healthStatus<0||healthStatus>2||healthStatus==null||StringUtils.isEmpty(addrss))
        {
            return IMOOCJSONResult.errorMsg("参数错误");
        }

        List<PatientsBO> infoFromHealthandAddress = perInfoService.getInfoFromHealthandAddress(healthStatus, addrss);

        return IMOOCJSONResult.ok(infoFromHealthandAddress);

    }

    @GetMapping("/InfoByHealthandAdd2")
    public IMOOCJSONResult getInfoByHealthAndAdd2(@RequestParam Integer healthStatus,@RequestParam("address") String addrss)
    {
        if(healthStatus<0||healthStatus>2||healthStatus==null||StringUtils.isEmpty(addrss))
        {
            return IMOOCJSONResult.errorMsg("参数错误");
        }

        List<PatientsBO2> infoFromHealthandAddress = perInfoService.getInfoFromHealthandAddress2(healthStatus, addrss);

        return IMOOCJSONResult.ok(infoFromHealthandAddress);

    }

    @GetMapping("/placeLocation")
    public IMOOCJSONResult placeLocation(@RequestBody List<String> address) {

        if (address.size() <= 0 || address == null)
        {
            return IMOOCJSONResult.errorMsg("参数有误");
        }
        List<Placelocation> locationFromList = perInfoService.getLocationFromList(address);
        return IMOOCJSONResult.ok(locationFromList);
    }

    @GetMapping("/riskCheckInfo")
    public IMOOCJSONResult getRiskCheckInfo(@RequestParam String city,@RequestParam String username)
    {
        if(StringUtils.isEmpty(city))
        {
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }

        List<RiskCheckBO> riskCheckInfo = perInfoService.getRiskCheckInfo2(city, username);

        return IMOOCJSONResult.ok(riskCheckInfo);


    }

    @GetMapping("/patientNumber")
    public IMOOCJSONResult getPatientNumber()
    {
        int cureNumber = perInfoService.getNumber(HealthCode.CURE.type);
        int confirmNumber = perInfoService.getNumber(HealthCode.Confirmed.type);
        int deadNumber = perInfoService.getNumber(HealthCode.Death.type);
        PatientCountBO patientCountBO = new PatientCountBO();
        patientCountBO.setConfirm(confirmNumber);
        patientCountBO.setCure(cureNumber);
        patientCountBO.setDead(deadNumber);
        return IMOOCJSONResult.ok(patientCountBO);


    }


}
