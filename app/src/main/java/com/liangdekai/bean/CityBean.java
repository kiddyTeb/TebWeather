package com.liangdekai.bean;

public class CityBean {
    private String cityId;
    private String cityName;
    private String provinceName;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public CityBean(String cityId, String cityName){
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public CityBean(){
    }
}
