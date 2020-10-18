package com.callanna.appdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MsgStartReceiver extends BroadcastReceiver {
    private static final String ACTION_START_SERVICE = "com.callanna.appdemo.startmsgservice";
    @Override
    public void onReceive(Context context, Intent intent) {
      if(intent.getAction().equals(ACTION_START_SERVICE)){
          context.startService(new Intent(context,MyMsgService.class));
      }
    }
}
