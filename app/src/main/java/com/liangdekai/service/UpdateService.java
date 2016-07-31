package com.liangdekai.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.liangdekai.activity.MainActivity;
import com.liangdekai.db.WeatherDB;
import com.liangdekai.util.HandleResponseUtil;
import com.liangdekai.util.RequestAsyncTask;
import com.liangdekai.weather_liangdekai.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 创建一个更新天气信息的服务
 */
public class UpdateService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();//开启线程去更新天气信息
            }
        }).start();
        SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE) ;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("backFromChooseActivity",true);//设置标识
        PendingIntent pendingIntend = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);//实现点击效果
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(preferences.getString("city","暂无"))
                .setContentText(preferences.getString("weather","暂无")+"\t"+preferences.getString("temperature",""))
                .setSmallIcon(R.mipmap.icon)
                .setTicker("天气已经更新，注意天气变化")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntend);
        notificationManager.notify(1,builder.build());
        startForeground(1,builder.build());//设置为前台服务


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);//获取一个AlarmManager实例
        int sixHour = 6 * 60 * 60 * 1000;//设置定时为6小时的任务
        long sleepTime = SystemClock.elapsedRealtime()+sixHour;//获取系统开机到现在所经历的毫秒数
        Intent intented = new Intent(this , UpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intented,0);//获取一个能够执行广播的PendingIntent
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,sleepTime,pendingIntent);//设置定时任务
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气数据
     */
    public void updateWeather(){
        SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE) ;
        String cityName = preferences.getString("city","");//获取文件中已选择城市的名字
        try {
            String cityNameUTF = URLEncoder.encode(cityName,"UTF-8");//进行UPL编码
            String address = "http://v.juhe.cn/weather/index?cityname="+cityNameUTF+"&dtype=&format=2&key=5b34e560321fd5f86680b4deb1e30ad8";
            new RequestAsyncTask(new RequestAsyncTask.RequestListener() {
                @Override
                public void succeed(String result ,String city) {
                    WeatherDB weatherDB = WeatherDB.getInstance(UpdateService.this);
                    SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE) ;
                    String cityName = preferences.getString("city","");//获取文件中已选择城市的名字
                    HandleResponseUtil.praseWeatherResponse(UpdateService.this , weatherDB , result , cityName);
                }

                @Override
                public void failed() {

                }
            }).execute(address);
        } catch (UnsupportedEncodingException e) {//不支持编码异常，说明字符编码有问题
            e.printStackTrace();
        }
    }

}
