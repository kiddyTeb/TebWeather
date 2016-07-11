package com.liangdekai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangdekai.weather_liangdekai.R;

import java.util.List;

/**
 * 自定义Adapter对城市数据进行适配
 */
public class CityAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private int mResourceId;

    public CityAdapter(Context context , int textViewResourceId , List<String> list){
        this.mResourceId = textViewResourceId;
        this.mContext = context;
        this.mList = list;
    }

    /**
     * 返回数据大小
     * @return
     */
    @Override
    public int getCount() {
        return mList.size();//listView在开始绘制的时候，系统首先调用getCount（）获取长度
    }

    /**
     * 返回具体项
     * @param i
     * @return
     */
    @Override
    public String getItem(int i) {
        return mList.get(i);
    }

    /**
     * 返回每项ID
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {//调用getView（）逐一绘制每一行
        String name = getItem(i);//获取当前项
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.adapter_iv_image);
            viewHolder.textView = (TextView) view.findViewById(R.id.adapter_tv_cityName);
            view.setTag(viewHolder);//把HolderView对象存储在View中
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.menu_city));
        viewHolder.textView.setText(name);
        viewHolder.textView.setTextColor(Color.parseColor("#FF8042"));
        return view;
    }

    /**
     * 内部类对控件实例进行缓存
     */
    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
