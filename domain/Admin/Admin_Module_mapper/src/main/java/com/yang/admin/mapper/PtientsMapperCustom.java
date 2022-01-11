package com.yang.admin.mapper;

import com.yang.pojo.VO.Patients;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface PtientsMapperCustom {

    public int getNumber(@Param("healthCode") int healthCode);

    public int getTodayNumber(@Param("healthCode") int healthCode);

    public int getYesNumber(@Param("healthCode") int healthCode);

    public int getMonthDate(@Param("realtiveMonth") int realtiveMonth,@Param("healthCode") int healthCode);

    @Select("<script>" +
            "SELECT COUNT(DISTINCT(Id_number)) FROM patients\n" +
            "WHERE health = #{healthCode}" +
            "</script>")
    public int getAllConfirm(@Param("paramsMap")Map<String,Object> map);

    @Select("<script>" +
            "SELECT COUNT(DISTINCT(Id_number)) FROM patients\n" +
            "WHERE health = #{healthCode}" +
            "</script>")
    public int getInfoByCode(@Param("paramsMap")Map<String,Object> map);

}
