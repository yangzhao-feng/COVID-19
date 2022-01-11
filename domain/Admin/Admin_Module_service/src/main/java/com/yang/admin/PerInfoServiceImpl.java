package com.yang.admin;

import com.alibaba.excel.EasyExcel;
import com.imooc.utils.DateUtil;
import com.yang.admin.listener.PatientsDataListener;
import com.yang.admin.mapper.*;
import com.yang.enums.CommunityCode;
import com.yang.enums.HealthCode;
import com.yang.pojo.BO.*;
import com.yang.pojo.TimeCompareBO;
import com.yang.pojo.VO.Community;
import com.yang.pojo.VO.Patients;
import com.yang.pojo.VO.Patientstatus;
import com.yang.pojo.VO.Placelocation;

import com.yang.user.impl.UserInfoService;
import com.yang.user.impl.pojo.BO.UserTripBO2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PerInfoServiceImpl extends BaseService implements PerInfoService {

    @Autowired
    private PatientsMapper patientsMapper;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private PtientsMapperCustom patientsMapperCumstom;

    @Autowired
    private PatientstatusMapper patientstatusMapper;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean perUploadByExcel(MultipartFile file) {

        try {
            EasyExcel.read(file.getInputStream(), Patients.class, new PatientsDataListener(patientsMapper,this, placeMapper,patientstatusMapper)).sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean perUpload(PatientsBO patientsBO) {

        Patients patients = new Patients();
        BeanUtils.copyProperties(patientsBO,patients);
        // 把用户上传的地址的定位传入到数据库

        if(patientsMapper.insert(patients)>0)
        {
            return true;
        }

        return false;
    }

    @Override
    public List<PatientsBO> getInfoFromHealth(Integer healthCode) {

        Example patientExample = new Example(Patients.class);
        Example.Criteria criteria = patientExample.createCriteria();

        criteria.andEqualTo("health",healthCode);

        List<Patients> patientsList = patientsMapper.selectByExample(patientExample);

        List<PatientsBO> patientsBOList = new ArrayList<>();

        patientsList.forEach(patients -> {
            PatientsBO patientsBO = new PatientsBO();
            BeanUtils.copyProperties(patients,patientsBO);
            patientsBOList.add(patientsBO);
        });

        return patientsBOList;
    }

    @Override
    public List<PatientsBO> getInfoFromHealthandAddress(Integer healthCode, String address) {
        Example patientExample = new Example(Patients.class);
        Example.Criteria criteria = patientExample.createCriteria();

        criteria.andEqualTo("health",healthCode);
        criteria.andLike("place",address+"%");

        List<Patients> patientsList = patientsMapper.selectByExample(patientExample);

        List<PatientsBO> patientsBOList = new ArrayList<>();

        patientsList.forEach(patients -> {
            PatientsBO patientsBO = new PatientsBO();
            BeanUtils.copyProperties(patients,patientsBO);
            patientsBOList.add(patientsBO);
        });

        return patientsBOList;
    }

    @Override
    public List<PatientsBO2> getInfoFromHealthandAddress2(Integer healthCode, String address) {
        Example patientExample = new Example(Patients.class);
        Example.Criteria criteria = patientExample.createCriteria();

        criteria.andEqualTo("health",healthCode);
        criteria.andLike("place",address+"%");

        List<Patients> patientsList = patientsMapper.selectByExample(patientExample);

        List<PatientsBO2> patientsBOList = new ArrayList<>();

        patientsList.forEach(patient -> {
            PatientsBO2 patientsBO2 = new PatientsBO2();
            BeanUtils.copyProperties(patient,patientsBO2);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
            patientsBO2.setStarttime(sdf.format(patient.getStarttime()));
            patientsBO2.setStoptime(sdf.format(patient.getStoptime()));
            Example locationExample = new Example(Placelocation.class);
            Example.Criteria locationCriteria = locationExample.createCriteria();

            locationCriteria.andEqualTo("address",patient.getPlace());

            Placelocation placelocation = placeMapper.selectOneByExample(locationExample);

            patientsBO2.setLat(placelocation.getLat());
            patientsBO2.setLng(placelocation.getLng());


            patientsBOList.add(patientsBO2);
        });

        return patientsBOList;
    }

    @Override
    public List<Placelocation> getLocationFromList(List<String> address) {

        Example placeLocationExample = new Example(Placelocation.class);
        Example.Criteria criteria = placeLocationExample.createCriteria();

        criteria.andIn("address",address);
        List<Placelocation> list = placeMapper.selectByExample(placeLocationExample);

        return list;

    }

    @Override
    public Integer getPatientHealth(String realname) {
        Example patientExample = new Example(Patients.class);
        Example.Criteria criteria = patientExample.createCriteria();

        criteria.andEqualTo("realname",realname);
        Patients patients = patientsMapper.selectOneByExample(patientExample);

        if(patients == null)
        {
            return CommunityCode.Zero.type;
        }

        return patients.getHealth();

    }

    @Override
    public List<RiskCheckBO> getRiskCheckInfo(String city, String username) {

        List<RiskCheckBO> riskCheckBOList = new ArrayList<>();


        //查出用户查询的轨迹
        List<UserTripBO2> userTripBO2List = userInfoService.userTrip2(username, city);

        userTripBO2List.forEach(item->{
            //获取该链表项的地址
            String address = item.getPlace();

            RiskCheckUserTripBO riskCheckUserTripBO = new RiskCheckUserTripBO();
            BeanUtils.copyProperties(item,riskCheckUserTripBO);
            riskCheckUserTripBO.setStarttime(item.getStarttime());
            riskCheckUserTripBO.setStoptime(item.getStoptime());

            //如果链表中没有数据,或者链表中没有以此地址创造的对象,则需要新添加一个对象
            int index = checkAddress(riskCheckBOList,address);
            if(index < 0)
            {
                //往链表中新添加一个对象
                RiskCheckBO riskCheckBO = new RiskCheckBO();
                riskCheckBO.setAddress(address);
                List<RiskCheckUserTripBO> userTripBO2s = new ArrayList<>();
                userTripBO2s.add(riskCheckUserTripBO);
                riskCheckBO.setRiskCheckUserTripBOList(userTripBO2s);
                riskCheckBO.setLat(item.getLat());
                riskCheckBO.setLng(item.getLng());
                riskCheckBO.setUserTripNumber(userTripBO2s.size());
                riskCheckBOList.add(riskCheckBO);
            }else {
                //链表中有数据,只需取出该链表的数据,在此基础上进行信息的补充即可
                RiskCheckBO riskCheckBO = riskCheckBOList.get(index);
                List<RiskCheckUserTripBO> userTrips = riskCheckBO.getRiskCheckUserTripBOList();
                if(userTrips == null)
                {
                    userTrips = new ArrayList<>();
                }
                userTrips.add(riskCheckUserTripBO);
                //将修改后的对象放回到对应的对象中
                riskCheckBO.setRiskCheckUserTripBOList(userTrips);
                riskCheckBO.setUserTripNumber(userTrips.size());
                riskCheckBOList.set(index,riskCheckBO);
            }

        });

        //查出数据中该城市的患者
        Example patientExample = new Example(Patients.class);
        Example.Criteria patientCriteria = patientExample.createCriteria();
        patientCriteria.andLike("place","%"+city+"%");
        List<Patients> patientList = patientsMapper.selectByExample(patientExample);
        List<RiskCheckPatientBO> riskCheckPatientBOList = new ArrayList<>();
        patientList.forEach(item->{
            RiskCheckPatientBO riskCheckPatientBO = new RiskCheckPatientBO();
            BeanUtils.copyProperties(item,riskCheckPatientBO);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
            riskCheckPatientBO.setStarttime(sdf.format(item.getStarttime()));
            riskCheckPatientBO.setStoptime(sdf.format(item.getStoptime()));
            riskCheckPatientBOList.add(riskCheckPatientBO);
        });


        //对患者链表信息进行循环,将相同地址信息的患者放在同一个对象的List中
        riskCheckPatientBOList.forEach(item->{
            //获取该链表项的地址
            String address = item.getPlace();
            //如果链表中没有数据,或者链表中没有以此地址创造的对象,则需要新添加一个对象
            int index = checkAddress(riskCheckBOList,address);
            if(index < 0)
            {
                //往链表中新添加一个对象
                RiskCheckBO riskCheckBO = new RiskCheckBO();
                riskCheckBO.setAddress(address);
                List<RiskCheckPatientBO> patientsBO2s = new ArrayList<>();
                patientsBO2s.add(item);
                riskCheckBO.setRiskCheckPatientBOList(patientsBO2s);
                riskCheckBO.setPatientNumber(patientsBO2s.size());
                Example placeExample = new Example(Placelocation.class);
                Example.Criteria placeCritera = placeExample.createCriteria();
                placeCritera.andEqualTo("address",item.getPlace());
                Placelocation placelocation = placeMapper.selectOneByExample(placeExample);
                riskCheckBO.setLat(placelocation.getLat());
                riskCheckBO.setLng(placelocation.getLng());
                riskCheckBOList.add(riskCheckBO);
            }else {
                //链表中有数据,只需取出该链表的数据,在此基础上进行信息的补充即可
                RiskCheckBO riskCheckBO = riskCheckBOList.get(index);
                List<RiskCheckPatientBO> patients = riskCheckBO.getRiskCheckPatientBOList();
                patients.add(item);
                //将修改后的对象放回到对应的对象中
                riskCheckBO.setRiskCheckPatientBOList(patients);
                riskCheckBO.setPatientNumber(patients.size());
                riskCheckBOList.set(index,riskCheckBO);
            }


        });


        //是否为风险区域
        Example communityExample = new Example(Community.class);
        Example.Criteria communityCriteria = communityExample.createCriteria();
        communityCriteria.andLike("place",city);
        List<Community> communities = communityMapper.selectByExample(communityExample);
        riskCheckBOList.forEach(item->{
                String address = item.getAddress();
                for(int i = 0;i<communities.size();i++)
                {
                    if(communities.get(i).getPlace().equals(address))
                    {
                        item.setIsRiskArea(true);
                        break;
                    }
                }
                item.setIsRiskArea(false);
        });

        return riskCheckBOList;

    }

    @Override
    public List<RiskCheckBO> getRiskCheckInfo2(String city, String username) {
        List<RiskCheckBO> riskCheckBOList = new ArrayList<>();


        //查出用户查询的轨迹
        List<UserTripBO2> userTripBO2List = userInfoService.userTrip3(username, city);

        userTripBO2List.forEach(item->{
            //获取该链表项的地址
            String address = item.getPlace();

            RiskCheckUserTripBO riskCheckUserTripBO = new RiskCheckUserTripBO();
            BeanUtils.copyProperties(item,riskCheckUserTripBO);
            riskCheckUserTripBO.setStarttime(item.getStarttime());
            riskCheckUserTripBO.setStoptime(item.getStoptime());

            //如果链表中没有数据,或者链表中没有以此地址创造的对象,则需要新添加一个对象
            int index = checkAddress(riskCheckBOList,address);
            if(index < 0)
            {
                //往链表中新添加一个对象
                RiskCheckBO riskCheckBO = new RiskCheckBO();
                riskCheckBO.setAddress(address);
                List<RiskCheckUserTripBO> userTripBO2s = new ArrayList<>();
                userTripBO2s.add(riskCheckUserTripBO);
                riskCheckBO.setRiskCheckUserTripBOList(userTripBO2s);
                riskCheckBO.setLat(item.getLat());
                riskCheckBO.setLng(item.getLng());
                riskCheckBO.setUserTripNumber(userTripBO2s.size());
                riskCheckBOList.add(riskCheckBO);
            }else {
                //链表中有数据,只需取出该链表的数据,在此基础上进行信息的补充即可
                RiskCheckBO riskCheckBO = riskCheckBOList.get(index);
                List<RiskCheckUserTripBO> userTrips = riskCheckBO.getRiskCheckUserTripBOList();
                if(userTrips == null)
                {
                    userTrips = new ArrayList<>();
                }
                userTrips.add(riskCheckUserTripBO);
                //将修改后的对象放回到对应的对象中
                riskCheckBO.setRiskCheckUserTripBOList(userTrips);
                riskCheckBO.setUserTripNumber(userTrips.size());
                riskCheckBOList.set(index,riskCheckBO);
            }

        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //查出数据中该城市的患者
        Example patientExample = new Example(Patients.class);
        Example.Criteria patientCriteria = patientExample.createCriteria();
        patientCriteria.andLike("place","%"+city+"%");
        List<Patients> patientList = patientsMapper.selectByExample(patientExample);
        List<RiskCheckPatientBO> riskCheckPatientBOList = new ArrayList<>();
        patientList.forEach(item->{
            RiskCheckPatientBO riskCheckPatientBO = new RiskCheckPatientBO();
            BeanUtils.copyProperties(item,riskCheckPatientBO);
            riskCheckPatientBO.setStarttime(sdf.format(item.getStarttime()));
            riskCheckPatientBO.setStoptime(sdf.format(item.getStoptime()));
            riskCheckPatientBOList.add(riskCheckPatientBO);
        });


        //对患者链表信息进行循环,将相同地址信息的患者放在同一个对象的List中
        riskCheckPatientBOList.forEach(item->{
            //获取该链表项的地址
            String address = item.getPlace();
            //如果链表中没有数据,或者链表中没有以此地址创造的对象,则需要新添加一个对象
            int index = checkAddress(riskCheckBOList,address);
            if(index < 0)
            {
                //往链表中新添加一个对象
                RiskCheckBO riskCheckBO = new RiskCheckBO();
                riskCheckBO.setAddress(address);
                List<RiskCheckPatientBO> patientsBO2s = new ArrayList<>();
                patientsBO2s.add(item);
                riskCheckBO.setRiskCheckPatientBOList(patientsBO2s);
                riskCheckBO.setPatientNumber(patientsBO2s.size());
                Example placeExample = new Example(Placelocation.class);
                Example.Criteria placeCritera = placeExample.createCriteria();
                placeCritera.andEqualTo("address",item.getPlace());
                Placelocation placelocation = placeMapper.selectOneByExample(placeExample);
                riskCheckBO.setLat(placelocation.getLat());
                riskCheckBO.setLng(placelocation.getLng());
                riskCheckBOList.add(riskCheckBO);
            }else {
                //链表中有数据,只需取出该链表的数据,在此基础上进行信息的补充即可
                RiskCheckBO riskCheckBO = riskCheckBOList.get(index);
                List<RiskCheckPatientBO> patients = riskCheckBO.getRiskCheckPatientBOList();
                patients.add(item);
                //将修改后的对象放回到对应的对象中
                riskCheckBO.setRiskCheckPatientBOList(patients);
                riskCheckBO.setPatientNumber(patients.size());
                riskCheckBOList.set(index,riskCheckBO);
            }


        });


        //是否为风险区域
        Example communityExample = new Example(Community.class);
        Example.Criteria communityCriteria = communityExample.createCriteria();
        communityCriteria.andLike("place",city);
        List<Community> communities = communityMapper.selectByExample(communityExample);
        riskCheckBOList.forEach(item->{
            String address = item.getAddress();
            for(int i = 0;i<communities.size();i++)
            {
                if(communities.get(i).getPlace().equals(address))
                {
                    item.setIsRiskArea(true);
                    break;
                }
            }
            item.setIsRiskArea(false);
        });

        //按时间排序
        List<RiskCheckBO> riskCheckBOS = sortTimeFromRiskCheckBo(riskCheckBOList);


        return riskCheckBOS;
    }

    @Override
    public int getNumber(int healthCode) {
        int patientNumber = patientsMapperCumstom.getNumber(healthCode);
        return patientNumber;
    }

    @Override
    public HomeInfoBO getHomeInfo() {
        HomeInfoBO homeInfoBO = new HomeInfoBO();
        //获取确诊人数今天和昨天的数据
        int T_confirm = patientsMapperCumstom.getTodayNumber(HealthCode.Confirmed.type);
        int Y_confirm = patientsMapperCumstom.getYesNumber(HealthCode.Confirmed.type);

        //获取治愈人数今天和昨天的数据
        int T_cure = patientsMapperCumstom.getTodayNumber(HealthCode.CURE.type);
        int Y_cure = patientsMapperCumstom.getYesNumber(HealthCode.CURE.type);

        //获取死亡人数今天和昨天的数据
        int T_death = patientsMapperCumstom.getTodayNumber(HealthCode.Death.type);
        int Y_death = patientsMapperCumstom.getYesNumber(HealthCode.Death.type);

        Map<String,Object> map = new HashMap<>();
        map.put("healthCode",1);
        int all_confirm = patientsMapperCumstom.getInfoByCode(map);
        map.replace("healthCode",0);
        int all_cure = patientsMapperCumstom.getInfoByCode(map);
        map.replace("healthCode",2);
        int all_death = patientsMapperCumstom.getInfoByCode(map);

        homeInfoBO.setT_confirmNumber(T_confirm);
        homeInfoBO.setY_confirmNumber(Y_confirm);
        homeInfoBO.setT_cureNumber(T_cure);
        homeInfoBO.setY_cureNumber(Y_cure);
        homeInfoBO.setT_deathNumber(T_death);
        homeInfoBO.setY_deathNumber(Y_death);
        homeInfoBO.setT_Y_death(T_death-Y_death);
        homeInfoBO.setT_Y_confirm(T_confirm-Y_confirm);
        homeInfoBO.setT_Y_cure(T_cure-Y_cure);
        homeInfoBO.setAll_confirm(all_confirm);
        homeInfoBO.setAll_cure(all_cure);
        homeInfoBO.setAll_death(all_death);

        return homeInfoBO;
    }

    @Override
    public List<MonthDataBO> getMonthData() throws ParseException {
        List<MonthDataBO> monthData = new ArrayList<>();
        Date date = new Date(System.currentTimeMillis());
        String dateStr = DateUtil.dateToString(date);
        //String month = DateUtil.getMonth(date);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        Date date2 = sf.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date2);
        for(int i = 0;i<7;i++)
        {
            MonthDataBO monthDataBO = new MonthDataBO();
            int confirmDate = patientsMapperCumstom.getMonthDate(i,HealthCode.Confirmed.type);
            int cureData = patientsMapperCumstom.getMonthDate(i,HealthCode.CURE.type);
            int deathData = patientsMapperCumstom.getMonthDate(i,HealthCode.Death.type);
            date2 = calendar.getTime();
            String accDate = sf.format(date2);
            monthDataBO.setMonth(accDate);
            monthDataBO.setCureData(cureData);
            monthDataBO.setConfirmData(confirmDate);
            monthDataBO.setDeadData(deathData);
            monthData.add(monthDataBO);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1); // 设置为上一个月
        }

        Collections.reverse(monthData);

        return monthData;
    }

    @Override
    public List<CityData> getAllCityData() {
        List<CityData> cityDataList = new ArrayList<>();
        List<Patientstatus> patientstatusList = patientstatusMapper.selectAll();

        patientstatusList.forEach(patient->{
            String city = patient.getProvince();
            //在城市列表中找,如果有该城市的对象,则对应的数据加一,没有对象则创建
            Boolean flag = true;
            for(int i = 0;i<cityDataList.size();i++)
            {
                if(patient.getProvince().equals(cityDataList.get(i).getProvince()))
                {
                    //找到的对应的列表项,不需要创建对象
                    flag = false;
                    if(patient.getHealth() == HealthCode.Confirmed.type) cityDataList.get(i).autoAddConfirm();
                    else if (patient.getHealth() == HealthCode.CURE.type) cityDataList.get(i).autoAddCure();
                    else if (patient.getHealth() == HealthCode.Death.type) cityDataList.get(i).autoAdddeath();
                }
            }
            if(flag)
            {
                //创建一个CityData对象放入列表中
                CityData cityData = new CityData();
                cityData.setProvince(patient.getProvince());
                if(patient.getHealth() == HealthCode.Confirmed.type) cityData.autoAddConfirm();
                else if (patient.getHealth() == HealthCode.CURE.type) cityData.autoAddCure();
                else if (patient.getHealth() == HealthCode.Death.type) cityData.autoAdddeath();
                cityDataList.add(cityData);
            }
        });

        return cityDataList;
    }

    @Override
    public List<CityData> getCityDataByHealth(Integer healthCode) {
        List<CityData> cityDataList = new ArrayList<>();
        Example patientStatusExample = new Example(Patientstatus.class);
        Example.Criteria criteria = patientStatusExample.createCriteria();
        criteria.andEqualTo("health",healthCode);
        List<Patientstatus> patientstatusList = patientstatusMapper.selectByExample(patientStatusExample);

        patientstatusList.forEach(patient->{
            String city = patient.getProvince();
            //在城市列表中找,如果有该城市的对象,则对应的数据加一,没有对象则创建
            Boolean flag = true;
            for(int i = 0;i<cityDataList.size();i++)
            {
                if(patient.getProvince().equals(cityDataList.get(i).getProvince()))
                {
                    //找到的对应的列表项,不需要创建对象
                    flag = false;
                    if(patient.getHealth() == HealthCode.Confirmed.type) cityDataList.get(i).autoAddConfirm();
                    else if (patient.getHealth() == HealthCode.CURE.type) cityDataList.get(i).autoAddCure();
                    else if (patient.getHealth() == HealthCode.Death.type) cityDataList.get(i).autoAdddeath();
                }
            }
            if(flag)
            {
                //创建一个CityData对象放入列表中
                CityData cityData = new CityData();
                cityData.setProvince(patient.getProvince());
                if(patient.getHealth() == HealthCode.Confirmed.type) cityData.autoAddConfirm();
                else if (patient.getHealth() == HealthCode.CURE.type) cityData.autoAddCure();
                else if (patient.getHealth() == HealthCode.Death.type) cityData.autoAdddeath();
                cityDataList.add(cityData);
            }
        });

        return cityDataList;
    }

    @Override
    public int getAllconfirm() {
        Map<String,Object> map = new HashMap<>();
        map.put("healthCode",HealthCode.Confirmed.type);
        int allConfirm = patientsMapperCumstom.getAllConfirm(map);
        return allConfirm;
    }


    private List<RiskCheckBO> sortTimeFromRiskCheckBo(List<RiskCheckBO> riskCheckBOList)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //初始化
        riskCheckBOList.forEach(item->{
            List<TimeCompareBO> timeCompareBOList = new ArrayList<>();
            List<RiskCheckPatientBO> riskList = item.getRiskCheckPatientBOList();
            List<RiskCheckUserTripBO> tripList = item.getRiskCheckUserTripBOList();
            riskList.forEach(risk->{
                TimeCompareBO compareBO = new TimeCompareBO();
                compareBO.setDate(risk.getStarttime());
                compareBO.setFlag(1);
                compareBO.setIndex(riskList.indexOf(risk));
                timeCompareBOList.add(compareBO);
                Date startDate = new Date();
                Date stopDate = new Date();
                try {
                    startDate = simpleDateFormat.parse(risk.getStarttime());
                    stopDate = simpleDateFormat.parse(risk.getStoptime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                risk.setStarttime(sdf.format(startDate));
                risk.setStoptime(sdf.format(stopDate));
            });

            tripList.forEach(trip->{
                TimeCompareBO compareBO = new TimeCompareBO();
                compareBO.setDate(trip.getStarttime());
                compareBO.setFlag(2);
                compareBO.setIndex(tripList.indexOf(trip));
                timeCompareBOList.add(compareBO);
                Date startDate = new Date();
                Date stopDate = new Date();
                try {
                    startDate = simpleDateFormat.parse(trip.getStarttime());
                    stopDate = simpleDateFormat.parse(trip.getStoptime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                trip.setStarttime(sdf.format(startDate));
                trip.setStoptime(sdf.format(stopDate));
            });

            List<TimeCompareBO> timeCompareBOList1 = DateBubbleSort(timeCompareBOList);
            timeCompareBOList1.forEach(timeCBO->{
                if(timeCBO.getFlag() == 1)
                {
                    riskList.get(timeCBO.getIndex()).setTimeIndex(timeCBO.getTimeIndex());

                }else if(timeCBO.getFlag() == 2)
                {
                    tripList.get(timeCBO.getIndex()).setTimeIndex(timeCBO.getTimeIndex());
                }
            });

            riskList.sort(Comparator.comparingInt(RiskCheckPatientBO::getTimeIndex));
            tripList.sort(Comparator.comparingInt(RiskCheckUserTripBO::getTimeIndex));

        });

        return riskCheckBOList;
    }

    /**
     * 判断链表中是否有已经有包含该地址的信息对象
     * @param list
     * @param address
     * @return
     */
    private int checkAddress(List<RiskCheckBO> list,String address)
    {
        if (list.size()<=0)
        {
            return -1;
        }

        for(int i = 0;i<list.size();i++)
        {
            RiskCheckBO riskCheckBO = list.get(i);
            if(riskCheckBO.getAddress().equals(address))
            {
                return i;
            }
        }

        return -1;
    }

    public static List<TimeCompareBO> DateBubbleSort(List<TimeCompareBO> timeCompareBOS) {
        TimeCompareBO temp;//定义一个临时变量
        for(int i=0;i<timeCompareBOS.size()-1;i++) {//冒泡趟数
            for (int j = 0; j < timeCompareBOS.size() - 1; j++) {
                if (DateUtil.sortDateString(timeCompareBOS.get(j + 1).getDate(), timeCompareBOS.get(j).getDate())) {
                    temp = timeCompareBOS.get(j);
                    timeCompareBOS.set(j, timeCompareBOS.get(j + 1));
                    timeCompareBOS.set(j + 1, temp);
                }
            }
        }

       for(int i = 0;i<timeCompareBOS.size();i++)
       {
           timeCompareBOS.get(i).setTimeIndex(i);
       }
        return timeCompareBOS;
    }


}
