package com.callanna.btdemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.callanna.fragment.ServerFragment;

import java.util.UUID;

import callannna.bluelibrary.UUIDs;

public class ServerActivity extends AppCompatActivity {
    public static void start(Context context){
        Intent intent = new Intent(context,ServerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_contianer2,new ServerFragment()).commit();

    }

    public void connnect(BluetoothDevice device){
        Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();
        Log.d("duanyl", "itemclick: "+ UUID.fromString(UUIDs.SerialPortServiceClass_UUID));

    }

}
