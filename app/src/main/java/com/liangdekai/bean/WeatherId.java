package com.liangdekai.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asus on 2016/7/20.
 */
public class WeatherId {
    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    @SerializedName("fa")
    private String weatherId;
}
