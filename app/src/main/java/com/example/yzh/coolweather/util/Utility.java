package com.example.yzh.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.yzh.coolweather.model.City;
import com.example.yzh.coolweather.model.CoolWeatherDB;
import com.example.yzh.coolweather.model.County;
import com.example.yzh.coolweather.model.Province;

import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

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
                    String[] array = s.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
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
                    String[] array = s.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
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
                    String[] array = s.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }

    /*
    解析天气的数据，并存到本地
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherinfo.getString("city");
            String weatherCode = weatherinfo.getString("cityid");
            String maxTemp = weatherinfo.getString("temp2");
            String minTemp = weatherinfo.getString("temp1");
            String weatherDescription = weatherinfo.getString("weather");
            String publishTime = weatherinfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,minTemp,maxTemp,weatherDescription,publishTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
    将天气信息通过sharePreferences存到本地
     */
    private static void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                        String minTemp,String maxTemp,String weatherDesc,String publishTime){
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy年MM月dd日");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("citySelected",true);
        editor.putString("cityName",cityName);
        editor.putString("weatherCode",weatherCode);
        editor.putString("minTemp",minTemp);
        editor.putString("maxTemp",maxTemp);
        editor.putString("weatherDesc",weatherDesc);
        editor.putString("publishTime",publishTime);
        editor.putString("date",sdf.format(new Date()));
        editor.commit();
    }
}
