package com.liangdekai.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.liangdekai.MyApplication;

/**
 * Created by asus on 2016/7/15.
 */
public class NetWorkUtil {
    public static boolean hasNetWork(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()){
            return true;
        }else {
            return false;
        }
    }
}
