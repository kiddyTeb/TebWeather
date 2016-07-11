package com.liangdekai.bean;

public class TrainBean {
    private String trainOpp;
    private String startStation;
    private String startTime;
    private String stopStation;
    private String stopTime;

    public String getTrainOpp() {
        return trainOpp;
    }

    public void setTrainOpp(String trainOpp) {
        this.trainOpp = trainOpp;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopStation() {
        return stopStation;
    }

    public void setStopStation(String stopStation) {
        this.stopStation = stopStation;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public TrainBean (String trainOpp,String startStation,String startTime,String stopStation,String stopTime){
        this.trainOpp = trainOpp;
        this.startStation = startStation;
        this.startTime = startTime;
        this.stopStation = stopStation;
        this.stopTime = stopTime;
    }

}
