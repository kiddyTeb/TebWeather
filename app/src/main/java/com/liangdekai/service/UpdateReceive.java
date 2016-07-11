package com.liangdekai.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 创建一个更新天气信息的广播接收器
 */
public class UpdateReceive extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent updateIntent = new Intent(context,UpdateService.class);
        context.startService(updateIntent);
    }
}
