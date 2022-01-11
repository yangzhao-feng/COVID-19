package com.yang.admin;

import com.alibaba.excel.EasyExcel;
import com.yang.admin.listener.CommunityDataListener;
import com.yang.admin.mapper.CommunityMapper;
import com.yang.admin.mapper.PlaceMapper;
import com.yang.enums.HealthCode;
import com.yang.pojo.BO.CommunityBO;
import com.yang.pojo.BO.CommunityBO2;
import com.yang.pojo.VO.Community;
import com.yang.pojo.VO.Placelocation;
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
public class CommunityServiceImpl extends BaseService implements CommunityService {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private PlaceMapper placeMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean comUploadByExcel(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), Community.class, new CommunityDataListener(communityMapper,this, placeMapper)).sheet().doRead();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean comUpload(CommunityBO communityBO) {
        Community community = new Community();
        BeanUtils.copyProperties(communityBO,community);
        //把用户上传的地址的定位传入到数据库

        if(communityMapper.insert(community)>0)
        {
            return true;
        }

        return false;
    }

    @Override
    public List<CommunityBO> getComInfoByAdd(String address) {

        Example communityExample = new Example(Community.class);
        Example.Criteria criteria = communityExample.createCriteria();

        criteria.andLike("place",address+"%");

        List<Community> communityList = communityMapper.selectByExample(communityExample);

        List<CommunityBO> communityBOList = new ArrayList<>();

        communityList.forEach(patients -> {
            CommunityBO communityBO = new CommunityBO();
            BeanUtils.copyProperties(patients,communityBO);
            communityBOList.add(communityBO);
        });

        return communityBOList;

    }

    @Override
    public List<CommunityBO2> getComInfoByAdd2(String address) {
        Example communityExample = new Example(Community.class);
        Example.Criteria criteria = communityExample.createCriteria();

        criteria.andLike("place",address+"%");

        List<Community> communityList = communityMapper.selectByExample(communityExample);

        List<CommunityBO2> communityBO2List = new ArrayList<>();

        communityList.forEach(item->{
            Example locationExample = new Example(Placelocation.class);
            Example.Criteria locationCriteria = locationExample.createCriteria();

            locationCriteria.andEqualTo("address",item.getPlace());

            Placelocation placelocation = placeMapper.selectOneByExample(locationExample);
            CommunityBO2 communityBO2 = new CommunityBO2();
            BeanUtils.copyProperties(item,communityBO2);
            communityBO2.setLat(placelocation.getLat());
            communityBO2.setLng(placelocation.getLng());
            communityBO2List.add(communityBO2);
        });
        return communityBO2List;
    }

    @Override
    public Integer getAddressRisk(String address) {

        Example communityExample = new Example(Community.class);
        Example.Criteria criteria = communityExample.createCriteria();

        criteria.andEqualTo("place",address);

        Community community = communityMapper.selectOneByExample(communityExample);

        if(community == null)
        {
            return HealthCode.CURE.type;
        }
        return community.getRiskLevel();

    }

}
