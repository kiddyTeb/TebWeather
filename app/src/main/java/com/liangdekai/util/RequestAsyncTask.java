package com.liangdekai.util;

import android.app.FragmentManager;
import android.os.AsyncTask;

import com.liangdekai.Fragment.ShowDialog;


/**
 * Created by asus on 2016/7/19.
 */
public class RequestAsyncTask extends AsyncTask<String , Void , String >{
    private FragmentManager mFragmentManager = null;
    private RequestListener mRequestListener;
    private ShowDialog mShowDialog = null;
    private String mCity ;
    private boolean flag ;

    public interface RequestListener {
        void succeed(String result , String city , boolean flag );
        void failed();
    }

    public RequestAsyncTask(FragmentManager fragmentManager , RequestListener requestListener , String city , boolean flag ){
        this.mFragmentManager = fragmentManager;
        this.mRequestListener = requestListener;
        this.mCity = city;
        this.flag = flag ;
    }



    @Override
    protected void onPreExecute() {
        if (mFragmentManager != null){
            showDialog();
        }
    }

    @Override
    protected String doInBackground(String... address) {
        return VolleyHelper.sendByVolley(address[0]);
    }


    @Override
    protected void onPostExecute(String result) {
        if (result != null){
            mRequestListener.succeed(result , mCity , flag );
        }else{
            mRequestListener.failed();
        }
        closeDialog();//取消进度条
    }

    private void showDialog(){
        mShowDialog = new ShowDialog();
        mShowDialog.show(mFragmentManager, "progressDialog");
    }

    private void closeDialog(){
        if (mShowDialog != null){
            mShowDialog.dismiss();
        }
    }
}
