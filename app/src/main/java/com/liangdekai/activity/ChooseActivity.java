package com.liangdekai.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.liangdekai.Fragment.ProgressFragment;
import com.liangdekai.adapter.CityAdapter;
import com.liangdekai.bean.CityBean;
import com.liangdekai.bean.ProvinceBean;
import com.liangdekai.util.HasNetUtil;
import com.liangdekai.util.HttpCallbackListener;
import com.liangdekai.util.HttpUtil;
import com.liangdekai.util.LocationUtil;
import com.liangdekai.weather_liangdekai.R;
import com.liangdekai.db.WeatherDbOpenHelper;
import com.liangdekai.util.HandleResponseUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择需要查询的天气的城市
 */
public class ChooseActivity extends Activity {
    public static final int SUCCESS_IN = 0;
    public static final int FAIL_OUT =1;
    private CityAdapter mCityAdapter;
    private List<String> mList = new ArrayList<String>();
    private List<ProvinceBean> mProvinceBeanList = new ArrayList<ProvinceBean>();
    private List<CityBean> mCityBeanList = new ArrayList<CityBean>();
    private WeatherDbOpenHelper mWeatherDbOpenHelper;
    private ListView mLvShowCity;
    private TextView mTvTitle;
    private Button mBtLocation;
    private SearchView mSvSearch;
    private String mLevel;
    private String mSelectedProvinceName;
    private boolean mFromWeatherActivity;
    private ProgressFragment mProgressFragment = new ProgressFragment();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS_IN :
                    mProgressFragment.dismiss();//取消进度条
                    searchProvince();//查询省份
                    break;
                case FAIL_OUT :
                    mProgressFragment.dismiss();//取消进度条
                    Toast.makeText(ChooseActivity.this, "网络请求失败...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);//从文件中查询是否已经存在已选择的城市天气信息
        mFromWeatherActivity = getIntent().getBooleanExtra("fromWeatherActivity",false);//判断是否从展示天气界面处跳转而来
        String selected = preferences.getString("city","");//获取文件中的城市名称，若没有，视为首次进行选择操作
        if(!mFromWeatherActivity && !selected.equals("")){//不从天气展示页面处跳转而来并且也有选择城市，则直接跳转到天气展示界面
            Intent intent = new Intent(ChooseActivity.this,WeatherActivity.class);
            intent.putExtra("backFromChooseActivity",true);//设置非第一次操作的标识
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_choose);
        mTvTitle = (TextView) findViewById(R.id.choose_tv_title);//将控件实例化
        mBtLocation = (Button) findViewById(R.id.choose_bt_location);
        mLvShowCity = (ListView) findViewById(R.id.choose_lv_list);
        mSvSearch = (SearchView) findViewById(R.id.choose_sv_search);
        mLvShowCity.setTextFilterEnabled(true);//设置ListView启动过滤
        mSvSearch.setIconifiedByDefault(false);//设置不自动缩小为图标
        mSvSearch.setSubmitButtonEnabled(true);//显示搜索按钮
        mSvSearch.setQueryHint("请输入查询的城市");//设置显示提示文本信息
        mCityAdapter = new CityAdapter(this,R.layout.adapter_city, mList);
        mLvShowCity.setAdapter(mCityAdapter);//建立ListView与数据的关联
        mWeatherDbOpenHelper = new WeatherDbOpenHelper(this);
        mLvShowCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置ListView事件监听
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mLevel.equals("province")) {
                    Log.d("test","ss");
                    mSelectedProvinceName = mProvinceBeanList.get(position).getProvinceName();//若点击相应省份，则显示相应城市
                    searchCity(mSelectedProvinceName);
                } else if (mLevel.equals("city")) {
                    //Intent intent = new Intent(ChooseActivity.this, WeatherActivity.class);
                    Intent intent = new Intent() ;
                    intent.putExtra("cityId", mCityBeanList.get(position).getCityId());//将点击的城市ID信息传递给另外一个活动
                    setResult(RESULT_OK , intent);
                    finish();//关闭当前活动
                }
            }
        });
        mSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {//设置搜索事件监听
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(ChooseActivity.this, WeatherActivity.class);
                try {
                    if (TextUtils.isEmpty(s)){//判断是否为空值
                        Toast.makeText(ChooseActivity.this, "输入不能为空~", Toast.LENGTH_LONG).show();
                    }else {
                        if (mCityBeanList.size() == 0){//若容器为空，则加载数据库中的数据
                            mCityBeanList = mWeatherDbOpenHelper.loadAllCity();
                        }

                        for (int i= 0 ; i < mProvinceBeanList.size(); i++){
                            if (s.equals(mProvinceBeanList.get(i).getProvinceName())){
                                searchCity(s);
                                return true;
                            }
                        }

                        for (int i = 0; i < mCityBeanList.size(); i++) {
                            if (s.equals(mCityBeanList.get(i).getCityName())){
                                String cityNameUTF = URLEncoder.encode(s,"UTF-8");//将城市的名字转换为utf8 URLENCLODE格式
                                intent.putExtra("cityId", cityNameUTF);
                                startActivity(intent);
                                finish();
                                return true;
                            }
                        }
                        Toast.makeText(ChooseActivity.this, "不存在此城市~", Toast.LENGTH_LONG).show();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (TextUtils.isEmpty(s)){
                    mLvShowCity.clearTextFilter();//清除ListView的过滤
                }else{
                    mLvShowCity.setFilterText(s);//使用用户输入的内容进行过滤
                }
                return false;
            }
        });

        mBtLocation.setOnClickListener(new View.OnClickListener() {//设置按钮的事件监听
            @Override
            public void onClick(View view) {
                LocationUtil locationUtil = new LocationUtil(new LocationUtil.LocationListener() {
                    @Override
                    public void succeed(String latitude, String longtitude) {//若定位成功则回调改方法
                        Intent intent = new Intent(ChooseActivity.this,WeatherActivity.class);
                        intent.putExtra("gps",true);//设置标志
                        intent.putExtra("latitude",latitude);//携带经度纬度的数据开启活动
                        intent.putExtra("longtitude",longtitude);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void failed() {
                        Log.d("test","error");
                    }
                });
            }
        });
        searchProvince();//查询省份
    }

    /**
     * 加载省份列表
     */
    private void searchProvince() {
        mProvinceBeanList = mWeatherDbOpenHelper.loadProvince();
        if (mProvinceBeanList.size() > 0) {//若数据库中不存在省份信息
            mList.clear();
            mTvTitle.setText("省份");
            for (int i = 0; i < mProvinceBeanList.size(); i++) {
                mList.add(mProvinceBeanList.get(i).getProvinceName());//添加城市名字到容器中
            }
            mCityAdapter.notifyDataSetChanged();//通知列表发生变化,强制调用getView来刷新每个Item的
            mLvShowCity.setSelection(0);//将数据定位到第一行
            mLevel = "province";//设置当前状态为省份查询
        } else {//则发送请求获取
            if (HasNetUtil.hasNetWork()){
                searchByInternet();
            }
            else{
                Toast.makeText(ChooseActivity.this ,"请检查网络是否开启" ,Toast.LENGTH_SHORT).show() ;
                finish();
            }
        }
    }

    /**
     * 加载城市列表
     * @param provinceName
     */
    private void searchCity(String provinceName) {
        mCityBeanList = mWeatherDbOpenHelper.loadCity(provinceName);
        mTvTitle.setText(provinceName);
        mList.clear();
        for (int i = 0; i < mCityBeanList.size(); i++) {
            mList.add(mCityBeanList.get(i).getCityName());
        }
        mCityAdapter.notifyDataSetChanged();//通知列表发生变化
        mLvShowCity.setSelection(0);//将数据定位到第一行
        mLevel = "city";//设置当前状态为查询城市
    }

    /**
     * 发送请求，处理返回结果
     */
    private void searchByInternet() {
        //String address = "http://v.juhe.cn/weather/citys?key=e56938624d0f9e670b989c945ede8aad";
        //new MyAsyncTask().execute(address);//执行查询城市的任务
        mProgressFragment.show(getFragmentManager(), "progressDialog");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://v.juhe.cn/weather/citys?key=e56938624d0f9e670b989c945ede8aad";
                HttpUtil.sendByConnection(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String result) {
                        if (result != null){
                            boolean flag = false;
                            flag = HandleResponseUtil.handleCityResponse(mWeatherDbOpenHelper,result);
                            if (flag){
                                mHandler.obtainMessage(SUCCESS_IN).sendToTarget();
                            }
                        }else{
                            mHandler.obtainMessage(FAIL_OUT).sendToTarget();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        mHandler.obtainMessage(FAIL_OUT).sendToTarget();
                    }
                });
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 重写BACK键方法
     */
    @Override
    public void onBackPressed() {
        if(mLevel.equals("city")){
            searchProvince();
        }else if(mFromWeatherActivity){
            Intent intent = new Intent(ChooseActivity.this,WeatherActivity.class);
            intent.putExtra("backFromChooseActivity",true);
            startActivity(intent);
            finish();
        }else {
            finish();
        }

    }


    /*class MyAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            mProgressFragment.show(getFragmentManager(), "progressDialog");
        }

        /**
         * 执行请求网络查询城市的耗时间任务
         * @param address
         * @return result
         */
       /* @Override
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

        /**
         * 对返回的结果进行处理收尾工作
         * @param result
         */
        /*@Override
        protected void onPostExecute(String result) {
            if (result != null){
                boolean flag = HandleResponseUtil.handleCityResponse(mWeatherDbOpenHelper,result);
                mProgressFragment.dismiss();//取消进度条
                if (flag){
                    searchProvince();//查询省份
                }
            }else{
                mProgressFragment.dismiss();//取消进度条
                Toast.makeText(ChooseActivity.this, "failed to load,please check your internet", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}

