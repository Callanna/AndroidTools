package com.callanna.fragment;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.callanna.btdemo.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Callanna on 2017/8/22.
 */

public class BlueDeviceAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

    public BlueDeviceAdapter(@Nullable List<BluetoothDevice> data){
        super(R.layout.item_device);
    }
    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.addOnClickListener(R.id.ll_bluetooth);
        TextView tv_bt_name = (TextView) helper.getView(R.id.tv_bt_name);
        TextView tv_bt_addtess = (TextView) helper.getView(R.id.tv_bt_addtess);
        String name = item.getName();
        String address = item.getAddress();
        tv_bt_name.setText(!TextUtils.isEmpty(name) ? name : "未知名称");
        tv_bt_addtess.setText(!TextUtils.isEmpty(address) ? address : "未知名称");
    }
}
