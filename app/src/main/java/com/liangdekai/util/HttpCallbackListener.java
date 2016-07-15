package com.liangdekai.util;

/**
 * Created by asus on 2016/7/15
 */
public interface HttpCallbackListener {
    void onFinish(String result);
    void onError(Exception e);
}
