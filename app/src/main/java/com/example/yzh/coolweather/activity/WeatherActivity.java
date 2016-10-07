package com.example.yzh.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yzh.coolweather.R;
import com.example.yzh.coolweather.util.HttpCallbackListener;
import com.example.yzh.coolweather.util.HttpUtil;
import com.example.yzh.coolweather.util.Utility;

public class WeatherActivity extends Activity {

    private TextView tvCity;
    private TextView tvPublishTime;
    private TextView tvWeatherDesc;
    private TextView tvMinTemp;
    private TextView tvMaxTemp;
    private TextView tvDate;
    private ProgressDialog progressDialog;
    private Button btnChangeCity;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);

        tvCity = (TextView) findViewById(R.id.tvCity);
        tvPublishTime = (TextView) findViewById(R.id.tvPublishTime);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvWeatherDesc = (TextView) findViewById(R.id.tvDescrition);
        tvMinTemp = (TextView) findViewById(R.id.tvMinTemp);
        tvMaxTemp = (TextView) findViewById(R.id.tvMaxTemp);
        btnChangeCity = (Button) findViewById(R.id.btnChangeCity);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        //判断是否通过选择城市进入这个页面还是直接进入这个页面
        //其实和之前的queryFromServer是一样的，要么从本地加载，要么从服务器加载到本地，再从本地加载
        String countyCode = getIntent().getStringExtra("countyCode");
        if (TextUtils.isEmpty(countyCode)){
            loadWeather();
        }else {
            loadWeatherFromServer(countyCode,"county");
        }

        btnChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this,ChooseActivity.class);
                intent.putExtra("isFromActivity",true);
                startActivity(intent);
                finish();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherCode = prefs.getString("weatherCode","");
                if (!TextUtils.isEmpty(weatherCode)){
                    loadWeatherFromServer(weatherCode,"weather");
                }
                closeProgressDialog();
            }
        });
    }

    private void loadWeatherFromServer(final String code,String type) {
        if (type.equals("county")) {
            final String address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    if (!TextUtils.isEmpty(response)){
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2){
                            //重复调用自身，解决了不能return到weatherCode的问题
                            loadWeatherFromServer(array[1],"weather");
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }else if (type.equals("weather")){
            final String address = "http://www.weather.com.cn/data/cityinfo/"+code+".html";
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Utility.handleWeatherResponse(WeatherActivity.this,response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //回到主线程刷新数据
                            closeProgressDialog();
                            loadWeather();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(WeatherActivity.this,"加载天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void loadWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tvCity.setText(prefs.getString("cityName",""));
        tvPublishTime.setText(prefs.getString("publishTime",""));
        tvWeatherDesc.setText(prefs.getString("weatherDesc",""));
        tvMinTemp.setText(prefs.getString("minTemp",""));
        tvMaxTemp.setText(prefs.getString("maxTemp",""));
        tvDate.setText(prefs.getString("date",""));
    }

    //显示进度框
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("加载ing...");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }
    //关闭进度框
    private void closeProgressDialog(){
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
