package com.liangdekai.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.liangdekai.adapter.CityAdapter;
import com.liangdekai.db.WeatherDB;
import com.liangdekai.util.RequestAsyncTask;
import com.liangdekai.util.NetWorkUtil;
import com.liangdekai.util.LocationUtil;
import com.liangdekai.weather_liangdekai.R;
import com.liangdekai.util.HandleResponseUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 选择需要查询的天气的城市
 */
public class ChooseActivity extends Activity {
    private CityAdapter mCityAdapter;
    private List<String> mList = new ArrayList<String>();
    private List<String> mProvinceNameList = new ArrayList<String>();
    private List<String> mCityNameList = new ArrayList<String>();
    private WeatherDB mWeatherDB ;
    private ListView mLvShowCity;
    private TextView mTvTitle;
    private Button mBtLocation;
    private SearchView mSvSearch;
    private String mLevel;
    private String mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jumpToWeather();//判断是否直接跳转
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_choose);//加载布局
        initView();//初始化控件
        setAllListener();//设置各种事件监听
        searchProvince();//查询省份
    }

    /**
     * 设置控件的事件监听
     */
    private void setAllListener(){
        mLvShowCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置ListView事件监听
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                judgeClickItem(position);
            }
        });
        mSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {//设置搜索事件监听
            @Override
            public boolean onQueryTextSubmit(String s) {
                return judgeSearchCity(s);
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
                        Intent intent = new Intent(ChooseActivity.this,MainActivity.class);
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
    }

    /**
     * 判断是否为第一次操作，进行判断是否直接跳转到天气界面逻辑
     */
    private void jumpToWeather(){
        SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE);
        boolean mFromWeatherActivity = getIntent().getBooleanExtra("fromWeatherActivity", false);
        mSelected = preferences.getString("city","");//获取文件中的城市名称，若没有，视为首次进行选择操作
        if(!mFromWeatherActivity && !mSelected.equals("")){//不从天气展示页面处跳转而来并且也有选择城市，则直接跳转到天气展示界面
            Intent intent = new Intent(ChooseActivity.this,MainActivity.class);
            intent.putExtra("backFromChooseActivity",true);//设置非第一次操作的标识
            startActivity(intent);
            finish();
            return;
        }
    }

    /**
     * 加载省份列表
     */
    private void searchProvince() {
        mProvinceNameList = mWeatherDB.loadProvince();
        if (mProvinceNameList.size() > 0) {//若数据库中不存在省份信息
            mList.clear();
            mTvTitle.setText("省份");
            for (int i = 0; i < mProvinceNameList.size(); i++) {
                mList.add(mProvinceNameList.get(i));//添加城市名字到容器中
            }
            mCityAdapter.notifyDataSetChanged();//通知列表发生变化,强制调用getView来刷新每个Item的
            mLvShowCity.setSelection(0);//将数据定位到第一行
            mLevel = "province";//设置当前状态为省份查询
        } else {
            if (NetWorkUtil.hasNetWork()){
                searchByInternet();//发送请求获取
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
        mCityNameList = mWeatherDB.loadCity(provinceName);
        mTvTitle.setText(provinceName);
        mList.clear();
        for (int i = 0; i < mCityNameList.size(); i++) {
            mList.add(mCityNameList.get(i));
        }
        mCityAdapter.notifyDataSetChanged();//通知列表发生变化
        mLvShowCity.setSelection(0);//将数据定位到第一行
        mLevel = "city";//设置当前状态为查询城市
    }

    /**
     * 初始化控件
     */
    private void initView(){
        mTvTitle = (TextView) findViewById(R.id.choose_tv_title);//将控件实例化
        mBtLocation = (Button) findViewById(R.id.choose_bt_location);
        mLvShowCity = (ListView) findViewById(R.id.choose_lv_list);
        mSvSearch = (SearchView) findViewById(R.id.choose_sv_search);
        mLvShowCity.setTextFilterEnabled(true);//设置ListView启动过滤
        mSvSearch.setIconifiedByDefault(false);//设置不自动缩小为图标
        mSvSearch.setSubmitButtonEnabled(true);//显示搜索按钮
        mSvSearch.setQueryHint("请输入查询的城市");//设置显示提示文本信息
        mCityAdapter = new CityAdapter(this, mList);
        mLvShowCity.setAdapter(mCityAdapter);//建立ListView与数据的关联
        mWeatherDB = WeatherDB.getInstance(this);
    }


    /**
     * ListView的点击判断逻辑
     */
    private void judgeClickItem(int position){
        if (mLevel.equals("province")) {
            String mSelectedProvinceName = mProvinceNameList.get(position);
            searchCity(mSelectedProvinceName);
        } else if (mLevel.equals("city")) {
            if (mSelected.equals("")) {
                Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                intent.putExtra("cityId", mCityNameList.get(position));//将点击的城市ID信息传递给另外一个活动
                startActivity(intent);
                finish();//关闭当前活动
            } else {
                Intent intent = new Intent();
                intent.putExtra("cityId", mCityNameList.get(position));//将点击的城市ID信息传递给另外一个活动
                setResult(RESULT_OK, intent);
                finish();//关闭当前活动
            }
        }
    }

    /**
     * 搜索框对城市搜索的判断逻辑
     */
    private boolean judgeSearchCity(String s){
        Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
        try {
            if (TextUtils.isEmpty(s)){//判断是否为空值
                Toast.makeText(ChooseActivity.this, "输入不能为空~", Toast.LENGTH_LONG).show();
            }else {
                if (mCityNameList.size() == 0){//若容器为空，则加载数据库中的数据
                    mCityNameList = mWeatherDB.loadAllCity();
                }

                for (int i = 0; i < mProvinceNameList.size(); i++){
                    if (s.equals(mProvinceNameList.get(i))){
                        searchCity(s);
                        return true;
                    }
                }

                for (int i = 0; i < mCityNameList.size(); i++) {
                    if (s.equals(mCityNameList.get(i))){
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



    /**
     * 发送请求，处理返回结果并进行保存
     */
    private void searchByInternet() {
        String address = "http://v.juhe.cn/weather/citys?key=5b34e560321fd5f86680b4deb1e30ad8";
        RequestAsyncTask requestAsyncTask = new RequestAsyncTask(getFragmentManager(), new RequestAsyncTask.RequestListener() {
            @Override
            public void succeed(String result, String empty, boolean mflag) {
                if (result != null){
                    boolean flag = false;
                    flag = HandleResponseUtil.praseCityResponse(mWeatherDB , result);
                    if (flag){
                        searchProvince();//查询省份
                    }
                }else{
                    Toast.makeText(ChooseActivity.this, "数据解析失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed() {
                Toast.makeText(ChooseActivity.this, "网络请求失败...", Toast.LENGTH_SHORT).show();
            }
        },"" , false);
        requestAsyncTask.execute(address);
    }


    /**
     * 重写BACK键方法
     */
    @Override
    public void onBackPressed() {
        if(mLevel.equals("city")){
            searchProvince();
        }else {
            finish();
        }
    }
}

