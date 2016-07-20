package com.liangdekai.util;

import android.app.FragmentManager;
import android.os.AsyncTask;
import com.liangdekai.Fragment.ProgressDialogFragment;


/**
 * Created by asus on 2016/7/19.
 */
public class MyAsyncTask extends AsyncTask<String , Void , String >{
    private FragmentManager mFragmentManager ;
    private RequestListener mRequestListener;
    private ProgressDialogFragment mProgressDialogFragment ;

    public interface RequestListener {
        void succeed(String result);
        void failed();
    }

    public MyAsyncTask(FragmentManager fragmentManager , RequestListener requestListener){
        mFragmentManager = fragmentManager;
        mRequestListener = requestListener;
    }

    public MyAsyncTask(RequestListener requestListener){
        mRequestListener = requestListener;
    }

    @Override
    protected void onPreExecute() {
        showDialog();
    }

    @Override
    protected String doInBackground(String... address) {
        String result = VolleyHelper.sendByVolley(address[0]);
        return result;
    }


    @Override
    protected void onPostExecute(String result) {
        if (result != null){
            mRequestListener.succeed(result);
        }else{
            mRequestListener.failed();
        }
        closeDialog();//取消进度条
    }

    private void showDialog(){
        mProgressDialogFragment = new ProgressDialogFragment();
        mProgressDialogFragment.show(mFragmentManager, "progressDialog");
    }

    private void closeDialog(){
        if (mProgressDialogFragment != null){
            mProgressDialogFragment.dismiss();
        }
    }
}
