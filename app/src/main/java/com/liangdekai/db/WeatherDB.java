package com.liangdekai.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liangdekai.bean.CityInfo;
import com.liangdekai.bean.FutureWeatherInfo;
import com.liangdekai.bean.TodayInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对数据库的一些操作方法进行封装
 */
public class WeatherDB  {
    public static final String DB_NAME = "weather";//数据库名字
    public static final int VERSION = 1;//数据库版本
    private SQLiteDatabase mDatabase;
    private static WeatherDB weatherDB ;

    private WeatherDB(Context context){
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context , DB_NAME , null , VERSION);
        mDatabase = dbOpenHelper.getWritableDatabase();
    }

    public synchronized static WeatherDB getInstance(Context context){
        if (weatherDB == null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB ;
    }

    /**
     * 保存省份信息
     * @param provinceName
     */
    public void saveProvince(String provinceName){
        ContentValues values = new ContentValues();//使用ContentValues对要添加的数据进行组装
        if (provinceName !=null){
            values.put("provinceName", provinceName);
            mDatabase.insert("Province",null,values);//插入数据
        }
    }

    /**
     * 加载省份信息
     * @return provinceBeanList
     */
    public List<String> loadProvince(){
        List<String> provinceNameList = new ArrayList<String>();
        Cursor cursor = mDatabase.query("Province",null,null,null,null,null,null);//根据表名查询表中全部数据
        if (cursor.moveToFirst()){
            do {
                String provinceName = cursor.getString(cursor.getColumnIndexOrThrow("provinceName"));
                provinceNameList.add(provinceName);
            }while(cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return provinceNameList;
    }

    /**
     * 保存城市数据
     * @param cityInfo
     */
    public void saveCity(CityInfo cityInfo){
        ContentValues values = new ContentValues();//使用ContentValues对要添加的数据进行组装
        if(cityInfo !=null){
            values.put("cityName", cityInfo.getCity());
            values.put("provinceName", cityInfo.getProvince());
            mDatabase.insert("City",null,values);//插入数据库当中
        }
    }

    /**
     * 加载城市数据
     * @param provinceName
     * @return cityBeanList
     */
    public List<String> loadCity(String provinceName){
        List<String> cityInfoList = new ArrayList<String>();
        Cursor cursor = mDatabase.query("City",null,"provinceName = ?",new String[]{provinceName},null,null,null);//根据表名查询表中符合省份名字的城市数据
        if(cursor.moveToFirst()){
            do {
                String cityName = cursor.getString(cursor.getColumnIndexOrThrow("cityName"));
                cityInfoList.add(cityName);
            }while(cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return cityInfoList;
    }

    /**
     * 加载所有城市，用于判断
     * @return cityBeanList
     */
    public List<String> loadAllCity(){
        List<String> cityNameList = new ArrayList<String>();
        Cursor cursor = mDatabase.query("City",null,null,null,null,null,null);//根据表名查询表中符合省份名字的城市数据
        if(cursor.moveToFirst()){
            do {
                String cityName = cursor.getString(cursor.getColumnIndexOrThrow("cityName"));
                cityNameList.add(cityName);
            }while(cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return cityNameList;
    }

    /**
     * 保存未来六日天气的信息
     * @param weatherList
     */
    public void saveFutureWeather(List<FutureWeatherInfo> weatherList){
        ContentValues values = new ContentValues();//使用ContentValues对要添加的数据进行组装
        for(int i = 0;i<weatherList.size();i++) {
            values.put("city", weatherList.get(i).getCity());
            values.put("week", weatherList.get(i).getWeek());
            values.put("weather", weatherList.get(i).getWeather());
            values.put("temperature", weatherList.get(i).getTemperature());
            values.put("wind", weatherList.get(i).getWind());
            values.put("weatherId" , weatherList.get(i).getWeatherId().getWeatherId());
            mDatabase.insert("Weather", null, values);//插入数据
        }
    }

    /**
     * 加载未来六日天气信息
     * @return weatherList
     */
    public List<FutureWeatherInfo> loadFutureWeather(String cityName){
        List<FutureWeatherInfo> weatherList = new ArrayList<FutureWeatherInfo>();
        Cursor cursor = mDatabase.query("Weather",null,"city = ?",new String[]{cityName},null,null,null);//根据表名查询表中全部数据
        if (cursor.moveToFirst()){
            do {
                String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                String week = cursor.getString(cursor.getColumnIndexOrThrow("week"));
                String weather = cursor.getString((cursor.getColumnIndexOrThrow("weather")));
                String temperature = cursor.getString(cursor.getColumnIndexOrThrow("temperature"));
                String wind = cursor.getString(cursor.getColumnIndexOrThrow("wind"));
                String weatherId = cursor.getString(cursor.getColumnIndexOrThrow("weatherId"));
                weatherList.add(new FutureWeatherInfo(city ,week,weather,temperature,wind,weatherId));
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return weatherList;
    }

    /**
     * 删除城市联动删除数据库中未来的天气中全部数据
     */
    public void deleteWeather(String cityName){
        mDatabase.delete("Weather","city = ?",new String[]{cityName});
    }

    /**
     * 保存当日天气的信息
     * @param context
     * @param todayInfo
     */
    public void saveWeather(Context context, TodayInfo todayInfo , boolean flag) {
        SharedPreferences.Editor editor ;
        if (flag){//根据需要选择存储文件位置
            editor = context.getSharedPreferences(todayInfo.getCity() , Context.MODE_PRIVATE).edit();
        }else {
            editor = context.getSharedPreferences("data" , Context.MODE_PRIVATE).edit();
        }
        editor.putString("city", todayInfo.getCity());
        editor.putString("time",todayInfo.getWeek());
        editor.putString("date_y", todayInfo.getDate());
        editor.putString("wind", todayInfo.getWind());
        editor.putString("weather", todayInfo.getWeather());
        editor.putString("temperature", todayInfo.getTemperature());
        editor.putString("dressing_index", todayInfo.getDress());
        editor.putString("travel_index", todayInfo.getTravel());
        editor.putString("exercise_index", todayInfo.getSport());
        editor.putString("uv_index", todayInfo.getRay());
        editor.putString("dressing_advice", todayInfo.getAdvice());
        editor.apply();
    }

    /**
     * 保存常用城市
     */
    public void saveCommonCity(Context context , String cityName){
        SharedPreferences.Editor editor = context.getSharedPreferences("common" , Context.MODE_APPEND).edit();
        editor.putString(cityName , cityName);
        editor.apply();
    }

    /**
     * 加载所有常用城市
     */
    public List<String> loadCommonCity(Context context){
        List<String> cityList = new ArrayList<String>();
        SharedPreferences preferences = context.getSharedPreferences("common" , Context.MODE_APPEND) ;
        Map<String, ?> map = preferences.getAll();
        for (Map.Entry<String , ? > entry : map.entrySet()){//从文件中拿出全部信息，利用MAO循环读取所有信息放进容器当中
            cityList.add(entry.getValue()+"");
        }
        return cityList;
    }

    /**
     * 删除常用城市
     */
    public void deleteCommonCity(Context context , String cityName){
        SharedPreferences preferences = context.getSharedPreferences("common" , Context.MODE_APPEND) ;
        SharedPreferences.Editor editor= preferences.edit();
        editor.remove(cityName);
        editor.apply();
    }
}
