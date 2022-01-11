package com.yang.user.impl;

import com.yang.admin.PerInfoService;
import com.yang.user.mapper.UserInformationMapper;
import com.yang.user.mapper.UserloginMapper;
import com.yang.user.impl.pojo.VO.UserInformation;
import com.yang.user.impl.pojo.VO.Userlogin;
import com.yang.user.impl.pojo.BO.UserBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private UserloginMapper userloginMapper;

    @Autowired
    private PerInfoService perInfoService;

    @Autowired
    private UserInformationMapper userInformationMapper;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean Reigister(UserBO userBO) {

        Example userExample = new Example(Userlogin.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("email", userBO.getEmail());

        if(userloginMapper.selectCountByExample(userExample)>0)
        {
            return false;
        }

        Userlogin userlogin = new Userlogin();
        userlogin.setUsername(userBO.getUsername());
        userlogin.setEmail(userBO.getEmail());
        userlogin.setPassword(userBO.getPassword());
        userlogin.setRealname(userBO.getRealname());

        int insert = userloginMapper.insert(userlogin);


        UserInformation userInformation = new UserInformation();
        userInformation.setEmail(userBO.getEmail());
        userInformationMapper.insert(userInformation);

        if(insert > 0)
        {
            return true;
        }
        return false;

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserBO Login(UserBO userBO) {
        //登录功能实现
        Userlogin userlogin = new Userlogin();
        BeanUtils.copyProperties(userBO,userlogin);
        List<Userlogin> select = userloginMapper.select(userlogin);


        if(select.size() <= 0||select == null)
        {
            return null;
        }

        userBO.setUsername(select.get(0).getUsername());
        userBO.setRealname(select.get(0).getRealname());
        return userBO;
    }
}
