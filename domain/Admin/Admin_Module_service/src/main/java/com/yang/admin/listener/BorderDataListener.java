package com.yang.admin.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yang.admin.BaseService;
import com.yang.admin.mapper.BorderpatientsMapper;
import com.yang.admin.mapper.BorderpatientstatusMapper;
import com.yang.admin.mapper.PatientstatusMapper;
import com.yang.admin.mapper.PlaceMapper;
import com.yang.enums.HealthCode;
import com.yang.enums.LocalOrOver;
import com.yang.pojo.PlaceLocation;
import com.yang.pojo.VO.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BorderDataListener extends AnalysisEventListener<Borderpatients> {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(BorderDataListener.class);

    private BorderpatientsMapper borderpatientsMapper;


    private PlaceMapper placeMapper;

    private BorderpatientstatusMapper borderpatientstatusMapper;

    public static Integer dataNum = 0;

    public static Integer allData = 0;

    public static Integer getDataNum()
    {
        Integer num = dataNum;
        //自动清零
        dataNum = 0;
        return num;
    }

    public static Integer getAllNum()
    {
        Integer num = allData;
        //自动清零
        allData = 0;
        return num;
    }

    public BorderDataListener(BorderpatientsMapper borderpatientsMapper
            , PlaceMapper placeMapper, BorderpatientstatusMapper borderpatientstatusMapper) {
        this.borderpatientsMapper = borderpatientsMapper;
        this.placeMapper = placeMapper;
        this.borderpatientstatusMapper = borderpatientstatusMapper;
    }

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<Borderpatients> list = new ArrayList<>();


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Borderpatients data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", com.alibaba.fastjson.JSON.toJSONString(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        String cityStr = data.getCityList();
        //解析城市
        List<String> cityList = splitStrToListLong(cityStr);
//        System.out.println(cityList);

        saveCityList(cityList);

        saveBorderPatient(data);
        if (list.size() >= BATCH_COUNT) {
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        LOGGER.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveBorderPatient(Borderpatients data) {
        // 最好改成批插入
        //检查是否有重复插入
        allData++;
        Example borderExample = new Example(Borderpatients.class);
        Example.Criteria criteria = borderExample.createCriteria();
        //criteria.andEqualTo(data);
        Borderpatients borderpatients = borderpatientsMapper.selectOne(data);
        if(borderpatients != null)
        {
            LOGGER.info("信息已存在 {}",borderpatients);
            return;
        }
        borderpatientsMapper.insert(data);

        //插入患者状态表
        Borderpatientstatus borderpatientstatus = new Borderpatientstatus();
        List<String> startAndEnd_city = data.getStartAndEnd_City();
        borderpatientstatus.setCity(startAndEnd_city.get(1));
        borderpatientstatus.setIdnumber(data.getIdNumber());
        //默认确诊
        borderpatientstatus.setHealth(HealthCode.Confirmed.type);
        //查询状态表中是否有该患者的信息
        Example statusExample = new Example(Patientstatus.class);
        Example.Criteria statusCriteris = statusExample.createCriteria();
        statusCriteris.andEqualTo("idNumber",borderpatientstatus.getIdnumber());
        Borderpatientstatus borderpatientstatus1 = borderpatientstatusMapper.selectOneByExample(statusExample);
        if(borderpatientstatus1!=null)
        {
            //覆盖信息
            borderpatientstatus.setId(borderpatientstatus1.getId());
            borderpatientstatusMapper.updateByPrimaryKeySelective(borderpatientstatus);
        }else {
            borderpatientstatusMapper.insert(borderpatientstatus);
        }

        dataNum++;
    }
    private void saveCityList(List<String> cityList)
    {
        cityList.forEach(city->{

            Example placeExample = new Example(Placelocation.class);
            Example.Criteria criteria = placeExample.createCriteria();

            criteria.andEqualTo("address",city);

            if(placeMapper.selectByExample(placeExample).size()<=0)
            {
                PlaceLocation locationFromAddress = BaseService.getLocationFromAddress(city);
                Placelocation location2DB = new Placelocation();
                location2DB.setAddress(city);
                location2DB.setLng(String.valueOf(locationFromAddress.getResult().getLocation().getLng()));
                location2DB.setLat(String.valueOf(locationFromAddress.getResult().getLocation().getLat()));
                placeMapper.insert(location2DB);
            }
        });
    }

    public List<String> splitStrToListLong(String str) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(str)) {
            String[] split = StringUtils.split(str, "-");
            for (String string : split) {
                list.add(string);
            }
        }
        return list;
    }
}