package com.liangdekai.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by asus on 2016/7/20.
 */
public class JsonCity {
    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public List<CityInfo> getCityInfoList() {
        return cityInfoList;
    }

    public void setCityInfoList(List<CityInfo> cityInfoList) {
        this.cityInfoList = cityInfoList;
    }

    private String resultcode ;

    @SerializedName("result")
    private List<CityInfo> cityInfoList ;
}
