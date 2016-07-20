package com.liangdekai.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asus on 2016/7/20.
 */
public class CityInfo {
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String province ;

    @SerializedName("district")
    private String city ;

    public CityInfo(String province ,String city){
        this.province = province ;
        this.city = city ;
    }
}
