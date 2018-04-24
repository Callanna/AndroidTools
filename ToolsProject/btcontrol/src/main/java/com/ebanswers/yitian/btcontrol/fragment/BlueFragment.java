package com.ebanswers.yitian.btcontrol.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ebanswers.yitian.btcontrol.CoreApplication;
import com.ebanswers.yitian.btcontrol.MainActivity;
import com.ebanswers.yitian.btcontrol.R;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subcriber;

import java.util.Set;
import java.util.UUID;

import callannna.bluelibrary.BluetoothUtils;
import callannna.bluelibrary.UUIDs;
import callannna.bluelibrary.dao.DeviceFoundListener;

/**
 * Created by Callanna on 2017/3/17.
 */

public class BlueFragment extends Fragment {
    private static final String NO_DEVICE_FIND = "No devices connect";
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private BlueDeviceAdapter mPairedDevicesArrayAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Button btn_next,btn_server;
    private ListView listview;
    private ProgressBar progressBar;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bluetooth, null);
        btn_next = (Button) root.findViewById(R.id.btn_bluenext);
        btn_server = (Button) root.findViewById(R.id.btn_server);
        listview = (ListView) root.findViewById(R.id.list_devices);
        progressBar = (ProgressBar) root.findViewById(R.id.progressbar);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        EventBus.getDefault().register(this);
        //打开蓝牙
        if (!BluetoothUtils.isEnable()) {
            BluetoothUtils.forceEnableBluetooth();
        }
        pairedDevices = BluetoothUtils.getBondedDevices();

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setMainTitle("蓝牙连接");
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });
        btn_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtils.getInstance().startAsServer(UUID.fromString(UUIDs.SerialPortServiceClass_UUID));
            }
        });
        mPairedDevicesArrayAdapter = new BlueDeviceAdapter(context);
        mPairedDevicesArrayAdapter.setItemClickListener(mDeviceClickListener);
        listview.setAdapter(mPairedDevicesArrayAdapter);
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            progressBar.setVisibility(View.GONE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            doDiscovery();
        }
    }

    @Override
    public void onDestroy() {
        BluetoothUtils.getInstance().stopDiscoverDevicesAndDestroy();
        super.onDestroy();

    }

    private DeviceFoundListener deviceFoundListener = new DeviceFoundListener() {
        @Override
        public void findADevice(BluetoothDevice device) {
            progressBar.setVisibility(View.GONE);
            Log.d("duanyl", "deviceFoundListener: device.getName():" + device.getName());
            mPairedDevicesArrayAdapter.add(device);
        }
    };

    private void doDiscovery() {
        mPairedDevicesArrayAdapter.clear();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device);
                CoreApplication.getInstance().connectMac = device.getAddress();
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        BluetoothUtils.getInstance().startDiscoverDevices(context, deviceFoundListener);
    }

    private BlueDeviceAdapter.ItemClickListener mDeviceClickListener = new BlueDeviceAdapter.ItemClickListener() {
        @Override
        public void itemclick(int position, BluetoothDevice device) {
            CoreApplication.getInstance().connectMac = device.getAddress();
            Toast.makeText(context, "Connecting", Toast.LENGTH_SHORT).show();
            Log.d("duanyl", "itemclick: "+UUID.fromString(UUIDs.SerialPortServiceClass_UUID));
            BluetoothUtils.getInstance().connectAsClient(device, UUID.fromString(UUIDs.SerialPortServiceClass_UUID), CoreApplication.getInstance().msgConnectListener);
        }
    };

    @Subcriber(tag = "connect")
    public void connectBlue() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_contianer, new DeviceFragment()).commit();
    }

}
