package com.liangdekai.Fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.liangdekai.weather_liangdekai.R;

/**
 * 用DialogFragment 创建自定义对话框
 */
public class ProgressFragment extends DialogFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//设置不要标题
        setCancelable(false);//设置不可取消
        View view = inflater.inflate(R.layout.fragment_progress,container);//使用自定义的xml布局文件展示Dialog
        return view;
    }
}
