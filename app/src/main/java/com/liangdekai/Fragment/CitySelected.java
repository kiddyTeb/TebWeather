package com.liangdekai.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangdekai.bean.FutureWeatherInfo;
import com.liangdekai.db.WeatherDB;
import com.liangdekai.weather_liangdekai.R;

import java.util.List;

public class CitySelected extends Fragment {
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
    private WeatherDB mWeatherDB;
    private String mCityName;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.part_main_activity_show, container , false);
        initView();
        Bundle bundle = this.getArguments();
        if (bundle != null){
            mCityName = bundle.getString("cityName");
        }
        return view;
    }

    public static CitySelected newInstance(String cityName){
        CitySelected citySelected = new CitySelected();
        Bundle bundle = new Bundle();
        bundle.putString("cityName",cityName);
        citySelected.setArguments(bundle);
        return citySelected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("test","onCreateMain");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("test","onCreateViewMain");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(mCityName, Context.MODE_PRIVATE) ;
        if (!"".equals(preferences.getString("city","")) ){
            loadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("test","onDestoryMain");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("test","onPauseMain");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("test","onDestoryViewMain");
    }

    private void initView(){
        mTvCity = (TextView) view.findViewById(R.id.weather_tv_city);//初始化当日天气信息所需的控件
        mTvPublishTime = (TextView) view.findViewById(R.id.weather_tv_publish);
        mTvDate = (TextView) view.findViewById(R.id.weather_tv_time);
        mTvWind = (TextView) view.findViewById(R.id.weather_tv_wind);
        mTvTemperature = (TextView) view.findViewById(R.id.weather_tv_temperature);
        mTvWeather = (TextView) view.findViewById(R.id.weather_tv_weather);
        mTvDress = (TextView) view.findViewById(R.id.weather_tv_how_dress);
        mTvTravel = (TextView) view.findViewById(R.id.weather_tv_how_travel);
        mTvSport = (TextView) view.findViewById(R.id.weather_tv_how_sport);
        mTvUv = (TextView) view.findViewById(R.id.weather_tv_how_ray);
        mTvAdvice = (TextView) view.findViewById(R.id.weather_tv_suggestion);

        mTvFutureDayOne = (TextView) view.findViewById(R.id.weather_tv_day_one);//初始化未来六日天气信息所需的控件
        mIvFutureImageOne = (ImageView) view.findViewById(R.id.weather_iv_first);
        mTvFutureWeatherOne = (TextView) view.findViewById(R.id.weather_tv_future_weather_one);
        mTvFutureTempOne = (TextView) view.findViewById(R.id.weather_tv_future_temp_one);
        mTvFutureWindOne = (TextView) view.findViewById(R.id.weather_tv_future_wind_one);
        mTvFutureDayTwo = (TextView) view.findViewById(R.id.weather_tv_day_two);
        mIvFutureImageTwo = (ImageView) view.findViewById(R.id.weather_iv_second);
        mTvFutureWeatherTwo = (TextView) view.findViewById(R.id.weather_tv_future_weather_two);
        mTvFutureTempTwo = (TextView) view.findViewById(R.id.weather_tv_future_temp_two);
        mTvFutureWindTwo = (TextView) view.findViewById(R.id.weather_tv_future_wind_two);
        mTvFutureDayThree = (TextView) view.findViewById(R.id.weather_tv_day_three);
        mIvFutureImageThree = (ImageView) view.findViewById(R.id.weather_iv_third);
        mTvFutureWeatherThree = (TextView) view.findViewById(R.id.weather_tv_future_weather_three);
        mTvFutureTempThree = (TextView) view.findViewById(R.id.weather_tv_future_temp_three);
        mTvFutureWindThree = (TextView) view.findViewById(R.id.weather_tv_future_wind_three);
        mTvFutureDayForth = (TextView) view.findViewById(R.id.weather_tv_day_forth);
        mIvFutureImageForth = (ImageView) view.findViewById(R.id.weather_iv_forth);
        mTvFutureWeatherForth = (TextView) view.findViewById(R.id.weather_tv_future_weather_forth);
        mTvFutureTempForth = (TextView) view.findViewById(R.id.weather_tv_future_temp_forth);
        mTvFutureWindForth = (TextView) view.findViewById(R.id.weather_tv_future_wind_forth);
        mTvFutureDayFifth = (TextView) view.findViewById(R.id.weather_tv_day_fifth);
        mIvFutureImageFifth = (ImageView) view.findViewById(R.id.weather_iv_fifth);
        mTvFutureWeatherFifth = (TextView) view.findViewById(R.id.weather_tv_future_weather_fifth);
        mTvFutureTempFifth = (TextView) view.findViewById(R.id.weather_tv_future_temp_fifth);
        mTvFutureWindFifth = (TextView) view.findViewById(R.id.weather_tv_future_wind_fifth);
        mTvFutureDaySix = (TextView) view.findViewById(R.id.weather_tv_day_six);
        mIvFutureImageSix = (ImageView) view.findViewById(R.id.weather_iv_six);
        mTvFutureWeatherSix = (TextView) view.findViewById(R.id.weather_tv_future_weather_six);
        mTvFutureTempSix = (TextView) view.findViewById(R.id.weather_tv_future_temp_six);
        mTvFutureWindSix = (TextView) view.findViewById(R.id.weather_tv_future_wind_six);

        mWeatherDB = WeatherDB.getInstance(getActivity());
    }

    public void loadData(){
        List<FutureWeatherInfo> weatherList = mWeatherDB.loadFutureWeather(mCityName);
        SharedPreferences preferences = getActivity().getSharedPreferences(mCityName , Context.MODE_PRIVATE) ;
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

        mTvFutureDayOne.setText(weatherList.get(0).getWeek());
        changeImage(mIvFutureImageOne, weatherList.get(0).getId());
        mTvFutureWeatherOne.setText(weatherList.get(0).getWeather());
        mTvFutureTempOne.setText(weatherList.get(0).getTemperature());
        mTvFutureWindOne.setText(weatherList.get(0).getWind());
        mTvFutureDayTwo.setText(weatherList.get(1).getWeek());
        changeImage(mIvFutureImageTwo, weatherList.get(1).getId());
        mTvFutureWeatherTwo.setText(weatherList.get(1).getWeather());
        mTvFutureTempTwo.setText(weatherList.get(1).getTemperature());
        mTvFutureWindTwo.setText(weatherList.get(1).getWind());
        mTvFutureDayThree.setText(weatherList.get(2).getWeek());
        changeImage(mIvFutureImageThree, weatherList.get(2).getId());
        mTvFutureWeatherThree.setText(weatherList.get(2).getWeather());
        mTvFutureTempThree.setText(weatherList.get(2).getTemperature());
        mTvFutureWindThree.setText(weatherList.get(2).getWind());
        mTvFutureDayForth.setText(weatherList.get(3).getWeek());
        changeImage(mIvFutureImageForth, weatherList.get(3).getId());
        mTvFutureWeatherForth.setText(weatherList.get(3).getWeather());
        mTvFutureTempForth.setText(weatherList.get(3).getTemperature());
        mTvFutureWindForth.setText(weatherList.get(3).getWind());
        mTvFutureDayFifth.setText(weatherList.get(4).getWeek());
        changeImage(mIvFutureImageFifth, weatherList.get(4).getId());
        mTvFutureWeatherFifth.setText(weatherList.get(4).getWeather());
        mTvFutureTempFifth.setText(weatherList.get(4).getTemperature());
        mTvFutureWindFifth.setText(weatherList.get(4).getWind());
        mTvFutureDaySix.setText(weatherList.get(5).getWeek());
        changeImage(mIvFutureImageSix, weatherList.get(5).getId());
        mTvFutureWeatherSix.setText(weatherList.get(5).getWeather());
        mTvFutureTempSix.setText(weatherList.get(5).getTemperature());
        mTvFutureWindSix.setText(weatherList.get(5).getWind());
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
