package com.liangdekai.util;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by asus on 2016/7/16.
 */
public class ActivityCollectUtil {
    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void remoteActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity :activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
