package com.liangdekai.util;

import android.content.Context;

import com.google.gson.Gson;
import com.liangdekai.bean.CityInfo;
import com.liangdekai.bean.FutureWeatherInfo;
import com.liangdekai.bean.JsonCity;
import com.liangdekai.bean.JsonWeather;
import com.liangdekai.bean.TodayInfo;
import com.liangdekai.bean.WeatherId;
import com.liangdekai.bean.WeatherInfo;
import com.liangdekai.db.WeatherDbOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 对JSON格式数据进行解析，并将结果进行保存
 */
public class HandleResponseUtil {
    /**
     * 对城市的JSON数据进行解析保存
     * @param weatherDbOpenHelper
     * @param result
     * @return boolean
     */
    public static boolean praseCityResponse(WeatherDbOpenHelper weatherDbOpenHelper, String result){
        Gson gson = new Gson();
        String provinceName = "";
        JsonCity jsonCity = gson.fromJson(result , JsonCity.class);
        List<CityInfo> cityInfoList = jsonCity.getCityInfoList();
        for (int i = 0 ; i < cityInfoList.size() ; i++){
            CityInfo cityInfo = cityInfoList.get(i);
            if (!provinceName.equals(cityInfo.getProvince())){
                weatherDbOpenHelper.saveProvince(cityInfo.getProvince());
                provinceName = cityInfo.getProvince();
            }
            weatherDbOpenHelper.saveCity(cityInfo);
        }
        return true;
    }

    /**
     * 对天气的信息进行解析保存
     * @param context
     * @param weatherDbOpenHelper
     * @param result
     * @return boolean
     */
    public static boolean praseWeatherResponse(Context context , WeatherDbOpenHelper weatherDbOpenHelper , String result){
        Gson gson = new Gson();
        JsonWeather jsonWeather = gson.fromJson(result , JsonWeather.class);
        WeatherInfo weatherInfo = jsonWeather.getWeatherInfo();
        TodayInfo todayInfo = weatherInfo.getTodayInfo();
        weatherDbOpenHelper.saveWeather(context , todayInfo);

        List<FutureWeatherInfo> futureWeatherInfoList = weatherInfo.getFutureWeatherInfoList();
        List<FutureWeatherInfo> futureWeatherList = new ArrayList<FutureWeatherInfo>();
        for (int i = 0 ; i < futureWeatherInfoList.size() ; i++){
            FutureWeatherInfo futureWeatherInfo = futureWeatherInfoList.get(i);
            WeatherId weather = futureWeatherInfoList.get(i).getWeatherId();
            futureWeatherInfo.setId(weather.getWeatherId());
            futureWeatherList.add(futureWeatherInfo);
        }
        weatherDbOpenHelper.saveFutureWeather(futureWeatherList);
        return true;
    }

}
