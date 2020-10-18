package com.callanna.androidtools;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mTvMsg;
    private EditText mEditMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_send).setOnClickListener(l->{
             String msg = mEditMsg.getText().toString();
             MsgServiceManager.getInstance().sendWord(msg);
        });
        mTvMsg = findViewById(R.id.tv_msg);
        mEditMsg = findViewById(R.id.editmsg);
        MsgServiceManager.getInstance().init(this);
        MsgServiceManager.getInstance().setMsgReceiverCallback(new MsgServiceManager.MsgReceiverCallback() {
            @Override
            public void onReceiveMsg(String word) {
                mTvMsg.post(()->{
                    mTvMsg.setText(word);
                });
            }
        });
    }


}
