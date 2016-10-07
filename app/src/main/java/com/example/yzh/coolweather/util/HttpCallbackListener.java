package com.example.yzh.coolweather.util;

/**
 * Created by YZH on 2016/10/7.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
