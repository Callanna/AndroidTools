package com.ebanswers.cleanmemory;

import android.os.Bundle;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ebanswers.cleantool.CleanManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        CleanFloatView.getInstance(this).show();
        //mnt/inter_sd/下不删除的文件
        LinkedList<String> listFiles = new LinkedList<>();
        listFiles.add(".android_secure");
        listFiles.add("Android");
        listFiles.add("Alarms");
        listFiles.add("DCIM");
        listFiles.add("Movies");
        listFiles.add("Music");
        listFiles.add("Video");
        listFiles.add("53iq");

        CleanManager.getInstance(this).cleanOtherData(listFiles);

    }


}
