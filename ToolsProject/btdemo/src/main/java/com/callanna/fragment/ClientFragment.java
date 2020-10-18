package com.callanna.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callanna.ChatBean;
import com.callanna.btdemo.R;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subcriber;

import java.util.ArrayList;
import java.util.List;

import callannna.bluelibrary.socket.ClientDeviceManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ClientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private Context context;
    private String mParam1;
    private RecyclerView rv_chat_list;
    private Button bt_send;
    private EditText et_input;
    private ChatAdapter chatAdapter;

    public ClientFragment() {
        // Required empty public constructor
    }
    public static ClientFragment newInstance(String param1 ) {
        ClientFragment fragment = new ClientFragment();
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
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_client, container, false);
        rv_chat_list = (RecyclerView) view.findViewById(R.id.rv_chat_list);
        bt_send = (Button) view.findViewById(R.id.bt_send);
        et_input = (EditText)view. findViewById(R.id.et_input);

        List<ChatBean> chatBeen = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatBeen);
        rv_chat_list.setAdapter(chatAdapter);
        rv_chat_list.setLayoutManager(new LinearLayoutManager(context));
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et_input.getText().toString().trim();
                ClientDeviceManager.getInstence().sendCommand(msg.getBytes());
                chatAdapter.addData(new ChatBean(ChatBean.RIGHT, msg));
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_input.getWindowToken(), 0);
                et_input.setText("");
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subcriber(tag = "MSG")
    public void connectBlue(String msg) {
        Log.d("duanyl", "connectBlue: "+msg);
        chatAdapter.addData(new ChatBean(ChatBean.LEFT, msg));
    }

}
