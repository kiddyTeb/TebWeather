package com.liangdekai.util;

import android.content.Context;
import com.liangdekai.bean.CityBean;
import com.liangdekai.bean.FutureWeatherBean;
import com.liangdekai.bean.ProvinceBean;
import com.liangdekai.bean.TrainBean;
import com.liangdekai.bean.WeatherBean;
import com.liangdekai.db.WeatherDbOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 对JSON格式数据进行解析，并将结果进行保存
 */
public class HandleResponseUtil {
    /**
     * 对城市的JSON数据进行解析保存
     * @param weatherDbOpenHelper
     * @param result
     * @return boolean
     */
    public static boolean handleCityResponse(WeatherDbOpenHelper weatherDbOpenHelper, String result) {
        ProvinceBean provinceBean = new ProvinceBean();
        CityBean cityBean = new CityBean();
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                result=jsonObject.getString("result");
                JSONArray jsonArray = new JSONArray(result);
                String provinceName = "";
                for(int i = 0;i< jsonArray.length();i++) {
                    JSONObject resultMessage = jsonArray.getJSONObject(i);
                    if(!provinceName.equals(resultMessage.getString("province"))) {//判断省份是否重复
                        provinceName = resultMessage.getString("province");
                        provinceBean.setProvinceName(provinceName);//将省份封装成对象
                        weatherDbOpenHelper.saveProvince(provinceBean);//插入数据库
                    }
                    cityBean.setCityId(resultMessage.getString("id"));//将城市信息封装成对象
                    cityBean.setCityName(resultMessage.getString("district"));
                    cityBean.setProvinceName(resultMessage.getString("province"));
                    weatherDbOpenHelper.saveCity(cityBean);//插入数据库
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 对天气的信息进行解析保存
     * @param context
     * @param weatherDbOpenHelper
     * @param result
     * @return boolean
     */
    public static boolean handleWeatherResponse(Context context,WeatherDbOpenHelper weatherDbOpenHelper,String result){
        WeatherBean weatherBean = new WeatherBean();
        List<FutureWeatherBean> weatherList = new ArrayList<FutureWeatherBean>();
        if(result!=null){
            try{
                JSONObject jsonObject = new JSONObject(result);
                result = jsonObject.getString("result");
                JSONObject json = new JSONObject(result);//将结果传入对象中
                String publishTime = json.getString("sk");//通过对象获取细节数据
                JSONObject time = new JSONObject(publishTime);
                result = json.getString("today");
                JSONObject resultMessage = new JSONObject(result);//将当日的数据传入新的对象中
                String nowDate = resultMessage.getString("date_y");//通过对象获取细节数据，并封装成WeatherBean对象
                weatherBean.setCityName(resultMessage.getString("city"));
                weatherBean.setPublishTime(time.getString("time"));
                weatherBean.setTodayDate(resultMessage.getString("date_y"));
                weatherBean.setWind(resultMessage.getString("wind"));
                weatherBean.setWeather(resultMessage.getString("weather"));
                weatherBean.setTemperature(resultMessage.getString("temperature"));
                weatherBean.setDress(resultMessage.getString("dressing_index"));
                weatherBean.setTravel(resultMessage.getString("travel_index"));
                weatherBean.setSport(resultMessage.getString("exercise_index"));
                weatherBean.setUv(resultMessage.getString("uv_index"));
                weatherBean.setSuggestion(resultMessage.getString("dressing_advice"));
                weatherDbOpenHelper.saveWeather(context,weatherBean);//对该对象进行保存

                String future = json.getString("future");
                int yearIndex = nowDate.indexOf("年");//对日期进行解析
                int monthIndex = nowDate.indexOf("月");
                int dayIndex = nowDate.indexOf("日");
                String year = nowDate.substring(0,yearIndex);
                String month = nowDate.substring(yearIndex+1,monthIndex);
                String day = nowDate.substring(monthIndex+1,dayIndex);
                String date = year+month+day;//将当日日期数字化
                date = NextDayUtil.calculateNextDay(date);//计算下一天的日期
                JSONObject futrueMessage = new JSONObject(future);
                for(int i = 0 ; i<6 ;i++){
                    String information = futrueMessage.getString("day_"+date);
                    JSONObject weather = new JSONObject(information);
                    String category = weather.getString("weather_id");
                    JSONObject weatherId = new JSONObject(category);
                    weatherList.add(new FutureWeatherBean(weather.getString("week"),weather.getString("weather"),//将对象封装到容器中
                            weather.getString("temperature"),weather.getString("wind"),weatherId.getString("fa")));
                    date = NextDayUtil.calculateNextDay(date);//计算下一天的日期
                }
                weatherDbOpenHelper.saveFutureWeather(weatherList);//保存数据到数据库
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理火车班次结果数据
     * @param result
     * @return trainBeanList
     */
    public static List<TrainBean> handleTrainResponse(String result){
        List<TrainBean> trainBeanList = new ArrayList<TrainBean>();
        if (result != null){
            try{
                JSONObject jsonObject = new JSONObject(result);
                result = jsonObject.getString("result");
                JSONObject json = new JSONObject(result);
                result = json.getString("data");
                JSONArray jsonArray = new JSONArray(result);
                for(int i = 0;i< jsonArray.length();i++) {
                    JSONObject resultMessage = jsonArray.getJSONObject(i);
                    String startStation = resultMessage.getString("start_staion");
                    String startTime = resultMessage.getString("leave_time");
                    String stopStation = resultMessage.getString("end_station");
                    String stopTime = resultMessage.getString("arrived_time");
                    String trainOpp = resultMessage.getString("trainOpp");
                    trainBeanList.add(new TrainBean(trainOpp,startStation,startTime,stopStation,stopTime));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return trainBeanList;
    }
}
