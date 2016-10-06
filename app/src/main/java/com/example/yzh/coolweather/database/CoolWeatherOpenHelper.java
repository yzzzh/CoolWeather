package com.example.yzh.coolweather.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.xml.sax.ContentHandler;

/**
 * Created by YZH on 2016/10/6.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    /*
    创建Province表
     */
    private static final String CREATE_PROVINCE = "create table Province(" +
            "id integer primary key autoincrement," +
            "provinceName text," +
            "provinceCode text)";
    /*
    创建City表
     */
    private static final String CREATE_CITY = "create table City(" +
            "id integer primary key autoincrement," +
            "cityName text," +
            "cityCode text," +
            "provinceId integer)";
    /*
    创建County表
     */
    private static final String CREATE_COUNTY = "create table County(" +
            "id integer primary key autoincrement," +
            "countyName text,"+
            "countyCode text," +
            "cityId integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
