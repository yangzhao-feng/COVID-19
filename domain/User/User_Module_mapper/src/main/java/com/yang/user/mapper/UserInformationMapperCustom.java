package com.yang.user.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserInformationMapperCustom {

    public int insertInfo(@Param("paramsMap") Map<String,Object> map);
}
