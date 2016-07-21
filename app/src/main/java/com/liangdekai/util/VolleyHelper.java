package com.liangdekai.util;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.liangdekai.MyApplication;

import org.json.JSONObject;


/**
 * Created by asus on 2016/7/19.
 */
public class VolleyHelper {
    public static String result = null;
    public static boolean flag = true;

    public static String sendByVolley(String address) {
        //RequestQueue requestQueue = Volley.newRequestQueue(MyApplication.getContext(), null);
        RequestQueue requestQueue = MyApplication.getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(address, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        result = jsonObject.toString();
                        flag =false ;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("test",volleyError.getMessage());
                flag =false ;
            }
        });
        requestQueue.add(jsonObjectRequest);
        while (flag){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        flag =true;
        return result;
    }

}
