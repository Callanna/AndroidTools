package com.callanna.btdemo;


import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.UUID;

import callannna.bluelibrary.BluetoothUtils;
import callannna.bluelibrary.UUIDs;

public class MainActivity extends AppCompatActivity {
    private SwitchButton sbtn_bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sbtn_bluetooth = (SwitchButton) findViewById(R.id.sbtn_bluetooth);
        sbtn_bluetooth.setChecked(BluetoothUtils.isEnable());


        findViewById(R.id.btn_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               ClientActivity.start(getApplicationContext());
            }
        });
        findViewById(R.id.btn_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothUtils.makeDiscoverable(getBaseContext(),120);
                BluetoothUtils.getInstance().startAsServer(UUID.fromString(UUIDs.SerialPortServiceClass_UUID));
                ServerActivity.start(getBaseContext());
            }
        });
        sbtn_bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    BluetoothUtils.forceEnableBluetooth();
                }else{
                    BluetoothUtils.disableBluetooth();
                }
            }
        });
    }
}
