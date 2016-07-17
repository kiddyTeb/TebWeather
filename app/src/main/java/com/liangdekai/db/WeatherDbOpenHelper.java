package com.liangdekai.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.liangdekai.bean.CityBean;
import com.liangdekai.bean.FutureWeatherBean;
import com.liangdekai.bean.ProvinceBean;
import com.liangdekai.bean.WeatherBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责创建数据库以及一些对数据库操作的方法
 */
public class WeatherDbOpenHelper extends SQLiteOpenHelper{

    public static final String CREATE_PROVINCE="create table Province(provinceName text)";//创建数据库的语句
    public static final String CREATE_CITY = "create table City(cityId integer primary key autoincrement,"
            +"cityName text, provinceName text)";
    public static final String CREATE_WEATHER = "create table Weather(week text,weather text,temperature text,wind text,weatherId text)";
    public static final String DB_NAME = "weather";//数据库名字
    public static final int VERSION = 1;//数据库版本
    private SQLiteDatabase mDatabase;

    public WeatherDbOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mDatabase = getWritableDatabase();//获取一个对数据库课进行读写操作的对象

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);//创建数据库
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 保存省份信息
     * @param provinceBean
     */
    public void saveProvince(ProvinceBean provinceBean){
        ContentValues values = new ContentValues();//使用ContentValues对要添加的数据进行组装
        if (provinceBean !=null){
            values.put("provinceName", provinceBean.getProvinceName());
            mDatabase.insert("Province",null,values);//插入数据
        }
    }

    /**
     * 加载省份信息
     * @return provinceBeanList
     */
    public List<ProvinceBean> loadProvince(){
        List<ProvinceBean> provinceBeanList = new ArrayList<ProvinceBean>();
        Cursor cursor = mDatabase.query("Province",null,null,null,null,null,null);//根据表名查询表中全部数据
        if (cursor.moveToFirst()){
            do {
                String provinceName = cursor.getString(cursor.getColumnIndexOrThrow("provinceName"));
                provinceBeanList.add(new ProvinceBean(provinceName));
            }while(cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return provinceBeanList;
    }

    /**
     * 保存城市数据
     * @param cityBean
     */
    public void saveCity(CityBean cityBean){
        ContentValues values = new ContentValues();//使用ContentValues对要添加的数据进行组装
        if(cityBean !=null){
            values.put("cityId", cityBean.getCityId());//存储城市数据
            values.put("cityName", cityBean.getCityName());
            values.put("provinceName", cityBean.getProvinceName());
            mDatabase.insert("City",null,values);//插入数据库当中
        }
    }

    /**
     * 加载城市数据
     * @param provinceName
     * @return cityBeanList
     */
    public List<CityBean> loadCity(String provinceName){
        List<CityBean> cityBeanList = new ArrayList<CityBean>();
        Cursor cursor = mDatabase.query("City",null,"provinceName = ?",new String[]{provinceName},null,null,null);//根据表名查询表中符合省份名字的城市数据
        if(cursor.moveToFirst()){
            do {
                String cityId = cursor.getString(cursor.getColumnIndexOrThrow("cityId"));
                String cityName = cursor.getString(cursor.getColumnIndexOrThrow("cityName"));
                cityBeanList.add(new CityBean(cityId,cityName));
            }while(cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return cityBeanList;
    }

    /**
     * 加载所有城市，用于判断
     * @return cityBeanList
     */
    public List<CityBean> loadAllCity(){
        List<CityBean> cityBeanList = new ArrayList<CityBean>();
        Cursor cursor = mDatabase.query("City",null,null,null,null,null,null);//根据表名查询表中符合省份名字的城市数据
        if(cursor.moveToFirst()){
            do {
                String cityId = cursor.getString(cursor.getColumnIndexOrThrow("cityId"));
                String cityName = cursor.getString(cursor.getColumnIndexOrThrow("cityName"));
                cityBeanList.add(new CityBean(cityId,cityName));
            }while(cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return cityBeanList;
    }

    /**
     * 保存未来六日天气的信息
     * @param weatherList
     */
    public void saveFutureWeather(List<FutureWeatherBean> weatherList){
        ContentValues values = new ContentValues();//使用ContentValues对要添加的数据进行组装
        deleteWeather();//保存新的一批天气信息之前，对原本数据进行删除
        for(int i = 0;i<weatherList.size();i++) {
            values.put("week", weatherList.get(i).getWeek());
            values.put("weather", weatherList.get(i).getWeather());
            values.put("temperature", weatherList.get(i).getTemperature());
            values.put("wind", weatherList.get(i).getWind());
            values.put("weatherId",weatherList.get(i).getWeatherId());
            mDatabase.insert("Weather", null, values);//插入数据
        }
    }

    /**
     * 加载未来六日天气信息
     * @return weatherList
     */
    public List<FutureWeatherBean> loadFutureWeather(){
        List<FutureWeatherBean> weatherList = new ArrayList<FutureWeatherBean>();
        Cursor cursor = mDatabase.query("Weather",null,null,null,null,null,null);//根据表名查询表中全部数据
        if (cursor.moveToFirst()){
            do {
                String week = cursor.getString(cursor.getColumnIndexOrThrow("week"));
                String weather = cursor.getString((cursor.getColumnIndexOrThrow("weather")));
                String temperature = cursor.getString(cursor.getColumnIndexOrThrow("temperature"));
                String wind = cursor.getString(cursor.getColumnIndexOrThrow("wind"));
                String weatherId = cursor.getString(cursor.getColumnIndexOrThrow("weatherId"));
                weatherList.add(new FutureWeatherBean(week,weather,temperature,wind,weatherId));
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return weatherList;
    }

    /**
     * 删除未来天气表中全部数据
     */
    public void deleteWeather(){
        Cursor cursor = mDatabase.query("Weather",null,null,null,null,null,null);
        if(cursor.getCount()!=0) {
           mDatabase.delete("Weather",null,null);
        }
        if (cursor != null){
            cursor.close();
        }
    }

    /**
     * 保存当日天气的信息
     * @param context
     * @param weatherBean
     */
    public void saveWeather(Context context,WeatherBean weatherBean) {
        //SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SharedPreferences.Editor editor = context.getSharedPreferences("data" , Context.MODE_PRIVATE).edit();
        editor.putString("city", weatherBean.getCityName());
        editor.putString("time",weatherBean.getPublishTime());
        editor.putString("date_y", weatherBean.getTodayDate());
        editor.putString("wind", weatherBean.getWind());
        editor.putString("weather", weatherBean.getWeather());
        editor.putString("temperature", weatherBean.getTemperature());
        editor.putString("dressing_index", weatherBean.getDress());
        editor.putString("travel_index", weatherBean.getTravel());
        editor.putString("exercise_index", weatherBean.getSport());
        editor.putString("uv_index", weatherBean.getUv());
        editor.putString("dressing_advice", weatherBean.getSuggestion());
        editor.apply();
    }

}
