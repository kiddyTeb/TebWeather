package com.liangdekai.util;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 管理程序中的全局Context，以便能轻松获取context
 */
public class MyApplication extends Application {
    private static Context context;
    private static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        requestQueue = Volley.newRequestQueue(this , null);
    }

    public static Context getContext() {
        return context;
    }

    public static RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
