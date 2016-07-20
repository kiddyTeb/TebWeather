package com.liangdekai.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asus on 2016/7/20.
 */
public class JsonWeather {
    private String resultcode ;

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    @SerializedName("result")

    private WeatherInfo weatherInfo ;
}
