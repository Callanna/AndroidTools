package com.ebanswers.yitian.btcontrol.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ebanswers.yitian.btcontrol.CoreApplication;
import com.ebanswers.yitian.btcontrol.R;

import org.json.JSONObject;

import callannna.bluelibrary.socket.ClientDeviceManager;

/**
 * Created by Callanna on 2017/3/17.
 */

public class LockFragment extends Fragment {
    private Button btn_lock;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lock, null);
        btn_lock = (Button) root.findViewById(R.id.btn_lock);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("duanyl", "onClick: lock");
                if(!CoreApplication.getInstance().connectMac.equals("")){
                    JSONObject json_unlock = new JSONObject();
                    json_unlock.optString("lock","1");
                    byte[] cmd = new byte[]{0x5a,0x00,0x01,0x01,0x00};
                    ClientDeviceManager.getInstence().sendCommand(cmd);
                    //CoreApplication.getInstance().bt.send(json_unlock.toString(),true);
                }
            }
        });
    }
}
