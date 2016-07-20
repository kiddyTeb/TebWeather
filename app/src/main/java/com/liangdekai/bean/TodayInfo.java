package com.liangdekai.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asus on 2016/7/20.
 */
public class TodayInfo {
    private String city ;
    @SerializedName("date_y")

    private String date ;
    private String wind ;
    private String week ;
    private String weather ;
    private String temperature ;

    @SerializedName("dressing_index")
    private String dress ;

    @SerializedName("travel_index")
    private String travel ;

    @SerializedName("exercise_index")
    private String sport ;

    @SerializedName("uv_index")
    private String ray ;

    @SerializedName("dressing_advice")
    private String advice ;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDress() {
        return dress;
    }

    public void setDress(String dress) {
        this.dress = dress;
    }

    public String getTravel() {
        return travel;
    }

    public void setTravel(String travel) {
        this.travel = travel;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getRay() {
        return ray;
    }

    public void setRay(String ray) {
        this.ray = ray;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }
}
