package com.callanna.appdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyMsgService extends Service {
    private static final int MSG_FORM_CLIENT = 0x01;
    private Messenger messenger = null;
    private  int mTotalTime = 0;
    private ScheduledExecutorService mTimerThread;
    private Intent uiIntent;
    private Bundle bundle;


    private class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //handle message
            switch (msg.what) {
                case MSG_FORM_CLIENT:
                    String sendmsg =  msg.getData().getString("clientmsg");
                    Log.d("MyMsgService","msg form client:"+sendmsg);
                    sendMsgToMainPage(sendmsg);

                    Message newMsg = Message.obtain();
                    Bundle bundle = new Bundle();
                    newMsg.what = 0x01;
                    bundle.putString("servermsg", "我收到了："+sendmsg+"现在保持通讯连接"+mTotalTime);
                    newMsg.setData(bundle);
                    try {
                        msg.replyTo.send(newMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void sendMsgToMainPage(String sendmsg) {
        bundle.putString("msg", sendmsg);
        uiIntent.putExtras(bundle);
        uiIntent.setAction(MainActivity.ACTION_MSG_RECEIVER_FORM_CLIENT);
        //发送广播，通知UI层时间改变了
        sendBroadcast(uiIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        messenger = new Messenger(new MsgHandler());
        Log.d("MyMsgService", " MyMsgService onCreate success");
        if (mTimerThread != null) {
            mTimerThread.shutdownNow();
            mTimerThread = null;
        }
        uiIntent = new Intent();
        bundle = new Bundle();

        mTimerThread = Executors.newScheduledThreadPool(1);
        mTimerThread.scheduleWithFixedDelay(()->{
            mTotalTime++;
        },0,1000, TimeUnit.MILLISECONDS);
    }
}
