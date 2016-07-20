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
import com.liangdekai.util.MyAsyncTask;
import com.liangdekai.util.NetWorkUtil;
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
    private CityAdapter mCityAdapter;
    private List<String> mList = new ArrayList<String>();
    private List<String> mProvinceNameList = new ArrayList<String>();
    private List<String> mCityNameList = new ArrayList<String>();
    private WeatherDbOpenHelper mWeatherDbOpenHelper;
    private ListView mLvShowCity;
    private TextView mTvTitle;
    private Button mBtLocation;
    private SearchView mSvSearch;
    private String mLevel;
    private String mSelected;
    private String mSelectedProvinceName;
    private boolean mFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE);
        mFromWeatherActivity = getIntent().getBooleanExtra("fromWeatherActivity",false);//判断是否从展示天气界面处跳转而来
        mSelected = preferences.getString("city","");//获取文件中的城市名称，若没有，视为首次进行选择操作
        if(!mFromWeatherActivity && !mSelected.equals("")){//不从天气展示页面处跳转而来并且也有选择城市，则直接跳转到天气展示界面
            Intent intent = new Intent(ChooseActivity.this,WeatherActivity.class);
            intent.putExtra("backFromChooseActivity",true);//设置非第一次操作的标识
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_choose);
        initView();
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
        mProvinceNameList = mWeatherDbOpenHelper.loadProvince();
        if (mProvinceNameList.size() > 0) {//若数据库中不存在省份信息
            mList.clear();
            mTvTitle.setText("省份");
            for (int i = 0; i < mProvinceNameList.size(); i++) {
                mList.add(mProvinceNameList.get(i));//添加城市名字到容器中
            }
            mCityAdapter.notifyDataSetChanged();//通知列表发生变化,强制调用getView来刷新每个Item的
            mLvShowCity.setSelection(0);//将数据定位到第一行
            mLevel = "province";//设置当前状态为省份查询
        } else {//则发送请求获取
            if (NetWorkUtil.hasNetWork()){
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
        mCityNameList = mWeatherDbOpenHelper.loadCity(provinceName);
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
        mWeatherDbOpenHelper = new WeatherDbOpenHelper(this);
    }


    /**
     * ListView的点击判断逻辑
     */
    private void judgeClickItem(int position){
        if (mLevel.equals("province")) {
            mSelectedProvinceName = mProvinceNameList.get(position);//若点击相应省份，则显示相应城市
            searchCity(mSelectedProvinceName);
        } else if (mLevel.equals("city")) {
            if (mSelected.equals("")) {
                Intent intent = new Intent(ChooseActivity.this, WeatherActivity.class);
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
        Intent intent = new Intent(ChooseActivity.this, WeatherActivity.class);
        try {
            if (TextUtils.isEmpty(s)){//判断是否为空值
                Toast.makeText(ChooseActivity.this, "输入不能为空~", Toast.LENGTH_LONG).show();
            }else {
                if (mCityNameList.size() == 0){//若容器为空，则加载数据库中的数据
                    mCityNameList = mWeatherDbOpenHelper.loadAllCity();
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
     * 发送请求，处理返回结果
     */
    private void searchByInternet() {
        String address = "http://v.juhe.cn/weather/citys?key=5b34e560321fd5f86680b4deb1e30ad8";
        MyAsyncTask myAsyncTask = new MyAsyncTask(getFragmentManager(), new MyAsyncTask.RequestListener() {
            @Override
            public void succeed(String result) {
                if (result != null){
                    boolean flag = false;
                    //flag = HandleResponseUtil.handleCityResponse(mWeatherDbOpenHelper,result);
                    flag = HandleResponseUtil.praseCityResponse(mWeatherDbOpenHelper , result);
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
        });
        myAsyncTask.execute(address);
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

}

