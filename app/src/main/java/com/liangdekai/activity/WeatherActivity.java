package com.liangdekai.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liangdekai.bean.FutureWeatherInfo;
import com.liangdekai.service.UpdateService;
import com.liangdekai.util.NetWorkUtil;
import com.liangdekai.util.MyAsyncTask;
import com.liangdekai.weather_liangdekai.R;
import com.liangdekai.db.WeatherDbOpenHelper;
import com.liangdekai.util.HandleResponseUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 显示天气信息
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    private TextView mTvCity;
    private TextView mTvPublishTime;
    private TextView mTvDate;
    private TextView mTvWind;
    private TextView mTvTemperature;
    private TextView mTvWeather;
    private TextView mTvDress;
    private TextView mTvTravel;
    private TextView mTvSport;
    private TextView mTvUv;
    private TextView mTvAdvice;

    private TextView mTvFutureDayOne;
    private ImageView mIvFutureImageOne;
    private TextView mTvFutureWeatherOne;
    private TextView mTvFutureTempOne;
    private TextView mTvFutureWindOne;
    private TextView mTvFutureDayTwo;
    private ImageView mIvFutureImageTwo;
    private TextView mTvFutureWeatherTwo;
    private TextView mTvFutureTempTwo;
    private TextView mTvFutureWindTwo;
    private TextView mTvFutureDayThree;
    private ImageView mIvFutureImageThree;
    private TextView mTvFutureWeatherThree;
    private TextView mTvFutureTempThree;
    private TextView mTvFutureWindThree;
    private TextView mTvFutureDayForth;
    private ImageView mIvFutureImageForth;
    private TextView mTvFutureWeatherForth;
    private TextView mTvFutureTempForth;
    private TextView mTvFutureWindForth;
    private TextView mTvFutureDayFifth;
    private ImageView mIvFutureImageFifth;
    private TextView mTvFutureWeatherFifth;
    private TextView mTvFutureTempFifth;
    private TextView mTvFutureWindFifth;
    private TextView mTvFutureDaySix;
    private ImageView mIvFutureImageSix;
    private TextView mTvFutureWeatherSix;
    private TextView mTvFutureTempSix;
    private TextView mTvFutureWindSix;

    private Button mBtChooseCity;
    private Button mBtRefresh;
    private LinearLayout mLlBackground;
    private List<FutureWeatherInfo> mWeatherList;
    private WeatherDbOpenHelper mWeatherDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
        setContentView(R.layout.activity_weather);//加载布局
        initializeView();//初始化控件
        setOnListener();
        mWeatherDbOpenHelper = new WeatherDbOpenHelper(this);
        boolean isFromChooseActivity = getIntent().getBooleanExtra("backFromChooseActivity",false);//判断是否通过BACK返回到此界面,或者是否已经选择城市跳过选择界面
        boolean gps = getIntent().getBooleanExtra("gps",false);//判断是否从自动定位进入此界面
        if (gps){
            findWeatherByGps(getIntent().getStringExtra("latitude"),getIntent().getStringExtra("longtitude"));//根据经纬度进行查询天气
        }else {
            if (isFromChooseActivity) {//如果是，则直接加载数据库中的内容
                mWeatherList = mWeatherDbOpenHelper.loadFutureWeather();
                showWeather();//展现天气
            }else if (NetWorkUtil.hasNetWork()){
                String cityId = getIntent().getStringExtra("cityId");//获取选择的城市ID
                sendResquest(cityId);//通过城市ID发送请求
            } else {
                Toast.makeText(WeatherActivity.this , "网络连接异常，请检查网络" , Toast.LENGTH_LONG).show();
                mWeatherList = mWeatherDbOpenHelper.loadFutureWeather();
                showWeather();//展现天气
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1 :
                if (resultCode == RESULT_OK){
                    String cityId = data.getStringExtra("cityId");
                    sendResquest(cityId);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weather_bt_switch://点击切换城市
                Intent intent = new Intent(WeatherActivity.this,ChooseActivity.class);
                intent.putExtra("fromWeatherActivity",true);//设置是否从天气展示界面跳转而回的标志
                startActivityForResult(intent ,1);
                break;
            case R.id.weather_bt_refresh://点击更新按钮
                SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE) ;
                try {
                    if (NetWorkUtil.hasNetWork()){
                        sendResquest(URLEncoder.encode(preferences.getString("city",""),"UTF-8"));//从文件获取城市名字并转换格式，重新发送请求
                    }else {
                        Toast.makeText(WeatherActivity.this , "网络连接异常，请检查网络" , Toast.LENGTH_LONG).show();
                    }
                } catch (UnsupportedEncodingException e) {//不支持编码异常，说明字符编码有问题
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 设置各种事件监听
     */
    private void setOnListener(){
        mBtChooseCity.setOnClickListener(this);//添加事件监听
        mBtRefresh.setOnClickListener(this);
    }

    /**
     * 初始化各种控件
     */
    private void initializeView(){
        mTvCity = (TextView) findViewById(R.id.weather_tv_city);//初始化当日天气信息所需的控件
        mTvPublishTime = (TextView) findViewById(R.id.weather_tv_publish);
        mTvDate = (TextView) findViewById(R.id.weather_tv_time);
        mTvWind = (TextView) findViewById(R.id.weather_tv_wind);
        mTvTemperature = (TextView) findViewById(R.id.weather_tv_temperature);
        mTvWeather = (TextView) findViewById(R.id.weather_tv_weather);
        mTvDress = (TextView) findViewById(R.id.weather_tv_howdress);
        mTvTravel = (TextView) findViewById(R.id.weather_tv_howtravel);
        mTvSport = (TextView) findViewById(R.id.weather_tv_howsport);
        mTvUv = (TextView) findViewById(R.id.weather_tv_howray);
        mTvAdvice = (TextView) findViewById(R.id.weather_tv_suggestion);
        mBtChooseCity = (Button) findViewById(R.id.weather_bt_switch);
        mBtRefresh = (Button) findViewById(R.id.weather_bt_refresh);

        mTvFutureDayOne = (TextView) findViewById(R.id.weather_tv_dayOne);//初始化未来六日天气信息所需的控件
        mIvFutureImageOne = (ImageView) findViewById(R.id.weather_iv_first);
        mTvFutureWeatherOne = (TextView) findViewById(R.id.weather_tv_futureWeatherOne);
        mTvFutureTempOne = (TextView) findViewById(R.id.weather_tv_futureTempOne);
        mTvFutureWindOne = (TextView) findViewById(R.id.weather_tv_futureWindOne);
        mTvFutureDayTwo = (TextView) findViewById(R.id.weather_tv_dayTwo);
        mIvFutureImageTwo = (ImageView) findViewById(R.id.weather_iv_second);
        mTvFutureWeatherTwo = (TextView) findViewById(R.id.weather_tv_futureWeatherTwo);
        mTvFutureTempTwo = (TextView) findViewById(R.id.weather_tv_futureTempTwo);
        mTvFutureWindTwo = (TextView) findViewById(R.id.weather_tv_futureWindTwo);
        mTvFutureDayThree = (TextView) findViewById(R.id.weather_tv_dayThree);
        mIvFutureImageThree = (ImageView) findViewById(R.id.weather_iv_third);
        mTvFutureWeatherThree = (TextView) findViewById(R.id.weather_tv_futureWeatherThree);
        mTvFutureTempThree = (TextView) findViewById(R.id.weather_tv_futureTempThree);
        mTvFutureWindThree = (TextView) findViewById(R.id.weather_tv_futureWindThree);
        mTvFutureDayForth = (TextView) findViewById(R.id.weather_tv_dayForth);
        mIvFutureImageForth = (ImageView) findViewById(R.id.weather_iv_forth);
        mTvFutureWeatherForth = (TextView) findViewById(R.id.weather_tv_futureWeatherForth);
        mTvFutureTempForth = (TextView) findViewById(R.id.weather_tv_futureTempForth);
        mTvFutureWindForth = (TextView) findViewById(R.id.weather_tv_futureWindForth);
        mTvFutureDayFifth = (TextView) findViewById(R.id.weather_tv_dayFifth);
        mIvFutureImageFifth = (ImageView) findViewById(R.id.weather_iv_fifth);
        mTvFutureWeatherFifth = (TextView) findViewById(R.id.weather_tv_futureWeatherFifth);
        mTvFutureTempFifth = (TextView) findViewById(R.id.weather_tv_futureTempFifth);
        mTvFutureWindFifth = (TextView) findViewById(R.id.weather_tv_futureWindFifth);
        mTvFutureDaySix = (TextView) findViewById(R.id.weather_tv_daySix);
        mIvFutureImageSix = (ImageView) findViewById(R.id.weather_iv_six);
        mTvFutureWeatherSix = (TextView) findViewById(R.id.weather_tv_futureWeatherSix);
        mTvFutureTempSix = (TextView) findViewById(R.id.weather_tv_futureTempSix);
        mTvFutureWindSix = (TextView) findViewById(R.id.weather_tv_futureWindSix);
        mLlBackground = (LinearLayout) findViewById(R.id.weather_ll_background);
    }


    /**
     * 展示天气信息
     */
    public void showWeather(){
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE) ;
        mTvCity.setText(preferences.getString("city",""));//对各种组件进行设置值
        changeBackground(mLlBackground,preferences.getString("weather",""));
        mTvPublishTime.setText(preferences.getString("time",""));
        mTvDate.setText(preferences.getString("date_y",""));
        mTvWind.setText(preferences.getString("wind",""));
        mTvWeather.setText(preferences.getString("weather",""));
        mTvTemperature.setText(preferences.getString("temperature",""));
        mTvDress.setText(preferences.getString("dressing_index",""));
        mTvTravel.setText(preferences.getString("travel_index",""));
        mTvSport.setText(preferences.getString("exercise_index",""));
        mTvUv.setText(preferences.getString("uv_index",""));
        mTvAdvice.setText(preferences.getString("dressing_advice",""));

        mTvFutureDayOne.setText(mWeatherList.get(0).getWeek());
        changeImage(mIvFutureImageOne,mWeatherList.get(0).getId());
        mTvFutureWeatherOne.setText(mWeatherList.get(0).getWeather());
        mTvFutureTempOne.setText(mWeatherList.get(0).getTemperature());
        mTvFutureWindOne.setText(mWeatherList.get(0).getWind());
        mTvFutureDayTwo.setText(mWeatherList.get(1).getWeek());
        changeImage(mIvFutureImageTwo,mWeatherList.get(1).getId());
        mTvFutureWeatherTwo.setText(mWeatherList.get(1).getWeather());
        mTvFutureTempTwo.setText(mWeatherList.get(1).getTemperature());
        mTvFutureWindTwo.setText(mWeatherList.get(1).getWind());
        mTvFutureDayThree.setText(mWeatherList.get(2).getWeek());
        changeImage(mIvFutureImageThree,mWeatherList.get(2).getId());
        mTvFutureWeatherThree.setText(mWeatherList.get(2).getWeather());
        mTvFutureTempThree.setText(mWeatherList.get(2).getTemperature());
        mTvFutureWindThree.setText(mWeatherList.get(2).getWind());
        mTvFutureDayForth.setText(mWeatherList.get(3).getWeek());
        changeImage(mIvFutureImageForth,mWeatherList.get(3).getId());
        mTvFutureWeatherForth.setText(mWeatherList.get(3).getWeather());
        mTvFutureTempForth.setText(mWeatherList.get(3).getTemperature());
        mTvFutureWindForth.setText(mWeatherList.get(3).getWind());
        mTvFutureDayFifth.setText(mWeatherList.get(4).getWeek());
        changeImage(mIvFutureImageFifth,mWeatherList.get(4).getId());
        mTvFutureWeatherFifth.setText(mWeatherList.get(4).getWeather());
        mTvFutureTempFifth.setText(mWeatherList.get(4).getTemperature());
        mTvFutureWindFifth.setText(mWeatherList.get(4).getWind());
        mTvFutureDaySix.setText(mWeatherList.get(5).getWeek());
        changeImage(mIvFutureImageSix,mWeatherList.get(5).getId());
        mTvFutureWeatherSix.setText(mWeatherList.get(5).getWeather());
        mTvFutureTempSix.setText(mWeatherList.get(5).getTemperature());
        mTvFutureWindSix.setText(mWeatherList.get(5).getWind());
        Intent intent = new Intent(WeatherActivity.this, UpdateService.class);
        startService(intent);//启动更新数据服务
    }

    /**
     * 根据城市ID发送请求
     * @param cityId
            */
    private void sendResquest(String cityId){
                String address = "http://v.juhe.cn/weather/index?cityname="+cityId+"&dtype=&format=2&key=5b34e560321fd5f86680b4deb1e30ad8";
                new MyAsyncTask(getFragmentManager(), new MyAsyncTask.RequestListener() {
                    @Override
                    public void succeed(String result) {
                        boolean flag = HandleResponseUtil.praseWeatherResponse(WeatherActivity.this , mWeatherDbOpenHelper , result);
                        if (flag){
                            mWeatherList = mWeatherDbOpenHelper.loadFutureWeather();//加载未来天气信息
                            showWeather();
                        }else {
                            Toast.makeText(WeatherActivity.this, "数据解析失败...", Toast.LENGTH_SHORT).show();
                        }
                    }

            @Override
            public void failed() {
                Toast.makeText(WeatherActivity.this, "网络请求失败...", Toast.LENGTH_SHORT).show();
            }
        }).execute(address);
    }

    /**
     * 根据经度纬度发送请求，返回JSON天气数据，并处理存储
     * @param latitude
     * @param longtitude
     */
    private void findWeatherByGps(String latitude , String longtitude){
        String address = "http://v.juhe.cn/weather/geo?format=2&key=5b34e560321fd5f86680b4deb1e30ad8&lon="+longtitude+"&lat="+latitude;
        new MyAsyncTask(getFragmentManager(), new MyAsyncTask.RequestListener() {
            @Override
            public void succeed(String result) {
                boolean flag = HandleResponseUtil.praseWeatherResponse(WeatherActivity.this , mWeatherDbOpenHelper , result);
                if (flag){
                    mWeatherList = mWeatherDbOpenHelper.loadFutureWeather();//加载未来天气信息
                    showWeather();
                }else {
                    Toast.makeText(WeatherActivity.this, "数据解析失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed() {
                Toast.makeText(WeatherActivity.this, "网络请求失败...", Toast.LENGTH_SHORT).show();
            }
        }).execute(address);
    }

    /**
     * 通过不同的天气种类ID来转换不同天气的图片
     * @param imageView
     * @param weatherId
     */
    private void changeImage(ImageView imageView , String weatherId){
        int id = Integer.valueOf(weatherId);
        switch(id){
            case 0: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.sun));break;
            case 1: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.clouds));break;
            case 2: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.shadow));break;
            case 3: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.smallrain));break;
            case 4: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.lighting));break;
            case 5: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ice_with_rain));break;
            case 6: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.snow_rain));break;
            case 7: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.little_rain));break;
            case 19:
            case 21:
            case 8: imageView.setImageDrawable(getResources().getDrawable(R.mipmap.middle_rain));break;
            case 9:
            case 10:
            case 11:
            case 22:
            case 23:
            case 24:
            case 25:
            case 12:imageView.setImageDrawable(getResources().getDrawable(R.mipmap.big_rain));break;
            case 13:
            case 26:
            case 14:imageView.setImageDrawable(getResources().getDrawable(R.mipmap.snow));break;
            case 15:
            case 16:
            case 27:
            case 28:
            case 17:imageView.setImageDrawable(getResources().getDrawable(R.mipmap.big_snow));break;
            case 18:imageView.setImageDrawable(getResources().getDrawable(R.mipmap.flog));break;
            case 29:
            case 30:
            case 31:
            case 20:imageView.setImageDrawable(getResources().getDrawable(R.mipmap.sandstorm));break;
            case 53:imageView.setImageDrawable(getResources().getDrawable(R.mipmap.mai));break;
        }
    }

    /**
     * 通过不同的天气种类来转换不同天气的背景
     * @param linearLayout
     * @param weatherName
     */
    private void changeBackground(LinearLayout linearLayout , String weatherName){
        if(weatherName.contains("阴")||weatherName.contains("云")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg02));
        }else if(weatherName.contains("晴")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg01));
        }else if (weatherName.contains("雪")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg_snow));
        }else if (weatherName.contains("雨")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg_rain));
        }else if (weatherName.contains("雾")||weatherName.contains("霾")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.bg_fog));
        } else {
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg01));
        }
    }

}
