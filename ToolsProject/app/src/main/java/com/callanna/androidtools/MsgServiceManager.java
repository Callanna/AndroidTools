package com.callanna.androidtools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Callanna on 2020/10/18.
 */

public class MsgServiceManager  {

    private static final int MSG_ANSWER_FORM_SERVER = 0x01;
    private static final int MSG_SEND_FORM_LOCAL = 0x01;

    private static final String LOGTAG = "MsgServiceManager";
    private static MsgServiceManager instance;
    private Messenger mService;
    private Messenger mClient;
    private WeakReference<Context> mContext;
    private boolean isConn = false;
    private MsgReceiverCallback mMsgReceiverCallback;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mService = new Messenger(iBinder);
            mClient = new Messenger(  new ClientHandler());
            isConn = true;

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mClient = null;
            isConn = false;
        }
    };



    private MsgServiceManager() {
    }

    public static MsgServiceManager getInstance() {
        if (instance == null) {
            synchronized(MsgServiceManager.class) {
                if (instance == null) {
                    instance = new MsgServiceManager();
                }
            }
        }

        return instance;
    }

    public void init(Context context) {
        this.mContext = new WeakReference(context.getApplicationContext());

        this.bindRunnerService();
    }


    private void bindRunnerService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.callanna.appdemo","com.callanna.appdemo.MyMsgService"));
        intent.setAction("com.callanna.appdemo.runwork");

        Intent intent2 = new Intent("com.callanna.appdemo.startmsgservice");
        if ( mContext.get().bindService(intent,  mServiceConnection,Context.BIND_AUTO_CREATE )) {
            Log.d(LOGTAG, " bind success");
        } else {
            Log.d(LOGTAG, " bind failed");
            mContext.get().sendBroadcast(intent2);
            Log.d(LOGTAG, "sendBroadcast");
            new Thread(new Runnable() {
                public void run() {
                    try {

                        Thread.sleep(5000L);
                        if(!isConn) {
                            bindRunnerService();
                        }
                    } catch (InterruptedException var2) {
                        var2.printStackTrace();
                    }

                }
            }).start();
        }

    }

    private boolean sendMessage(Message msg) {
        if (!this.isConn) {
            return false;
        } else {
            msg.replyTo = this.mClient;
            try {
                this.mService.send(msg);
            } catch (RemoteException var3) {
                Log.e("SmartRunner", "smart runner error :" + var3.getMessage());
            }

            return true;
        }
    }

    public void sendWord(String text) {
        Message msg = Message.obtain();
        msg.what = MSG_SEND_FORM_LOCAL;
        Bundle bundle = new Bundle();
        bundle.putString("clientmsg",text);
        msg.setData(bundle);
        this.sendMessage(msg);
    }

    public boolean isConn() {
        return isConn;
    }

    class ClientHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("result");
            switch (msg.what) {
                case MSG_ANSWER_FORM_SERVER:
                    //do something
                    String remsg =  msg.getData().getString("servermsg");
                    Log.d("MyMsgService","msg form server:"+remsg);
                     if(mMsgReceiverCallback != null){
                         mMsgReceiverCallback.onReceiveMsg(remsg);
                     }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void setMsgReceiverCallback(MsgReceiverCallback msgReceiverCallback) {
        mMsgReceiverCallback = msgReceiverCallback;
    }

    public interface MsgReceiverCallback{
        void onReceiveMsg(String word);
    }
}
