package com.yang.admin.listener;

import com.alibaba.excel.context.AnalysisContext;

import com.alibaba.excel.event.AnalysisEventListener;
import com.imooc.utils.DateUtil;
import com.yang.admin.PerInfoServiceImpl;
import com.yang.admin.mapper.PatientsMapper;
import com.yang.admin.mapper.PatientstatusMapper;
import com.yang.admin.mapper.PlaceMapper;
import com.yang.enums.LocalOrOver;
import com.yang.pojo.PlaceLocation;
import com.yang.pojo.VO.Community;
import com.yang.pojo.VO.Patients;
import com.yang.pojo.VO.Patientstatus;
import com.yang.pojo.VO.Placelocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientsDataListener extends AnalysisEventListener<Patients> {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PatientsDataListener.class);

    private PatientsMapper patientsMapper;

    private PerInfoServiceImpl perInfoService;

    private PlaceMapper placeMapper;

    private PatientstatusMapper patientstatusMapper;

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

    private SimpleDateFormat simpleDateFormat;
    public PatientsDataListener(PatientsMapper patientsMapper, PerInfoServiceImpl perInfoService, PlaceMapper placeMapper,PatientstatusMapper patientstatusMapper) {
        this.patientsMapper = patientsMapper;
        this.perInfoService = perInfoService;
        this.placeMapper = placeMapper;
        this.patientstatusMapper = patientstatusMapper;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<Patients> list = new ArrayList<>();


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Patients data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", com.alibaba.fastjson.JSON.toJSONString(data));
        //Date currentTime = DateUtil.stringToDate(simpleDateFormat.format(System.currentTimeMillis()));
        //data.setUpdateTime(currentTime);
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        PlaceLocation placelocation = perInfoService.getLocationFromAddress(data.getPlace());
        saveData2(data,placelocation);
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
    private void saveData(Patients data, PlaceLocation placeLocation) {
        // 最好改成批插入
        //检查是否有重复插入
        Example patientExample = new Example(Patients.class);
        Example.Criteria criteria = patientExample.createCriteria();
        criteria.andEqualTo("realname",data.getRealname());
        if(patientsMapper.selectOneByExample(patientExample) != null)
        {
            patientsMapper.updateByExampleSelective(data,patientExample);
        }else {
            patientsMapper.insert(data);
            Placelocation location2DB = new Placelocation();
            location2DB.setAddress(data.getPlace());
            location2DB.setLng(String.valueOf(placeLocation.getResult().getLocation().getLng()));
            location2DB.setLat(String.valueOf(placeLocation.getResult().getLocation().getLat()));
            placeMapper.insert(location2DB);
            LOGGER.info("{}条数据，开始存储数据库！", list.size());
            LOGGER.info("存储数据库成功！");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveData2(Patients data, PlaceLocation placeLocation) {
        // 最好改成批插入
        //检查是否有重复插入
        //解决地址重复插入的问题
        allData++;
        Example patientExample = new Example(Patients.class);
        Example.Criteria criteria = patientExample.createCriteria();
        criteria.andEqualTo(data);
        if(patientsMapper.selectOneByExample(patientExample) != null)
        {
            //不插入相同数据
            LOGGER.info("{}——数据相同", data);
            return;
        }else {
            patientsMapper.insert(data);
            Placelocation location2DB = new Placelocation();
            Example placeExample = new Example(Placelocation.class);
            Example.Criteria placeCriteria = placeExample.createCriteria();
            placeCriteria.andEqualTo("address",data.getPlace());
            if(placeMapper.selectOneByExample(placeExample) != null)
            {
                //不插入相同地址
                LOGGER.info("{}——已有地址",location2DB.getAddress());
            }else {

                location2DB.setAddress(data.getPlace());
                location2DB.setLng(String.valueOf(placeLocation.getResult().getLocation().getLng()));
                location2DB.setLat(String.valueOf(placeLocation.getResult().getLocation().getLat()));
                //插入地址
                placeMapper.insert(location2DB);
            }

            //插入患者状态
            //判断省份证号是否存在,保证患者状态表中一个人一个数据
            //如果检测到状态值改变则更新
            Example statusExample = new Example(Patientstatus.class);
            Example.Criteria statusCriteria = statusExample.createCriteria();
            statusCriteria.andEqualTo("idNumber",data.getIdNumber());
            //statusCriteria.andEqualTo("province",getProvince(data.getPlace()));
            Patientstatus patientstatus = patientstatusMapper.selectOneByExample(statusExample);
            if(patientstatus != null)
            {
                //如果检测到已经有患者状态在状态表中，则需要更新患者的将健康状态
                patientstatus.setHealth(data.getHealth());
                int i = patientstatusMapper.updateByExampleSelective(patientstatus, statusExample);
                if(i>0) LOGGER.info("患者状态表更新成功,更新数据{}",patientstatus);

            }else {
                //患者状态表没有该患者的信息,插入数据
                patientstatus = new Patientstatus();
                patientstatus.setIdNumber(data.getIdNumber());
                patientstatus.setProvince(getProvince(data.getPlace()));
                patientstatus.setHealth(data.getHealth());
                patientstatus.setPatientType(LocalOrOver.Local.type);
                int insert = patientstatusMapper.insert(patientstatus);
                if(insert>0) LOGGER.info("患者状态表更新成功,更新数据为{}",patientstatus);
            }

            dataNum++;
            //LOGGER.info("{}条数据，开始存储数据库！", list.size());
            LOGGER.info("存储数据库成功！");
        }

    }

    /**
     * 获取某个地址的省份名
     * @param city
     * @return
     */
    private String getProvince(String city)
    {
        int index = city.indexOf("省");
        String province = city.substring(0,index+1);
        return province;
    }
}