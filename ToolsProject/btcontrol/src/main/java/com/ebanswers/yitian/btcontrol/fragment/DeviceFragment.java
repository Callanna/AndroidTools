package com.ebanswers.yitian.btcontrol.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ebanswers.yitian.btcontrol.MainActivity;
import com.ebanswers.yitian.btcontrol.R;
import com.ebanswers.yitian.btcontrol.task.ApiTask;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Callanna on 2017/3/17.
 */

public class DeviceFragment extends Fragment {

    private Button btn_next,btn_getCode;
    private TextView tv_time;
    private EditText edit_phone,edit_code,edit_deviceID;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device, null);
        btn_next = (Button) root.findViewById(R.id.btn_devicenext);
        btn_getCode = (Button) root.findViewById(R.id.btn_getcode);
        tv_time = (TextView) root.findViewById(R.id.tv_time);
        edit_phone = (EditText) root.findViewById(R.id.edit_phone);
        edit_code = (EditText) root.findViewById(R.id.edit_code);
        edit_deviceID = (EditText) root.findViewById(R.id.edit_deviceid);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setMainTitle("设备绑定");
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_phone.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(),"电话号码为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edit_deviceID.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(),"设备号为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edit_code.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(),"验证码为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                bindDevice();
            }
        });
        btn_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_phone.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(),"电话号码为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                getSecCode();
            }
        });

    }

    private void bindDevice() {
        ApiTask.getInstance().bindDevice(edit_phone.getText().toString(),edit_code.getText().toString(),edit_deviceID.getText().toString(),new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("duanyl", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("duanyl", "onResponse: "+response.body().string());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_contianer,new LockFragment()).commit();
            }
        });
    }

    public void getSecCode(){
        btn_getCode.setEnabled(false);
        startCountTime();
        ApiTask.getInstance().getSecCode("",new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("duanyl", "onFailure: "+e.getMessage());
                stopTime();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("duanyl", "onResponse: "+response.body().string());
            }
        });
    }
    private Timer timer;
    private TimerTask timerTask;
    private int time = 60;
    private void startCountTime() {
        stopTime();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(time <= 0){
                    stopTime();
                    return;
                }
                time--;
                tv_time.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_time.setText(time+"s");
                    }
                });
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    private void stopTime(){
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        time = 60;
        btn_getCode.post(new Runnable() {
            @Override
            public void run() {
                btn_getCode.setEnabled(true);
                tv_time.setText(60+"s");
            }
        });
    }

}
