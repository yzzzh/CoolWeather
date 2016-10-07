package com.example.yzh.coolweather.util;

import android.text.TextUtils;

import com.example.yzh.coolweather.model.City;
import com.example.yzh.coolweather.model.CoolWeatherDB;
import com.example.yzh.coolweather.model.County;
import com.example.yzh.coolweather.model.Province;

/**
 * Created by YZH on 2016/10/7.
 */

public class Utility {
    /*
    将省级数据解析出来，并导入数据库
     */
    public synchronized static boolean handleProvicesResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0){
                for (String s : allProvinces){
                    String[] array = s.split("|");
                    Province province = new Province();
                    province.setProvinceName(array[0]);
                    province.setProvinceCode(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    /*
    将市级数据解析出来，并导入数据库
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0){
                for (String s : allCities){
                    String[] array = s.split("|");
                    City city = new City();
                    city.setCityName(array[0]);
                    city.setCityCode(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /*
    将县级数据解析出来，并导入数据库
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0){
                for (String s : allCounties){
                    String[] array = s.split("|");
                    County county = new County();
                    county.setCountyName(array[0]);
                    county.setCountyCode(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }
}
