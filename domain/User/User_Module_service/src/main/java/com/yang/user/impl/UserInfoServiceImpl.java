package com.yang.user.impl;

import com.alibaba.excel.EasyExcel;
import com.imooc.utils.DateUtil;
import com.imooc.utils.HttpUtil;
import com.imooc.utils.JsonUtils;
import com.yang.user.impl.listener.UsertripDataListener;
import com.yang.user.impl.pojo.BO.UserTripBO2;
import com.yang.user.mapper.UserInformationMapper;
import com.yang.user.impl.pojo.VO.UserInformation;
import com.yang.pojo.PlaceLocation;
import com.yang.user.mapper.PlacelocationMapper;
import com.yang.user.mapper.UsertripMapper;
import com.yang.user.impl.pojo.BO.UserBO;
import com.yang.user.impl.pojo.BO.UserTripBO;
import com.yang.user.impl.pojo.VO.Placelocation;
import com.yang.user.impl.pojo.VO.Usertrip;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final String BaiduAddUrl = "https://api.map.baidu.com/geocoding/v3/?";
    private static final String commonParama = "output=json&ak=aCkho2YWwtnNf2ydI4uEVBfRpTo0Mp1b&callback=showLocation";

    @Autowired
    private UserInformationMapper userInformationMapper;

    @Autowired
    private UsertripMapper usertripMapper;

    @Autowired
    private PlacelocationMapper placelocationMapper;


    @Override
    public UserInformation getUserInfo(String email) {

        Example userInfoExample = new Example(UserInformation.class);
        Example.Criteria criteria = userInfoExample.createCriteria();

        criteria.andEqualTo("email",email);

        UserInformation userInformation = userInformationMapper.selectOneByExample(userInfoExample);

        return userInformation;
    }


    @Override
    public List<UserTripBO> userTrip(UserBO userBO) {
        //用户历史轨迹查看
        List<Usertrip> usertripList = new ArrayList<>();
        Example tripExample = new Example(Usertrip.class);
        Example.Criteria criteria = tripExample.createCriteria();

        criteria.andEqualTo("username",userBO.getUsername());

        usertripList = usertripMapper.selectByExample(tripExample);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        List<UserTripBO> userTripBOList = new ArrayList<>();
        usertripList.forEach(i->{
            UserTripBO userTripBO = new UserTripBO();
            userTripBO.setUsername(i.getUsername());
            userTripBO.setPlace(i.getPlace());
            userTripBO.setStarttime(sdf.format(i.getStarttime()));
            userTripBO.setStoptime(sdf.format(i.getStoptime()));
            userTripBOList.add(userTripBO);
        });
        return userTripBOList;
    }

    @Override
    public List<UserTripBO2> userTrip2(String username, String address) {
        //用户历史轨迹查看
        List<Usertrip> usertripList = new ArrayList<>();
        Example tripExample = new Example(Usertrip.class);
        Example.Criteria criteria = tripExample.createCriteria();

        criteria.andEqualTo("username",username);
        criteria.andLike("place",address+"%");

        usertripList = usertripMapper.selectByExample(tripExample);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        List<UserTripBO2> userTripBOList = new ArrayList<>();
        usertripList.forEach(i->{
            UserTripBO2 userTripBO2 = new UserTripBO2();
            userTripBO2.setUsername(i.getUsername());
            userTripBO2.setStarttime(sdf.format(i.getStarttime()));
            userTripBO2.setStoptime(sdf.format(i.getStoptime()));
            userTripBO2.setPlace(i.getPlace());
            Example placeExample = new Example(Placelocation.class);
            Example.Criteria placeExampleCriteria = placeExample.createCriteria();
            placeExampleCriteria.andEqualTo("address",i.getPlace());
            Placelocation placelocation = placelocationMapper.selectOneByExample(placeExample);
            userTripBO2.setLat(placelocation.getLat());
            userTripBO2.setLng(placelocation.getLng());
            userTripBOList.add(userTripBO2);
        });
        return userTripBOList;
    }

    @Override
    public List<UserTripBO2> userTrip3(String username, String address) {
        //用户历史轨迹查看
        List<Usertrip> usertripList = new ArrayList<>();
        Example tripExample = new Example(Usertrip.class);
        Example.Criteria criteria = tripExample.createCriteria();

        criteria.andEqualTo("username",username);
        criteria.andLike("place",address+"%");

        usertripList = usertripMapper.selectByExample(tripExample);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        List<UserTripBO2> userTripBOList = new ArrayList<>();
        usertripList.forEach(i->{
            UserTripBO2 userTripBO2 = new UserTripBO2();
            userTripBO2.setUsername(i.getUsername());
            userTripBO2.setStarttime(sdf.format(i.getStarttime()));
            userTripBO2.setStoptime(sdf.format(i.getStoptime()));
            userTripBO2.setPlace(i.getPlace());
            Example placeExample = new Example(Placelocation.class);
            Example.Criteria placeExampleCriteria = placeExample.createCriteria();
            placeExampleCriteria.andEqualTo("address",i.getPlace());
            Placelocation placelocation = placelocationMapper.selectOneByExample(placeExample);
            userTripBO2.setLat(placelocation.getLat());
            userTripBO2.setLng(placelocation.getLng());
            userTripBOList.add(userTripBO2);
        });
        return userTripBOList;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean userTripBatchInsert(MultipartFile file){

        try {
            EasyExcel.read(file.getInputStream(), Usertrip.class, new UsertripDataListener(usertripMapper,placelocationMapper,this)).sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean userTripInsert(Usertrip usertrip) throws IOException {

        if(usertripMapper.insert(usertrip)>=0)
        {
            return true;
        }
        return false;
    }

    @Override
    public PlaceLocation getLocationFromAddress(String Address) {

        String parama = "address=" + Address + "&" +commonParama;

        String s = HttpUtil.sendGet(BaiduAddUrl,parama);
        String substring = StringUtils.substring(s, s.indexOf("(") + 1, s.lastIndexOf(")"));
        PlaceLocation placeLocation = JsonUtils.jsonToPojo(substring, PlaceLocation.class);

        return placeLocation;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean inserUserInfo(UserInformation userInformation) {

        Example userInfoExample = new Example(UserInformation.class);
        Example.Criteria criteria = userInfoExample.createCriteria();

        criteria.andEqualTo("email",userInformation.getEmail());

        int i;

        if(userInformationMapper.selectByExample(userInfoExample).size()>0)
        {
            i = userInformationMapper.updateByExampleSelective(userInformation, userInfoExample);
        }
        else{
            i = userInformationMapper.insert(userInformation);
        }

        if(i > 0)
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean insertPlaceLocation(String address) {

        Example placeLocationExample = new Example(Placelocation.class);
        Example.Criteria criteria = placeLocationExample.createCriteria();
        criteria.andEqualTo("address",address);

        if(placelocationMapper.selectByExample(placeLocationExample).size()>0)
        {
            return true;
        }

        PlaceLocation placeLocation = getLocationFromAddress(address);

        Placelocation location2DB = new Placelocation();
        location2DB.setAddress(address);
        location2DB.setLng(String.valueOf(placeLocation.getResult().getLocation().getLng()));
        location2DB.setLat(String.valueOf(placeLocation.getResult().getLocation().getLat()));

        if (placelocationMapper.insert(location2DB)>0)
        {
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        String s = HttpUtil.sendGet("https://api.map.baidu.com/geocoding/v3/?", "address=江苏省镇江市润州区江南华庭&output=json&ak=aCkho2YWwtnNf2ydI4uEVBfRpTo0Mp1b&callback=showLocation");
        PlaceLocation placeLocation = JsonUtils.jsonToPojo(s.substring(s.indexOf("(") + 1, s.indexOf(")")), PlaceLocation.class);
        System.out.println(placeLocation);
    }

}
