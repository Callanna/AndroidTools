package com.callanna.fragment;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callanna.ChatBean;
import com.callanna.btdemo.R;

import java.util.ArrayList;
import java.util.List;

import callannna.bluelibrary.dao.ServerMsgListener;
import callannna.bluelibrary.socket.ServerDeviceManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ServerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Context context;
    private RecyclerView rv_chat_list;
    private Button bt_send;
    private EditText et_input;
    private  ChatAdapter chatAdapter;
    private TextView tv_counts;
    private int devices;
    private BluetoothDevice mdevice;
    public ServerFragment() {
    }
    public static ServerFragment newInstance(String param1 ) {
        ServerFragment fragment = new ServerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
       context = getContext();
        ServerDeviceManager.getInstance().setClientConnectListener(new ServerDeviceManager.SocketConnectListener() {
            @Override
            public void clientConnected(BluetoothSocket socket) {
                devices++;
                Log.d("duanyl", "clientConnected: "+socket.getRemoteDevice().getName());
                tv_counts.setText(devices+"");
            }
        });
        ServerDeviceManager.getInstance().setServerMsgListener(new ServerMsgListener() {
            @Override
            public void connect(BluetoothDevice device) {

            }

            @Override
            public void readMsg(BluetoothDevice device, byte[] msg) {
                mdevice = device;
                chatAdapter.addData(new ChatBean(ChatBean.RIGHT, new String(msg)));
          }

            @Override
            public void disConnect(BluetoothDevice device) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server, container, false);
        rv_chat_list = (RecyclerView) view.findViewById(R.id.rv_chat_list);
        bt_send = (Button) view.findViewById(R.id.bt_send);
        et_input = (EditText)view. findViewById(R.id.et_input);
        tv_counts = (TextView) view.findViewById(R.id.tv_counts);
        List<ChatBean> chatBeens = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatBeens);
        rv_chat_list.setAdapter(chatAdapter);
        rv_chat_list.setLayoutManager(new LinearLayoutManager(context));
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et_input.getText().toString().trim();
                ServerDeviceManager.getInstance().sendCommand(mdevice.getAddress(),msg.getBytes());
                chatAdapter.addData(new ChatBean(ChatBean.LEFT, msg));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
                et_input.setText("");
            }
        });
        return view;
    }

}
