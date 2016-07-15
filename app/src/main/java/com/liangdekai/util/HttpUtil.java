package com.liangdekai.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 发送HTTP请求
 */
public class HttpUtil {

    public static void sendHttpResquest(final String address ,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();//获取HttpURLConnection实例
                    connection.setRequestMethod("GET");//设置从服务器中获取数据
                    connection.setConnectTimeout(10000);//设置连接超时未10S
                    connection.setReadTimeout(10000);//设置读取超时未10S
                    InputStream inputStream = connection.getInputStream();//获取到服务器返回的输入流
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();//对输入流进行读取
                    String line;
                    while((line = bufferedReader.readLine())!=null){
                        result.append(line);
                    }
                    if(listener!=null)
                        listener.onFinish(result.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    if(listener != null){
                        listener.onError(e);
                    }
                }
                finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
