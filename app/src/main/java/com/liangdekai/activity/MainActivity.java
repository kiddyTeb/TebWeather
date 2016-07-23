package com.liangdekai.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.liangdekai.Fragment.MainCity;
import com.liangdekai.Fragment.ExtraCity;
import com.liangdekai.adapter.CityAdapter;
import com.liangdekai.adapter.FragmentAdapter;
import com.liangdekai.util.NetWorkUtil;
import com.liangdekai.util.RequestAsyncTask;
import com.liangdekai.weather_liangdekai.R;
import com.liangdekai.db.WeatherDbOpenHelper;
import com.liangdekai.util.HandleResponseUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 显示天气信息
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private Button mBtChooseCity;
    private Button mBtRefresh;
    private Button mBtAddCity;
    private DrawerLayout mDrawerLayout;
    private WeatherDbOpenHelper mWeatherDbOpenHelper;
    private FragmentAdapter fragmentAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private MainCity mFirstCity;
    private ExtraCity mSecondCity;
    private ExtraCity mThirdCity;
    private ExtraCity mForthCity;
    private ViewPager mViewPager;
    private CityAdapter mCityAdapter;
    private ListView mLvCity;
    private List<String> mList = new ArrayList<String>();
    private boolean isFromChooseActivity;
    private String mCityFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
        setContentView(R.layout.draw_main);//加载布局
        initializeView(getIntent().getStringExtra("cityId"));//初始化控件
        setOnListener();//注册监听事件
        isFromChooseActivity = getIntent().getBooleanExtra("backFromChooseActivity",false);//判断是否通过BACK返回到此界面,或者是否已经选择城市跳过选择界面
        boolean gps = getIntent().getBooleanExtra("gps",false);//判断是否从自动定位进入此界面
        if (gps){
            findWeatherByGps(getIntent().getStringExtra("latitude"),getIntent().getStringExtra("longtitude"),
                    getIntent().getStringExtra("cityId"),isFromChooseActivity);//根据经纬度进行查询天气
        }else {
            if (isFromChooseActivity) {//判断是否非第一次操作
                showWeather();//展示天气背景
            }else if (NetWorkUtil.hasNetWork()){
                String cityName = getIntent().getStringExtra("cityId");//获取用户选取的城市
                mList.add(cityName);//存储用户选择的城市
                mCityAdapter.notifyDataSetChanged();//通知列表发生变化,强制调用getView来刷新每个Item的
                sendResquest(cityName , isFromChooseActivity );
            } else {
                Toast.makeText(MainActivity.this , "网络连接异常，请检查网络" , Toast.LENGTH_LONG).show();
                showWeather();//展现天气
            }
        }
    }

    /**
     * 获取从城市列表返回的数据，进行操作
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            String cityName = data.getStringExtra("cityId");//获取用户选择的常用城市
            boolean flag = false ;//选取标志位，确认是否超过城市数量限制范围
            switch (requestCode){
                case 1 :
                    switch (mList.size()){//动态添加城市逻辑
                        case 0:
                            break;
                        case 1:
                            mSecondCity = ExtraCity.newInstance(cityName);
                            mFragments.add(mSecondCity);
                            break;
                        case 2:
                            mThirdCity = ExtraCity.newInstance(cityName);
                            mFragments.add(mThirdCity);
                            break;
                        case 3:
                            mForthCity = ExtraCity.newInstance(cityName);
                            mFragments.add(mForthCity);
                        default:
                            flag = true ;
                            break;
                    }
                    if (flag){
                        Toast.makeText(this , "已经到达上限" ,Toast.LENGTH_SHORT).show();//对城市添加数量进行限制
                        break;
                    }
                    sendResquest(cityName , isFromChooseActivity);
                    mWeatherDbOpenHelper.saveCommonCity(MainActivity.this , cityName);//存储常用城市到文件
                    mList.add(cityName);//添加城市到容器中
                    mCityAdapter.notifyDataSetChanged();//通知常用城市列表发生变化,强制调用getView来刷新每个Item的
                    mDrawerLayout.closeDrawers();//关闭侧滑菜单
                    mViewPager.setAdapter(fragmentAdapter);//设置ViewPager
                    fragmentAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 按钮的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weather_bt_switch:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.weather_bt_refresh://点击更新按钮
                refresh();
                break;
            case R.id.weather_bt_add ://打开添加常用城市列表
                openList();
                break;
        }
    }

    /**
     * 打开添加常用城市列表
     */
    private void openList(){
        Intent intent = new Intent(MainActivity.this,ChooseActivity.class);
        intent.putExtra("fromWeatherActivity",true);//设置是否从天气展示界面跳转而回的标志
        startActivityForResult(intent ,1);
        mDrawerLayout.closeDrawers();
    }

    /**
     * 更新天气逻辑
     */
    private void refresh(){
        if (NetWorkUtil.hasNetWork()){
            Log.d("test",mCityFlag);
            Log.d("test",mList.size()+"");
            //sendResquest(mCityFlag, true);//从文件获取城市名字并转换格式，重新发送请求
        }else {
            Toast.makeText(MainActivity.this , "网络连接异常，请检查网络" , Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 常用城市列表监听事件
     */
    private void cityOnClickListener(){
        mLvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0 :
                        mViewPager.setCurrentItem(0);
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1);
                        break;
                    case 2:
                        mViewPager.setCurrentItem(2);
                        break;
                    case 3:
                        mViewPager.setCurrentItem(3);
                        break;
                }
                mDrawerLayout.closeDrawers();
            }
        });
        mLvCity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //showDialog();
                if (mList.size()==1){
                    Toast.makeText(MainActivity.this, "城市列表不能为空",Toast.LENGTH_SHORT).show();
                    return true;
                }
                mWeatherDbOpenHelper.deleteCommonCity(MainActivity.this ,mList.get(i));
                mWeatherDbOpenHelper.deleteWeather(mList.get(i));//删除城市时，删除该数据库中的天气信息
                mList.remove(i);
                mCityAdapter.notifyDataSetChanged();//通知列表发生变化,强制调用getView来刷新每个Item的
                if (mList.size()<mFragments.size()) {
                    mFragments.remove(i);
                    fragmentAdapter.notifyDataSetChanged();
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }



    /**
     * 展示一个对话框
     */
    /*private void showDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("请确认");
        alertDialog.setMessage("是否删除该城市");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("test" , i+"");
            }
        });
        alertDialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }*/

    /**
     * 转换UTF-8格式
     */
    private String transform(String formmer){
        try {
            //进行URL编码
            return URLEncoder.encode(formmer,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置各种事件监听
     */
    private void setOnListener(){
        mBtChooseCity.setOnClickListener(this);//添加事件监听
        mBtRefresh.setOnClickListener(this);
        mBtAddCity.setOnClickListener(this);
    }

    /**
     * 恢复用户之前添加的城市视图
     */
    private void restore(){
        switch (mList.size()){
            case 4:
                mSecondCity = ExtraCity.newInstance(mList.get(1));
                mFragments.add(mSecondCity);
                mThirdCity = ExtraCity.newInstance(mList.get(2));
                mFragments.add(mThirdCity);
                mForthCity = ExtraCity.newInstance(mList.get(3));
                mFragments.add(mForthCity);
                break;
            case 3:
                mSecondCity = ExtraCity.newInstance(mList.get(1));
                mFragments.add(mSecondCity);
                mThirdCity = ExtraCity.newInstance(mList.get(2));
                mFragments.add(mThirdCity);
                break;
            case 2:
                mSecondCity = ExtraCity.newInstance(mList.get(1));
                mFragments.add(mSecondCity);
                break;
            case 1:
                break;
        }
    }

    /**
     * 初始化各种控件
     */
    private void initializeView(String cityName){
        SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE) ;//获取存储主城市文件的引用
        mCityFlag = preferences.getString("city","");
        mWeatherDbOpenHelper = new WeatherDbOpenHelper(this);
        mBtChooseCity = (Button) findViewById(R.id.weather_bt_switch);
        mBtRefresh = (Button) findViewById(R.id.weather_bt_refresh);
        mBtAddCity = (Button) findViewById(R.id.weather_bt_add);
        mLvCity = (ListView) findViewById(R.id.show_lv_city);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.weather_ll_background);
        mList = mWeatherDbOpenHelper.loadCommonCity(MainActivity.this);//加载所有常用城市到容器中
        mCityAdapter = new CityAdapter(this , mList);
        mLvCity.setAdapter(mCityAdapter);
        mCityAdapter.notifyDataSetChanged();//通知常用城市列表发生变化,强制调用getView来刷新每个Item的
        cityOnClickListener();//注册事件监听
        if (!isFromChooseActivity){
            mFirstCity = MainCity.newInstance(cityName);
        }else {
            mFirstCity = MainCity.newInstance(preferences.getString("city",""));
        }
        mFragments.add(mFirstCity);
        restore();//恢复用户之前添加的城市视图
        mViewPager = (ViewPager) findViewById(R.id.weather_vp_view);
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager() , mFragments);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        mCityFlag = mList.get(position);//每次滑动到该页面时，记录下当前页面的城市名字
                        showWeather();//并且根据天气种类切换背景
                        break;
                    case 1 :
                        mCityFlag = mList.get(position);
                        showWeather();
                        break;
                    case 2:
                        mCityFlag = mList.get(position);
                        showWeather();
                        break;
                    case 3:
                        mCityFlag = mList.get(position);
                        showWeather();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 改变天气背景并开启更新服务
     */
    public void showWeather(){
        SharedPreferences preferences = getSharedPreferences(mCityFlag , MODE_PRIVATE) ;
        changeBackground(mDrawerLayout,preferences.getString("weather",""));

        /*Intent intent = new Intent(MainActivity.this, UpdateService.class);
        startService(intent);//启动更新数据服务*/

    }

    /**
     * 根据城市ID发送请求
     * @param
     */
    private void sendResquest(String cityName , boolean flag ){
        String cityId = transform(cityName);
        String address = "http://v.juhe.cn/weather/index?cityname="+cityId+"&dtype=&format=2&key=5b34e560321fd5f86680b4deb1e30ad8";
        new RequestAsyncTask(getFragmentManager(), new RequestAsyncTask.RequestListener() {
            @Override
            public void succeed(String result , String city , boolean mflag ) {
                Log.d("test",result);
                boolean flag = HandleResponseUtil.praseWeatherResponse(MainActivity.this , mWeatherDbOpenHelper , result , city , mflag);
                if (flag){
                    if (!isFromChooseActivity){
                        SharedPreferences preferences = getSharedPreferences("data" , MODE_PRIVATE) ;//获取存储主城市文件的引用
                        mCityFlag = preferences.getString("city","");
                        mFirstCity.loadData();
                    }
                    showWeather();
                }else {
                    Toast.makeText(MainActivity.this, "数据解析失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed() {
                Toast.makeText(MainActivity.this, "网络请求失败...", Toast.LENGTH_SHORT).show();
            }
        }, cityName , flag).execute(address);
    }

    /**
     * 根据经度纬度发送请求，返回JSON天气数据，并处理存储
     * @param latitude
     * @param longtitude
     */
    private void findWeatherByGps(String latitude , String longtitude , String city , boolean flag ){
        String address = "http://v.juhe.cn/weather/geo?format=2&key=5b34e560321fd5f86680b4deb1e30ad8&lon="+longtitude+"&lat="+latitude;
        new RequestAsyncTask(getFragmentManager(), new RequestAsyncTask.RequestListener() {
            @Override
            public void succeed(String result , String city , boolean mflag ) {
                boolean flag = HandleResponseUtil.praseWeatherResponse(MainActivity.this , mWeatherDbOpenHelper , result , city , mflag);
                if (flag){
                    showWeather();
                }else {
                    Toast.makeText(MainActivity.this, "数据解析失败...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failed() {
                Toast.makeText(MainActivity.this, "网络请求失败...", Toast.LENGTH_SHORT).show();
            }
        }, city , flag ).execute(address);
    }

    /**
     * 通过不同的天气种类来转换不同天气的背景
     * @param linearLayout
     * @param weatherName
     */
    private void changeBackground(DrawerLayout linearLayout , String weatherName){
        if(weatherName.contains("阴")||weatherName.contains("云")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg02));
        }else if(weatherName.contains("晴")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg01));
        }else if (weatherName.contains("雪")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg_snow));
        }else if (weatherName.contains("雨")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg_rain));
        }else if (weatherName.contains("雾")||weatherName.contains("霾")){
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.bg_fog));
        } else {
            linearLayout.setBackground(getResources().getDrawable(R.mipmap.app_bg01));
        }
    }

}
