package com.liangdekai.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.liangdekai.Fragment.ProgressDialogFragment;
import com.liangdekai.adapter.TrainAdapter;
import com.liangdekai.bean.CityBean;
import com.liangdekai.bean.TrainBean;
import com.liangdekai.db.WeatherDbOpenHelper;
import com.liangdekai.util.HandleResponseUtil;
import com.liangdekai.weather_liangdekai.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TravelActivity extends Activity implements View.OnClickListener {
    private List<TrainBean> mTrainList = new ArrayList<TrainBean>();
    private ProgressDialogFragment mProgressDialogFragment = new ProgressDialogFragment();
    private List<CityBean> mCityList = new ArrayList<CityBean>();
    private WeatherDbOpenHelper mWeatherDbOpenHelper;
    private EditText mEtStart;
    private EditText mEtStop;
    private Button mBtFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
        setContentView(R.layout.activity_travel);//加载布局
        mWeatherDbOpenHelper = new WeatherDbOpenHelper(this);
        initializeTrain();//初始化控件
    }

    /**
     * 初始化组件
     */
    private void initializeTrain(){
        mEtStart = (EditText) findViewById(R.id.weather_et_start);
        mEtStop = (EditText) findViewById(R.id.weather_et_stop);
        mBtFind = (Button) findViewById(R.id.weather_bt_find);
        mBtFind.setOnClickListener(this);
    }

    /**
     * 创建适配器配置数据
     */
    private void loadData(){
        TrainAdapter trainAdapter = new TrainAdapter(TravelActivity.this,R.layout.adapter_train,mTrainList);//创建自定义适配器对象
        ListView listView = (ListView) findViewById(R.id.weather_lv_train);
        listView.setAdapter(trainAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置点击事件
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TravelActivity.this,WebActivity.class);//启动网页
                startActivity(intent);
            }
        });
    }

    /**
     * 查询无效提示
     */
    private void error(){
        mProgressDialogFragment.dismiss();
        Toast.makeText(this,"两站之间的班次尚未开通或者当日无线路开通",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.weather_bt_find :
                boolean flag = true;
                boolean start = false;//初始站是否合法
                boolean stop = false;//终点站是否合法
                String startStation = mEtStart.getText().toString();//获取初始站城市名称
                String stopStation = mEtStop.getText().toString();//获取终点站城市名称
                if (startStation.equals("")||stopStation.equals("")) {
                    Toast.makeText(TravelActivity.this,"请不要输入空值",Toast.LENGTH_LONG).show();//空值判断并提示
                }else {
                    if (mCityList.size() == 0){
                        mCityList = mWeatherDbOpenHelper.loadAllCity();//若容器为空，则到数据库中获取
                    }
                    for (int i = 0 ; i<mCityList.size() ; i++){
                        if (startStation.equals(mCityList.get(i).getCityName())){//初始站存在此城市
                            start = true;
                        }
                        if (stopStation.equals(mCityList.get(i).getCityName())){//终点站存在此城市
                            stop = true;
                        }
                    }
                    if (start && stop){//同时合法，则进行查询
                        search(startStation, stopStation);
                        flag = false;
                    }
                    if (flag){
                        Toast.makeText(TravelActivity.this,"请输入合法的城市名字",Toast.LENGTH_LONG).show();//否则进行对用户提示
                    }
                }
                break;
        }
    }

    /**
     * 根据初始站和终点站对火车班次进行查询
     * @param startStation
     * @param stopStation
     */
    private void search(String startStation,String stopStation) {

        String startUTF = null;
        String stopUTF = null;
        try {
            startUTF = URLEncoder.encode(startStation,"UTF-8");//进行URL编码
            stopUTF = URLEncoder.encode(stopStation,"UTF-8");//进行URL编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();//不支持编码异常，说明字符编码存在问题
        }
        String address = "http://apis.juhe.cn/train/s2s?start="+startUTF+"&end="+stopUTF+"&traintype=&dtype=&key=22b1a0d31babe65e4e7463985a48888f";
        new MyAsyncTask().execute(address);//启动查询火车班次任务
    }

    class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressDialogFragment.show(getFragmentManager(), "progressDialog");
        }

        @Override
        protected String doInBackground(String... address) {
            String result ;
            try {
                HttpClient httpClient = new DefaultHttpClient();//获取实例
                HttpGet httpGet = new HttpGet(address[0]);//创建HttpGet对象，传入网络地址
                HttpResponse httpResponse = httpClient.execute(httpGet);//IOException
                if (httpResponse.getStatusLine().getStatusCode() == 200){
                    HttpEntity httpEntity = httpResponse.getEntity();//获取HttpEntity实例
                    result = EntityUtils.toString(httpEntity,"utf-8");//转换为字符串
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                mTrainList = HandleResponseUtil.handleTrainResponse(result);//若不为空，处理结果
                loadData();//加载数据
                mProgressDialogFragment.dismiss();
            }else {
                error();//查询无效提示
            }
        }
    }
}
