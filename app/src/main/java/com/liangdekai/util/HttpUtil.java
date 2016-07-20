package com.liangdekai.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 发送HTTP请求
 */
public class HttpUtil {

        public static void sendByConnection( String address ,  HttpCallbackListener listener){
                HttpURLConnection connection = null ;
                InputStream inputStream = null ;
                BufferedReader bufferedReader = null ;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();//获取HttpURLConnection实例
                    connection.setRequestMethod("GET");//设置从服务器中获取数据
                    connection.setConnectTimeout(10000);//设置连接超时未10S
                    connection.setReadTimeout(10000);//设置读取超时未10S
                    inputStream = connection.getInputStream();//获取到服务器返回的输入流
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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
                    try{
                        if (inputStream != null){
                            inputStream.close();
                        }
                        if (bufferedReader != null){
                            bufferedReader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(connection!=null){
                        connection.disconnect();
                    }
                }

        }

        public static void sendByClient (final String address , final HttpCallbackListener listener){
                try{
                    String result = null;
                    HttpClient httpClient = new DefaultHttpClient();//获取实例
                    HttpGet httpGet = new HttpGet(address);//创建HttpGet对象，传入网络地址
                    HttpResponse httpResponse = null;//IOException
                    httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200){
                        HttpEntity httpEntity = httpResponse.getEntity();//获取HttpEntity实例
                        result = EntityUtils.toString(httpEntity,"utf-8");//转换为字符串
                    }
                    if(listener!=null)
                        listener.onFinish(result.toString());
                }catch (IOException e){
                    e.printStackTrace();
                    if(listener != null){
                        listener.onError(e);
                    }
                }
        }
}
