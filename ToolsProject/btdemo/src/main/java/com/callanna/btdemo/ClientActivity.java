package com.callanna.btdemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.callanna.fragment.ClientFragment;
import com.callanna.fragment.DeviceFragment;

import org.simple.eventbus.EventBus;

import java.util.UUID;

import callannna.bluelibrary.BluetoothUtils;
import callannna.bluelibrary.UUIDs;
import callannna.bluelibrary.dao.ClientMsgListener;

public class ClientActivity extends AppCompatActivity {
    public static void start(Context context){
        Intent intent = new Intent(context,ClientActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_contianer,new DeviceFragment()).commit();
    }

    public void connnect(BluetoothDevice device){
        Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
        Log.d("duanyl", "itemclick: "+ UUID.fromString(UUIDs.SerialPortServiceClass_UUID));
        BluetoothUtils.getInstance().connectAsClient(device, UUID.fromString(UUIDs.SerialPortServiceClass_UUID),msgConnectListener);

    }


    private Handler handler = new Handler();
    public ClientMsgListener msgConnectListener = new ClientMsgListener() {
        @Override
        public void connect(BluetoothDevice device) {
            Log.d("duanyl", "connect: "+device.getAddress());
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_contianer, new ClientFragment()).commit();
//            ClientDeviceManager.getInstence().sendCommand("Hello1".getBytes());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Connect Success", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void readMsg(byte[] msg) {
            Log.d("duanyl", "CreadMsg: " + new String(msg));
            EventBus.getDefault().post(new String(msg),"MSG");
        }

        @Override
        public void disConnect() {
           // getSupportFragmentManager().beginTransaction().replace(R.id.frame_contianer, new ClientFragment()).commit();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                }
            }) ;
        }
    };
}
