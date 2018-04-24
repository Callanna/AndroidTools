package com.ebanswers.yitian.btcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ebanswers.yitian.btcontrol.fragment.BlueFragment;

public class MainActivity extends AppCompatActivity {

    private TextView tv_title ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_title = (TextView) findViewById(R.id.tv_title);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_contianer,new BlueFragment()).commit();
    }

    public void setMainTitle(String title){
        tv_title.setText(title);
    }
}
