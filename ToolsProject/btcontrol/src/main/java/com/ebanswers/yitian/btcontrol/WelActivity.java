package com.ebanswers.yitian.btcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ebanswers.yitian.btcontrol.task.ApiTask;

/**
 * Created by Callanna on 2017/3/17.
 */

public class WelActivity extends AppCompatActivity {

    private Button btn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(WelActivity.this,MainActivity.class));
            }
        });
        ApiTask.getInstance().getToken();
    }
}
