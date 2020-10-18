package com.callanna.appdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_MSG_RECEIVER_FORM_CLIENT = "ACTION_MSG_RECEIVER_FORM_CLIENT";

    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);

        UIUpdateReceiver receiver = new UIUpdateReceiver();
        IntentFilter filter = new IntentFilter(ACTION_MSG_RECEIVER_FORM_CLIENT);
        registerReceiver(receiver, filter);

    }
    public class UIUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(ACTION_MSG_RECEIVER_FORM_CLIENT.equals(action)){
                Bundle bundle = intent.getExtras();
                String strtime = bundle.getString("msg");
                mTextView.setText(strtime);
            }
        }
    }
}
