package com.example.yzh.coolweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yzh.coolweather.database.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YZH on 2016/10/6.
 */
/*
将对数据库的操作封装成类，便于操作
 */
public class CoolWeatherDB {
    //数据库名字
    public static final String DB_NAME = "coolWeather";
    //数据库版本
    public static final int VERSION = 1;
    //保证只有一个coolWeatherDB
    private static CoolWeatherDB coolWeatherDB;
    //以coolWeatherDB的名义，实际上操作的还是db
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper coolWeatherOpenHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = coolWeatherOpenHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context){
        if (coolWeatherDB == null)
            coolWeatherDB = new CoolWeatherDB(context);
        return coolWeatherDB;
    }

    /*
    将province对象存到数据库中
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("provinceName",province.getProvinceName());
            values.put("provinceCode",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /*
    将city对象存到数据库中
     */
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("cityName",city.getCityName());
            values.put("cityCode",city.getCityCode());
            values.put("provinceId",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /*
    将county对象存到数据库中
     */
    public void saveCounty(County county){
        if (county != null){
            ContentValues values = new ContentValues();
            values.put("countyName",county.getCountyName());
            values.put("countyCode",county.getCountyCode());
            values.put("cityId",county.getCityId());
            db.insert("County",null,values);
        }
    }

    /*
    从数据库中取出所有省份信息
     */
    public List<Province> loadProvinces(){
        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("provinceName")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("provinceCode")));
                provinceList.add(province);
            } while (cursor.moveToNext());
        }
        return provinceList;
    }

    /*
    从数据库取出某省份所有城市的信息
     */
    public List<City> loadCities(int provinceId){
        List<City> cityList = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"provinceId = ?",new String[]{provinceId+""},null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("cityCode")));
                city.setProvinceId(provinceId);
                cityList.add(city);
            }while (cursor.moveToNext());
        }
        return cityList;
    }

    /*
    从数据库取出某城市所有县的信息
     */
    public List<County> loadCounties(int cityId){
        List<County> countyList = new ArrayList<County>();
        Cursor cursor = db.query("County",null,"CityId = ?",new String[]{cityId+""},null,null,null);
        if (cursor.moveToFirst()){
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("countyName")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("countyCode")));
                county.setCityId(cityId);
                countyList.add(county);
            }while (cursor.moveToNext());
        }
        return countyList;
    }
}
