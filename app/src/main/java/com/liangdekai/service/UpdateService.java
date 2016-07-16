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
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.liangdekai.activity.WeatherActivity;
import com.liangdekai.db.WeatherDbOpenHelper;
import com.liangdekai.util.HandleResponseUtil;
import com.liangdekai.util.HttpCallbackListener;
import com.liangdekai.util.HttpUtil;
import com.liangdekai.util.MyApplication;
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent notificationIntent = new Intent(this, WeatherActivity.class);
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
        Intent intented = new Intent(this , UpdateReceive.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intented,0);//获取一个能够执行广播的PendingIntent
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,sleepTime,pendingIntent);//设置定时任务
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = preferences.getString("city","");//获取文件中已选择城市的名字
        try {
            String cityNameUTF = URLEncoder.encode(cityName,"UTF-8");//进行UPL编码
            String address = "http://v.juhe.cn/weather/index?cityname="+cityNameUTF+"&dtype=&format=&key=e56938624d0f9e670b989c945ede8aad";
            //new MyAsyncTask().execute(address);//启动更新任务
            resquestThread(address);
        } catch (UnsupportedEncodingException e) {//不支持编码异常，说明字符编码有问题
            e.printStackTrace();
        }
    }

    /*class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... address) {
            String result ;
            try {
                HttpClient httpClient = new DefaultHttpClient();//获取实例
                HttpGet httpGet = new HttpGet(address[0]);//创建HttpGet对象，传入网络地址
                HttpResponse httpResponse = httpClient.execute(httpGet);//IOException
                if (httpResponse.getStatusLine().getStatusCode() == 200){
                    HttpEntity httpEntity = httpResponse.getEntity();//获取HttpEntity实例
                    result = EntityUtils.toString(httpEntity,"utf-8");//转换为字符串
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                WeatherDbOpenHelper weatherDbOpenHelper = new WeatherDbOpenHelper(MyApplication.getContext());
                HandleResponseUtil.handleWeatherResponse(MyApplication.getContext(), weatherDbOpenHelper, result);//处理结果
            }
        }
    }*/

    private void resquestThread(final String address){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtil.sendByConnection(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String result) {
                        if (result != null) {
                            WeatherDbOpenHelper weatherDbOpenHelper = new WeatherDbOpenHelper(MyApplication.getContext());
                            HandleResponseUtil.handleWeatherResponse(MyApplication.getContext(), weatherDbOpenHelper, result);//处理结果
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("test", "error");
                    }
                });
            }
        }).start();
    }

}
