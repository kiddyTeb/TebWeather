package com.liangdekai.util;

import android.app.FragmentManager;
import android.os.AsyncTask;

import com.liangdekai.Fragment.ShowDialog;


/**
 * 该类用作于对网络进行请求数据，并将数据进行回调
 */
public class RequestAsyncTask extends AsyncTask<String , Void , String >{
    private FragmentManager mFragmentManager = null;
    private RequestListener mRequestListener;
    private ShowDialog mShowDialog = null;
    private String mCity ;

    public interface RequestListener {
        void succeed(String result , String city);
        void failed();
    }

    public RequestAsyncTask(FragmentManager fragmentManager , RequestListener requestListener , String city ){
        this.mFragmentManager = fragmentManager;
        this.mRequestListener = requestListener;
        this.mCity = city;
    }

    public RequestAsyncTask(RequestListener requestListener){
        this.mRequestListener = requestListener;
    }

    @Override
    protected void onPreExecute() {
        if (mFragmentManager != null){
            showDialog();//展示一个对话框
        }
    }

    @Override
    protected String doInBackground(String... address) {
        return VolleyHelper.sendByVolley(address[0]);
    }


    @Override
    protected void onPostExecute(String result) {
        if (result != null){
            mRequestListener.succeed(result , mCity);
        }else{
            mRequestListener.failed();
        }
        closeDialog();//取消对话框
    }

    /**
     * 展示一个对话框
     */
    private void showDialog(){
        mShowDialog = new ShowDialog();
        mShowDialog.show(mFragmentManager, "progressDialog");
    }

    /**
     * 展示一个对话框
     */
    private void closeDialog(){
        if (mShowDialog != null){
            mShowDialog.dismiss();
        }
    }
}
