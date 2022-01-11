package com.yang.user.impl;

import com.yang.user.impl.pojo.BO.UserBO;

public interface UserLoginService {


    boolean Reigister(UserBO userBO);

    UserBO Login(UserBO userBO);

}
