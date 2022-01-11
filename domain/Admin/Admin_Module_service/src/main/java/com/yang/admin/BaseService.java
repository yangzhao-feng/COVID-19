package com.yang.admin;

import com.imooc.utils.HttpUtil;
import com.imooc.utils.JsonUtils;
import com.yang.pojo.PlaceLocation;
import org.apache.commons.lang3.StringUtils;

public class BaseService {

    private static final String BaiduAddUrl = "https://api.map.baidu.com/geocoding/v3/?";
    private static final String commonParama = "output=json&ak=aCkho2YWwtnNf2ydI4uEVBfRpTo0Mp1b&callback=showLocation";

    public static PlaceLocation getLocationFromAddress(String Address) {

        String parama = "address=" + Address + "&" +commonParama;

        String s = HttpUtil.sendGet(BaiduAddUrl,parama);
        String substring = StringUtils.substring(s, s.indexOf("(") + 1, s.lastIndexOf(")"));
        PlaceLocation placeLocation = JsonUtils.jsonToPojo(substring, PlaceLocation.class);

        return placeLocation;
    }

}
