package com.yang.user.impl.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yang.pojo.PlaceLocation;
import com.yang.user.impl.UserInfoService;
import com.yang.user.mapper.PlacelocationMapper;
import com.yang.user.mapper.UsertripMapper;
import com.yang.user.impl.pojo.VO.Placelocation;
import com.yang.user.impl.pojo.VO.Usertrip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

public class UsertripDataListener extends AnalysisEventListener<Usertrip> {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(UsertripDataListener.class);

    private UsertripMapper usertripMapper;

    private PlacelocationMapper placelocationMapper;

    private UserInfoService userInfoService;

    public UsertripDataListener(UsertripMapper usertripMapper, PlacelocationMapper placelocationMapper, UserInfoService userInfoService) {
        this.usertripMapper = usertripMapper;
        this.placelocationMapper = placelocationMapper;
        this.userInfoService = userInfoService;
    }

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<Usertrip> list = new ArrayList<>();


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Usertrip data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", com.alibaba.fastjson.JSON.toJSONString(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        PlaceLocation placeLocation = userInfoService.getLocationFromAddress(data.getPlace());

        saveData(data,placeLocation);
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
    private void saveData(Usertrip data,PlaceLocation placeLocation) {
        // 最好改成批插入
        //检测到重复自动跳过不插入
        Example placeExample = new Example(Placelocation.class);
        Example.Criteria criteria = placeExample.createCriteria();
        criteria.andEqualTo("address",data.getPlace());

        if(placelocationMapper.selectByExample(placeExample).size()>0)
        {
            return;
        }
        usertripMapper.insert(data);
        Placelocation location2DB = new Placelocation();
        location2DB.setAddress(data.getPlace());
        location2DB.setLng(String.valueOf(placeLocation.getResult().getLocation().getLng()));
        location2DB.setLat(String.valueOf(placeLocation.getResult().getLocation().getLat()));
        placelocationMapper.insert(location2DB);
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
        LOGGER.info("存储数据库成功！");
    }
}