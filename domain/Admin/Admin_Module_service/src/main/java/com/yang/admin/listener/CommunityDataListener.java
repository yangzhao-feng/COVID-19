package com.yang.admin.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yang.admin.CommunityServiceImpl;
import com.yang.admin.mapper.CommunityMapper;
import com.yang.admin.mapper.PlaceMapper;
import com.yang.pojo.PlaceLocation;
import com.yang.pojo.VO.Community;
import com.yang.pojo.VO.Placelocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

public class CommunityDataListener extends AnalysisEventListener<Community> {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(CommunityDataListener.class);

    private CommunityMapper communityMapper;

    private CommunityServiceImpl communityServiceimpl;

    private PlaceMapper placeMapper;

    public CommunityDataListener(CommunityMapper communityMapper, CommunityServiceImpl communityServiceimpl, PlaceMapper placeMapper) {
        this.communityMapper = communityMapper;
        this.communityServiceimpl = communityServiceimpl;
        this.placeMapper = placeMapper;
    }

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<Community> list = new ArrayList<>();


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Community data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", com.alibaba.fastjson.JSON.toJSONString(data));
        list.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        PlaceLocation placelocation = communityServiceimpl.getLocationFromAddress(data.getPlace());
        saveData(data,placelocation);

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
    private void saveData(Community data, PlaceLocation placeLocation) {
        // 最好改成批插入
        //检查是否重复
        Example communityExample = new Example(Community.class);
        Example.Criteria criteria = communityExample.createCriteria();
        criteria.andEqualTo(data);
        if(communityMapper.selectOneByExample(communityExample) != null)
        {
            //不插入相同数据
            LOGGER.info("{}——数据相同", data);
            return;
        }else {
            // 如果地名相同,则更新数据内容
            communityMapper.insert(data);
            Placelocation location2DB = new Placelocation();
            Example placeExample = new Example(Placelocation.class);
            Example.Criteria placeCriteria = placeExample.createCriteria();
            placeCriteria.andEqualTo("address",data.getPlace());
            if(placeMapper.selectOneByExample(placeExample) != null)
            {
                //不插入相同地址
                LOGGER.info("{}——已有地址",location2DB.getAddress());
                return;
            }
            location2DB.setAddress(data.getPlace());
            location2DB.setLng(String.valueOf(placeLocation.getResult().getLocation().getLng()));
            location2DB.setLat(String.valueOf(placeLocation.getResult().getLocation().getLat()));
            placeMapper.insert(location2DB);
            LOGGER.info("{}条数据，开始存储数据库！", list.size());
            LOGGER.info("存储数据库成功！");
        }

    }
}