package com.example.yzh.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yzh.coolweather.R;
import com.example.yzh.coolweather.model.City;
import com.example.yzh.coolweather.model.CoolWeatherDB;
import com.example.yzh.coolweather.model.County;
import com.example.yzh.coolweather.model.Province;
import com.example.yzh.coolweather.util.HttpCallbackListener;
import com.example.yzh.coolweather.util.HttpUtil;
import com.example.yzh.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private int level;

    private TextView titleText;
    private ListView listView;
    private ProgressDialog progressDialog;
    private List<String> dataList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose);

        titleText = (TextView) findViewById(R.id.tvTitle);
        listView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (level == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    loadCities();
                }else if (level == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    loadCountise();
                }
            }
        });

        loadProvinces();
    }
    /*
    加载省级数据
     */
    private void loadProvinces(){
        provinceList = coolWeatherDB.loadProvinces();
        /*
        数据库有的话就直接加载，没有的话就要更新数据库，再加载
         */
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getProvinceName());
            adapter.notifyDataSetChanged();//刷新
            listView.setSelection(0);//定位到第一个数据
            titleText.setText("中国");
            level = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }
    /*
    加载城市数据
     */
    private void loadCities(){
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList)
                dataList.add(city.getCityName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            level = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    /*
    加载县级数据
     */
    private void loadCountise(){
        countyList = coolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList)
                dataList.add(county.getCountyName());
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            level = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }
    /*
    从服务器获取新的数据并更新到数据库
     */
    private void queryFromServer(final String code,final String type){
        String address;
        if (!TextUtils.isEmpty(code))
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        else
            address = "http://www.weather.com.cn/data/list3/city.xml";
        //开始加载
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //从服务器下载数据并更新到数据库
                boolean result = false;
                if (type.equals("province"))
                    result = Utility.handleProvicesResponse(coolWeatherDB,response);
                else if (type.equals("city"))
                    result = Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                else if (type.equals("county"))
                    result = Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                //更新完成，刷新页面
                if (result){
                    //回到主线程加载ui
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("province"))
                                loadProvinces();
                            else if (type.equals("city"))
                                loadCities();
                            else if (type.equals("county"))
                                loadCountise();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        //回到主线程，弹出一个窗口
                        Toast.makeText(ChooseActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
    //修改按back键的逻辑
    @Override
    public void onBackPressed() {
        if (level == LEVEL_COUNTY){
            loadCities();
        }else if (level == LEVEL_CITY){
            loadProvinces();
        }else if (level == LEVEL_PROVINCE) {
            finish();
        }
    }
}
