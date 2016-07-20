package com.liangdekai.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asus on 2016/7/20.
 */
public class WeatherInfo {
    @SerializedName("today")
    private TodayInfo todayInfo ;
    @SerializedName("future")
    private List<FutureWeatherInfo> futureWeatherInfoList ;

    public TodayInfo getTodayInfo() {
        return todayInfo;
    }

    public void setTodayInfo(TodayInfo todayInfo) {
        this.todayInfo = todayInfo;
    }

    public List<FutureWeatherInfo> getFutureWeatherInfoList() {
        return futureWeatherInfoList;
    }

    public void setFutureWeatherInfoList(List<FutureWeatherInfo> futureWeatherInfoList) {
        this.futureWeatherInfoList = futureWeatherInfoList;
    }
}
