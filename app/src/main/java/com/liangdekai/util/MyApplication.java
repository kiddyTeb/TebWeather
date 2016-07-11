package com.liangdekai.util;

import android.app.Application;
import android.content.Context;

/**
 * 管理程序中的全局Context，以便能轻松获取context
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
