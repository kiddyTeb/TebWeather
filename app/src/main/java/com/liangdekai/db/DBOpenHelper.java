package com.liangdekai.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 负责创建数据库
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String CREATE_PROVINCE="create table Province(provinceName text)";//创建数据库的语句
    public static final String CREATE_CITY = "create table City(cityId integer primary key autoincrement,"
            +"cityName text, provinceName text)";
    public static final String CREATE_WEATHER = "create table Weather(city text,week text,weather text,temperature text,wind text,weatherId text)";

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);//创建数据库
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_WEATHER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
