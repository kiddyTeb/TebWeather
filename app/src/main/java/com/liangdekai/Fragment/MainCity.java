package com.liangdekai.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangdekai.bean.FutureWeatherInfo;
import com.liangdekai.db.WeatherDbOpenHelper;
import com.liangdekai.weather_liangdekai.R;

import java.util.List;

/**
 * Created by asus on 2016/7/21.
 */
public class MainCity extends Fragment {
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
    private View view ;
    private List<FutureWeatherInfo> mWeatherList;
    private WeatherDbOpenHelper mWeatherDbOpenHelper;
    private String mCityName;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show , container , false);
        initView();
        SharedPreferences preferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE) ;
        if (!"".equals(preferences.getString("city","")) ){
            loadData();
        }
        Log.d("test","onCreateView");
        return view;
    }

    public static MainCity newInstance(String cityName){
        MainCity mainCity = new MainCity();
        Bundle bundle = new Bundle();
        bundle.putString("cityName",cityName);
        mainCity.setArguments(bundle);
        return mainCity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            mCityName = bundle.getString("cityName");
        }
        Log.d("test","onCreate");
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test","onDestory碎片");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("test","onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("test","deV");
    }

    private void initView(){
        mTvCity = (TextView) view.findViewById(R.id.weather_tv_city);//初始化当日天气信息所需的控件
        mTvPublishTime = (TextView) view.findViewById(R.id.weather_tv_publish);
        mTvDate = (TextView) view.findViewById(R.id.weather_tv_time);
        mTvWind = (TextView) view.findViewById(R.id.weather_tv_wind);
        mTvTemperature = (TextView) view.findViewById(R.id.weather_tv_temperature);
        mTvWeather = (TextView) view.findViewById(R.id.weather_tv_weather);
        mTvDress = (TextView) view.findViewById(R.id.weather_tv_howdress);
        mTvTravel = (TextView) view.findViewById(R.id.weather_tv_howtravel);
        mTvSport = (TextView) view.findViewById(R.id.weather_tv_howsport);
        mTvUv = (TextView) view.findViewById(R.id.weather_tv_howray);
        mTvAdvice = (TextView) view.findViewById(R.id.weather_tv_suggestion);

        mTvFutureDayOne = (TextView) view.findViewById(R.id.weather_tv_dayOne);//初始化未来六日天气信息所需的控件
        mIvFutureImageOne = (ImageView) view.findViewById(R.id.weather_iv_first);
        mTvFutureWeatherOne = (TextView) view.findViewById(R.id.weather_tv_futureWeatherOne);
        mTvFutureTempOne = (TextView) view.findViewById(R.id.weather_tv_futureTempOne);
        mTvFutureWindOne = (TextView) view.findViewById(R.id.weather_tv_futureWindOne);
        mTvFutureDayTwo = (TextView) view.findViewById(R.id.weather_tv_dayTwo);
        mIvFutureImageTwo = (ImageView) view.findViewById(R.id.weather_iv_second);
        mTvFutureWeatherTwo = (TextView) view.findViewById(R.id.weather_tv_futureWeatherTwo);
        mTvFutureTempTwo = (TextView) view.findViewById(R.id.weather_tv_futureTempTwo);
        mTvFutureWindTwo = (TextView) view.findViewById(R.id.weather_tv_futureWindTwo);
        mTvFutureDayThree = (TextView) view.findViewById(R.id.weather_tv_dayThree);
        mIvFutureImageThree = (ImageView) view.findViewById(R.id.weather_iv_third);
        mTvFutureWeatherThree = (TextView) view.findViewById(R.id.weather_tv_futureWeatherThree);
        mTvFutureTempThree = (TextView) view.findViewById(R.id.weather_tv_futureTempThree);
        mTvFutureWindThree = (TextView) view.findViewById(R.id.weather_tv_futureWindThree);
        mTvFutureDayForth = (TextView) view.findViewById(R.id.weather_tv_dayForth);
        mIvFutureImageForth = (ImageView) view.findViewById(R.id.weather_iv_forth);
        mTvFutureWeatherForth = (TextView) view.findViewById(R.id.weather_tv_futureWeatherForth);
        mTvFutureTempForth = (TextView) view.findViewById(R.id.weather_tv_futureTempForth);
        mTvFutureWindForth = (TextView) view.findViewById(R.id.weather_tv_futureWindForth);
        mTvFutureDayFifth = (TextView) view.findViewById(R.id.weather_tv_dayFifth);
        mIvFutureImageFifth = (ImageView) view.findViewById(R.id.weather_iv_fifth);
        mTvFutureWeatherFifth = (TextView) view.findViewById(R.id.weather_tv_futureWeatherFifth);
        mTvFutureTempFifth = (TextView) view.findViewById(R.id.weather_tv_futureTempFifth);
        mTvFutureWindFifth = (TextView) view.findViewById(R.id.weather_tv_futureWindFifth);
        mTvFutureDaySix = (TextView) view.findViewById(R.id.weather_tv_daySix);
        mIvFutureImageSix = (ImageView) view.findViewById(R.id.weather_iv_six);
        mTvFutureWeatherSix = (TextView) view.findViewById(R.id.weather_tv_futureWeatherSix);
        mTvFutureTempSix = (TextView) view.findViewById(R.id.weather_tv_futureTempSix);
        mTvFutureWindSix = (TextView) view.findViewById(R.id.weather_tv_futureWindSix);

        mWeatherDbOpenHelper = new WeatherDbOpenHelper(getActivity());
    }

    public void loadData(){
        mWeatherList = mWeatherDbOpenHelper.loadFutureWeather(mCityName);//加载未来天气信息
        Log.d("test",mCityName);
        SharedPreferences preferences = getActivity().getSharedPreferences("data" , Context.MODE_PRIVATE) ;
        mTvCity.setText(preferences.getString("city",""));//对各种组件进行设置值
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
}
