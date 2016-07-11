package com.liangdekai.util;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 获取定位位置
 */
public class LocationUtil {
    private static final int MAX_TRY_COUNT = 3;//最大尝试次数
    public LocationClient mLocationClient = null;
    public BDLocationListener mBdLocation= new MyLocationListener();
    private LocationListener myListener;
    private int mTryCount;//当前尝试次数

    public interface LocationListener {
        void succeed(String latitude,String longtitude);
        void failed();
    }

    public LocationUtil(LocationListener mylistener) {
        this.myListener = mylistener;
        mLocationClient = new LocationClient(MyApplication.getContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(mBdLocation);    //注册监听函数
        initLocation();//对定位进行菜单选择调配
        startLocation();
    }

    /**
     * 配置定位SDK各种参数
     */
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        mLocationClient.start();
        mTryCount = 0;
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        mLocationClient.stop();
        mTryCount = 0;
    }

    /**
     * 对定位情况进行监听
     */
    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {//接受定位对象
            if (bdLocation.getLocType() != 161 ){//定位失败
                mTryCount++;//尝试次数增加
                if(mTryCount >= MAX_TRY_COUNT){//达到3次时，停止定位
                    myListener.failed();
                    stopLocation();
                }
                return;
            }
            String latitude = bdLocation.getLatitude()+"";//获取纬度
            String longtitude = bdLocation.getLongitude()+"";//获取经度
            myListener.succeed(latitude,longtitude);
            stopLocation();
        }
    }
}
