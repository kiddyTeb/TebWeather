package com.liangdekai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangdekai.bean.TrainBean;
import com.liangdekai.weather_liangdekai.R;

import java.util.List;

/**
 * 自定义Adapter对城市数据进行适配
 */
public class TrainAdapter extends BaseAdapter{
    private List<TrainBean> mTrainBeanList;
    private Context mContext;
    private int mResourceId;

    public TrainAdapter(Context context , int textViewResourceId , List<TrainBean> list){
        this.mResourceId = textViewResourceId;
        this.mContext = context;
        this.mTrainBeanList = list;
    }
    @Override
    public int getCount() {
        return mTrainBeanList.size();
    }

    @Override
    public TrainBean getItem(int i) {
        return mTrainBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        TrainBean trainBean = getItem(i);//获取当前项
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(mContext).inflate(mResourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.ivImage = (ImageView) view.findViewById(R.id.adapter_iv_row);//获取控件实例
            viewHolder.tvStartStation = (TextView) view.findViewById(R.id.adapter_tv_startStation);
            viewHolder.tvStartTime = (TextView) view.findViewById(R.id.adapter_tv_startTime);
            viewHolder.tvStopStation = (TextView) view.findViewById(R.id.adapter_tv_stopStation);
            viewHolder.tvStopTime = (TextView) view.findViewById(R.id.adapter_tv_stopTime);
            viewHolder.tvOpp = (TextView) view.findViewById(R.id.adapter_tv_trainopp);
            view.setTag(viewHolder);//把HolderView对象存储在View中
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.ivImage.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.row));//设置控件的内容
        viewHolder.tvStartStation.setText(trainBean.getStartStation());
        viewHolder.tvStartTime.setText(trainBean.getStartTime());
        viewHolder.tvStopStation.setText(trainBean.getStopStation());
        viewHolder.tvStopTime.setText(trainBean.getStopTime());
        viewHolder.tvOpp.setText(trainBean.getTrainOpp());
        return view;
    }

    /**
     * 内部类对控件实例进行缓存
     */
    class ViewHolder {
        ImageView ivImage;
        TextView tvOpp;
        TextView tvStartStation;
        TextView tvStartTime;
        TextView tvStopStation;
        TextView tvStopTime;
    }
}
