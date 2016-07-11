package com.liangdekai.bean;

public class FutureWeatherBean {
    private String week;
    private String weatherId;
    private String weather;
    private String temperature;
    private String wind;

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

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public FutureWeatherBean(String futureDay,String futureWeather,String futureTemperature,String futureWind,String weatherId){
        this.week = futureDay;
        this.weather = futureWeather;
        this.temperature = futureTemperature;
        this.wind = futureWind;
        this.weatherId = weatherId;
    }
}
