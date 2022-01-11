package com.yang.admin.Controller;

import com.netflix.discovery.converters.Auto;
import com.yang.admin.CommunityService;
import com.yang.pojo.BO.CommunityBO;
import com.yang.pojo.BO.CommunityBO2;
import com.yang.pojo.BO.PatientsBO;
import com.yang.pojo.IMOOCJSONResult;
import com.yang.pojo.VO.Community;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/community")
public class AdminCommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping("/uploadByExcel")
    public IMOOCJSONResult upload(@RequestPart("file") MultipartFile file)
    {
        try {
            if(file == null)
            {
                return IMOOCJSONResult.errorMsg("文件不能为空");
            }

            if(communityService.comUploadByExcel(file))
            {
                return IMOOCJSONResult.ok("添加成功");
            }
            return IMOOCJSONResult.errorMsg("添加失败,请重新上传");
        }catch (Exception e){
            e.printStackTrace();
            return IMOOCJSONResult.errorMsg(e.getMessage());
        }
    }

    @PostMapping("/Upload")
    public IMOOCJSONResult PersonUpload(@RequestBody CommunityBO communityBO){
        if (StringUtils.isEmpty(communityBO.getPlace())||StringUtils.isEmpty(communityBO.getUnit())||
                communityBO.getRiskLevel()>2||communityBO.getRiskLevel()<0)
        {
            return IMOOCJSONResult.errorMsg("信息不能为空或信息有误");
        }

        if(communityService.comUpload(communityBO))
        {
            return IMOOCJSONResult.ok("插入成功");
        }

        return IMOOCJSONResult.errorMsg("插入失败,请重试");

    }

    @GetMapping("/ComInfoByAdd")
    public IMOOCJSONResult getComInfo(@RequestParam String address)
    {
        if(StringUtils.isEmpty(address))
        {
            return IMOOCJSONResult.errorMsg("参数有误");
        }

        List<CommunityBO> comInfoByAdd = communityService.getComInfoByAdd(address);

        return IMOOCJSONResult.ok(comInfoByAdd);
    }

    @GetMapping("ComInfoByAdd2")
    public IMOOCJSONResult getComInfo2(@RequestParam String address)
    {
        if(StringUtils.isEmpty(address))
        {
            return IMOOCJSONResult.errorMsg("参数有误");
        }

        List<CommunityBO2> comInfoByAdd = communityService.getComInfoByAdd2(address);

        return IMOOCJSONResult.ok(comInfoByAdd);
    }


}
