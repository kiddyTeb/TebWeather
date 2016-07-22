package com.liangdekai.bean;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asus on 2016/7/20.
 */
public class FutureWeatherInfo{
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String temperature ;
    private String weather ;
    private String wind ;
    @SerializedName("weather_id")
    private WeatherId weatherId ;

    public WeatherId getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(WeatherId weatherId) {
        this.weatherId = weatherId;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    private String week ;
    private String id ;

    public FutureWeatherInfo(String city ,String week , String weather ,String temperature , String wind ,String id){
        this.city = city ;
        this.week = week ;
        this.weather = weather ;
        this.temperature = temperature ;
        this.wind = wind ;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
