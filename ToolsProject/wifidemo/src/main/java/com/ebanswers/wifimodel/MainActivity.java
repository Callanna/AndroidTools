package com.ebanswers.wifimodel;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ebanswers.wifilibrary.StyleConfig;
import com.ebanswers.wifilibrary.WifiFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
//        StyleConfig styleConfig = new StyleConfig.Builder().setItemTextColor(Color.parseColor("#ffff00"))
//                .setTopBackGroundColor(Color.parseColor("#3F51B5"))
//                .setLayoutType(StyleConfig.TYPE2)
//                .setTopTitleColor(Color.parseColor("#FF4081")).build();
        fragmentManager.beginTransaction().replace(R.id.id_fl_container, WifiFragment.getInstance(StyleConfig.TYPE1_2)).commitAllowingStateLoss();
    }
}
