package com.callanna.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.callanna.btdemo.ClientActivity;
import com.callanna.btdemo.R;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import callannna.bluelibrary.BluetoothUtils;
import callannna.bluelibrary.dao.DeviceFoundListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceFragment extends Fragment {
    private static final String NO_DEVICE_FIND = "No devices connect";
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private BlueDeviceAdapter mPairedDevicesArrayAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Button btn_next,btn_server;
    private RecyclerView listview;
    private ProgressBar progressBar;
    private Context context;
    public DeviceFragment() {
    }

    public static DeviceFragment newInstance( ) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        //打开蓝牙
        if (!BluetoothUtils.isEnable()) {
            BluetoothUtils.forceEnableBluetooth();
        }
        pairedDevices = BluetoothUtils.getBondedDevices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        btn_next = (Button) view.findViewById(R.id.btn_bluenext);
        btn_server = (Button) view.findViewById(R.id.btn_server);
        listview = (RecyclerView) view.findViewById(R.id.list_devices);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });
        List<BluetoothDevice> devices = new ArrayList<>();
        mPairedDevicesArrayAdapter = new BlueDeviceAdapter(devices);
        if (pairedDevices.size() > 0) {
            progressBar.setVisibility(View.GONE);
            mPairedDevicesArrayAdapter.addHeaderView(getHeaderView(1,null));
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.addHeaderView(getHeaderView(2,device));
            }
            mPairedDevicesArrayAdapter.addHeaderView(getHeaderView(3, null));
        } else {
            progressBar.setVisibility(View.GONE);
            doDiscovery();
        }
        mPairedDevicesArrayAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) adapter.getData().get(position);
                ((ClientActivity) getActivity()).connnect(bluetoothDevice);
            }
        });
        listview.setAdapter(mPairedDevicesArrayAdapter);
        // If there are paired devices, add each one to the ArrayAdapter

    }

    @Override
    public void onDestroy() {
        BluetoothUtils.getInstance().stopDiscoverDevicesAndDestroy();
        super.onDestroy();

    }
    /**
     * 获得头部标题
     *
     * @param type
     * @return
     */
    private View getHeaderView(int type, final BluetoothDevice bluetoothDevice) {
        View view = null;
        if (type == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.item_group_title, (ViewGroup) listview.getParent(), false);
            TextView tv_group_title = (TextView) view.findViewById(R.id.tv_group_title);
            tv_group_title.setText("已配对设备");
        } else if (type == 2) {
            view = LayoutInflater.from(context).inflate(R.layout.item_device, (ViewGroup) listview.getParent(), false);

            TextView tv_bt_name = (TextView) view.findViewById(R.id.tv_bt_name);
            TextView tv_bt_addtess = (TextView) view.findViewById(R.id.tv_bt_addtess);
            tv_bt_name.setText(!TextUtils.isEmpty(bluetoothDevice.getName()) ? bluetoothDevice.getName() : "未知名称");
            tv_bt_addtess.setText(!TextUtils.isEmpty(bluetoothDevice.getAddress()) ? bluetoothDevice.getAddress() : "未知名称");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ClientActivity) getActivity()).connnect(bluetoothDevice);
                }
            });

        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_group_title, (ViewGroup) listview.getParent(), false);
            TextView tv_group_title = (TextView) view.findViewById(R.id.tv_group_title);
            tv_group_title.setText("附近可配对设备");
        }
        return view;
    }
    private DeviceFoundListener deviceFoundListener = new DeviceFoundListener() {
        @Override
        public void findADevice(BluetoothDevice device) {
            progressBar.setVisibility(View.GONE);
            Log.d("duanyl", "deviceFoundListener: device.getName():" + device.getName());
            mPairedDevicesArrayAdapter.addData(device);
        }
    };

    private void doDiscovery() {
        progressBar.setVisibility(View.VISIBLE);
        BluetoothUtils.getInstance().startDiscoverDevices(context, deviceFoundListener);
    }

}
