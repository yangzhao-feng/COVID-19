package com.yang.admin;

import com.alibaba.excel.EasyExcel;
import com.yang.admin.listener.BorderDataListener;
import com.yang.admin.mapper.BorderpatientsMapper;
import com.yang.admin.mapper.PatientstatusMapper;
import com.yang.admin.mapper.PlaceMapper;
import com.yang.enums.CityRiskEnum;
import com.yang.enums.NumberFlagEnum;
import com.yang.pojo.BO.BorderInfoBO;
import com.yang.pojo.BO.BorderpatientsBO;
import com.yang.pojo.BO.CityRiskBO;
import com.yang.pojo.PlaceLocation;
import com.yang.pojo.VO.Borderpatients;
import com.yang.pojo.VO.Placelocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class BorderInfoServiceImpl extends BaseService implements BorderInfoService{

    @Autowired
    private BorderpatientsMapper borderpatientsMapper;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private PatientstatusMapper patientstatusMapper;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean uploadByExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), Borderpatients.class, new BorderDataListener(borderpatientsMapper, placeMapper,patientstatusMapper)).sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean upload(BorderpatientsBO borderpatientsBO) {

        Borderpatients borderpatients = new Borderpatients();
        BeanUtils.copyProperties(borderpatientsBO,borderpatients);

        int insert = borderpatientsMapper.insert(borderpatients);

        List<String> cityList = splitStrToListLong(borderpatients.getCityList());
        cityList.forEach(city->{

            Example placeExample = new Example(Placelocation.class);
            Example.Criteria criteria = placeExample.createCriteria();

            criteria.andEqualTo("address",city);

            if(placeMapper.selectByExample(placeExample).size()<=0)
            {
                PlaceLocation locationFromAddress = getLocationFromAddress(city);
                Placelocation location2DB = new Placelocation();
                location2DB.setAddress(city);
                location2DB.setLng(String.valueOf(locationFromAddress.getResult().getLocation().getLng()));
                location2DB.setLat(String.valueOf(locationFromAddress.getResult().getLocation().getLat()));
                placeMapper.insert(location2DB);
            }
        });

        if(insert>0)
        {
            return true;
        }

        return false;


    }

    @Override
    public List<BorderInfoBO> borderInfo(String City) {

        Example borderInfoBoExample = new Example(Borderpatients.class);
        Example.Criteria criteria = borderInfoBoExample.createCriteria();
        List<BorderInfoBO> borderpatientsBOList = new ArrayList<>();
        List<Borderpatients> borderpatientsList = new ArrayList<>();

        if(City.equals("*"))
        {
            borderpatientsList = borderpatientsMapper.selectAll();
        }else {

            criteria.andLike("cityList","%"+City+"%");

            borderpatientsList = borderpatientsMapper.selectByExample(borderInfoBoExample);
        }

        //根据获取到的信息做拆分
        borderpatientsList.forEach(item->{
            String cityList = item.getCityList();
            String numberList = item.getNumberList();

            String[] citys = cityList.split("-");
            String[] numbers = numberList.split("-");

            for(int i = 0; i<numbers.length;i++)
            {
                String number = numbers[i].substring(1);
                String flag = numbers[i].substring(0,1);
                BorderInfoBO borderInfoBO = new BorderInfoBO();
                borderInfoBO.setRealName(item.getRealname());
                borderInfoBO.setStartCity(citys[i]);
                Example placeExample = new Example(Placelocation.class);
                Example.Criteria criteria1 = placeExample.createCriteria();
                criteria1.andEqualTo("address",borderInfoBO.getStartCity());
                Placelocation placelocation = placeMapper.selectOneByExample(placeExample);
                borderInfoBO.setStartLat(placelocation.getLat());
                borderInfoBO.setStartLng(placelocation.getLng());
                borderInfoBO.setEndCity(citys[i+1]);
                Example placeExample2 = new Example(Placelocation.class);
                Example.Criteria criteria2 = placeExample2.createCriteria();
                criteria2.andEqualTo("address",borderInfoBO.getEndCity());
                placelocation = placeMapper.selectOneByExample(placeExample2);
                borderInfoBO.setEndLat(placelocation.getLat());
                borderInfoBO.setEndLng(placelocation.getLng());
                borderInfoBO.setNumber(number);
                if (flag.equals("1"))
                {
                    borderInfoBO.setFlag(NumberFlagEnum.flight.type);
                }else if(flag.equals("2"))
                {
                    borderInfoBO.setFlag(NumberFlagEnum.train.type);
                }
                borderpatientsBOList.add(borderInfoBO);
            }

        });

        return borderpatientsBOList;

    }

    @Override
    public List<BorderInfoBO> targetCityPatient(String City) {
        Example borderInfoBoExample = new Example(Borderpatients.class);
        Example.Criteria criteria = borderInfoBoExample.createCriteria();
        List<BorderInfoBO> borderpatientsBOList = new ArrayList<>();
        List<Borderpatients> borderpatientsList = new ArrayList<>();

        if(City.equals("*"))
        {
            borderpatientsList = borderpatientsMapper.selectAll();
        }else {

            criteria.andLike("cityList","%"+City+"%");

            borderpatientsList = borderpatientsMapper.selectByExample(borderInfoBoExample);
        }

        //根据获取到的信息做拆分
        borderpatientsList.forEach(item->{

            String cityList = item.getCityList();
            String numberList = item.getNumberList();

            String[] citys = cityList.split("-");
            String[] numbers = numberList.split("-");

            if(citys[citys.length-1].equals(City))
            {
                for(int i = 0; i<numbers.length;i++)
                {
                    String number = numbers[i].substring(1);
                    String flag = numbers[i].substring(0,1);
                    BorderInfoBO borderInfoBO = new BorderInfoBO();
                    borderInfoBO.setRealName(item.getRealname());
                    borderInfoBO.setStartCity(citys[i]);
                    Example placeExample = new Example(Placelocation.class);
                    Example.Criteria criteria1 = placeExample.createCriteria();
                    criteria1.andEqualTo("address",borderInfoBO.getStartCity());
                    Placelocation placelocation = placeMapper.selectOneByExample(placeExample);
                    borderInfoBO.setStartLat(placelocation.getLat());
                    borderInfoBO.setStartLng(placelocation.getLng());
                    borderInfoBO.setEndCity(citys[i+1]);
                    Example placeExample2 = new Example(Placelocation.class);
                    Example.Criteria criteria2 = placeExample2.createCriteria();
                    criteria2.andEqualTo("address",borderInfoBO.getEndCity());
                    placelocation = placeMapper.selectOneByExample(placeExample2);
                    borderInfoBO.setEndLat(placelocation.getLat());
                    borderInfoBO.setEndLng(placelocation.getLng());
                    borderInfoBO.setNumber(number);
                    if (flag.equals("1"))
                    {
                        borderInfoBO.setFlag(NumberFlagEnum.flight.type);
                    }else if(flag.equals("2"))
                    {
                        borderInfoBO.setFlag(NumberFlagEnum.train.type);
                    }
                    borderpatientsBOList.add(borderInfoBO);
                }

            }
        });

        return borderpatientsBOList;

    }

    @Override
    public List<CityRiskBO> cityRisk() {
        List<CityRiskBO> cityRiskBOList = new ArrayList<>();

        List<Borderpatients> borderpatients = borderpatientsMapper.selectAll();
        List<String> cityListRed = new ArrayList<>();
        List<String> cityListOrange = new ArrayList<>();

        borderpatients.forEach(item->{

            //获取本项的出发城市和到达城市
            String[] cityStr = item.getCityList().split("-");

            //如果是橙色等级的遇到红色需要被覆盖,如果是红色等级的遇到橙色等级不需要被覆盖
            for(int i = 0;i<cityStr.length;i++)
            {
                if(i<cityStr.length-1)
                {
                    //在本条信息中是橙色等级,检测当前红色等级列表中是否包括,如果包括则不需要再添加到橙色等级的列表
                    if(!cityListRed.contains(cityStr[i]))
                    {
                        if(!cityListOrange.contains(cityStr[i])) cityListOrange.add(cityStr[i]);
                    }
                }else {
                    //在本条信息中式红色等级的信息,检查橙色等级列表中是否包含此条信息,如果包含则在橙色等级信息中移除。
                    if(cityListOrange.contains(cityStr[i]))
                    {
                        cityListOrange.remove(cityStr[i]);
                    }
                    if(!cityListRed.contains(cityStr[i])) cityListRed.add(cityStr[i]);
                }
            }
        });

        cityListRed.forEach(red->{
            Example placeExample = new Example(Placelocation.class);
            Example.Criteria criteria = placeExample.createCriteria();
            criteria.andEqualTo("address",red);
            Placelocation placelocation = placeMapper.selectOneByExample(placeExample);
            CityRiskBO cityRiskBO = new CityRiskBO();
            cityRiskBO.setCity(red);
            cityRiskBO.setLng(placelocation.getLng());
            cityRiskBO.setLat(placelocation.getLat());
            cityRiskBO.setRisk(CityRiskEnum.HavePatient.type);
            cityRiskBOList.add(cityRiskBO);
        });

        cityListOrange.forEach(orange->{
            Example placeExample = new Example(Placelocation.class);
            Example.Criteria criteria = placeExample.createCriteria();
            criteria.andEqualTo("address",orange);
            Placelocation placelocation = placeMapper.selectOneByExample(placeExample);
            CityRiskBO cityRiskBO = new CityRiskBO();
            cityRiskBO.setCity(orange);
            cityRiskBO.setLng(placelocation.getLng());
            cityRiskBO.setLat(placelocation.getLat());
            cityRiskBO.setRisk(CityRiskEnum.Other.type);
            cityRiskBOList.add(cityRiskBO);
        });

        return cityRiskBOList;
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
