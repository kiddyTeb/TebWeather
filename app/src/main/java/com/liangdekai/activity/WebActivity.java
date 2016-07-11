package com.liangdekai.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.liangdekai.weather_liangdekai.R;

/**
 * 用于开启一个网页
 */
public class WebActivity extends Activity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = (WebView) findViewById(R.id.web_wv_web);//获取WebView实例
        webView.getSettings().setJavaScriptEnabled(true);//让WebView支持JavaScript脚本
        webView.setWebChromeClient(new WebChromeClient());//网页的跳转仍旧停留在WebView进行操作
        webView.loadUrl("http://www.12306.cn/mormhweb/");//传入网址并显示
    }
}
