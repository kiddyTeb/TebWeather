package com.liangdekai.util;

/**
 * 计算下一天日期
 */
public class NextDayUtil {

    public static String calculateNextDay(String date){
        int year = Integer.valueOf(date.substring(0,4));
        int month = Integer.valueOf(date.substring(4,6));
        int day = Integer.valueOf(date.substring(6,8));
        int compareDay = 0;
        String newMonth;
        String newDay;
        boolean flag = true;
        if (year%4==0&&year%100!=0||year%400==0 &&month == 2){//判断是否为闰年来设置比较的日期长短
            compareDay = 29;
        }else{
            compareDay = 28;
        }
        switch (month){
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11: compareDay = 31;break;
            case 4:
            case 6:
            case 8:
            case 10:
            case 12: compareDay = 30;break;
        }

        if (month == 12 && day == 30){//如果是年末
            year = year +1;
            month = 1;
            day = 1;
            flag = false;
        }
        if (day < compareDay && flag){//如果不是月末
            day++;
        }else if(day == compareDay && flag){
            month++;
            day = 1;
        }
        if(month < 10) {//转换格式
            newMonth = "0"+String.valueOf(month);
        }else{
            newMonth = String.valueOf(month);
        }
        if(day < 10){
            newDay = "0"+String.valueOf(day);
        }else {
            newDay = String.valueOf(day);
        }
        String newYear = String.valueOf(year);
        date = newYear+newMonth+newDay;
        return date;//返回日期
    }

}
