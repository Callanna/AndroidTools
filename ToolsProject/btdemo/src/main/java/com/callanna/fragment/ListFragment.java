package com.callanna.fragment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.callanna.btdemo.R;
import com.callanna.btdemo.ServerActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_counts;
    private BlueDeviceAdapter adapter;

    public ListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        List<BluetoothDevice> devices = new ArrayList<>();
        adapter = new BlueDeviceAdapter(devices);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) adapter.getData().get(position);
                ((ServerActivity) getActivity()).connnect(bluetoothDevice);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        tv_counts = (TextView) view.findViewById(R.id.tv_counts);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_connect);
        return view;
    }


}
