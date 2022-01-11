package com.yang.test.Controller;

import com.alibaba.excel.EasyExcel;
import com.yang.test.BO.DemoDataListener;
import com.yang.test.BO.Order;
import com.yang.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping("/test")
public class ImportController {

    @Autowired
    public TestService testService;

    @PostMapping(value = "/upload")
    @ResponseBody
    public String upload(@RequestPart("file") MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), Order.class, new DemoDataListener()).sheet().doRead();
        return "success";
    }

    @PostMapping("upload2")
    @ResponseBody
    public String upload2(@RequestParam("Name") String Name,Integer ID,String illness,String Unit) throws IOException {
        //EasyExcel.read(file.getInputStream(), Order.class, new DemoDataListener()).sheet().doRead();
        System.out.println(Name+" "+ID+" "+illness+" "+Unit);
        return "success";
    }

    @PostMapping("/test")
    public void test()
    {
        testService.test();
    }

}