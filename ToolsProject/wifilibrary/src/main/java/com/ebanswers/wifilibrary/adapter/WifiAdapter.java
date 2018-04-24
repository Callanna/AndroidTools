package com.ebanswers.wifilibrary.adapter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebanswers.wifilibrary.NetUtils;
import com.ebanswers.wifilibrary.R;
import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.p.IPresenter;
import com.ebanswers.wifilibrary.v.IViewController;

import java.util.List;

/**
 * @author Created by lishihui on 2017/4/10.
 */

public class WifiAdapter extends BaseAdapter {
    private List<ScanResult> mList;
    private IPresenter mPresenter;
    private IViewController mViewController;
    private int textSize,textColor;
    public WifiAdapter(List<ScanResult> mList,int itemTextSize,int item_text_color, IViewController viewController,IPresenter presenter) {
        this.mList = mList;
        mPresenter = presenter;
        mViewController = viewController;
        textSize = itemTextSize;
        textColor = item_text_color;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public ScanResult getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_adapter_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.connected = (ImageView) convertView.findViewById(R.id.id_iv_wifi_connected);
            viewHolder.name = (TextView) convertView.findViewById(R.id.id_tv_wifi_ssid_name);
            viewHolder.lock = (ImageView) convertView.findViewById(R.id.id_iv_wifi_lock);
            viewHolder.wifi = (ImageView) convertView.findViewById(R.id.id_iv_wifi_signal);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScanResult result = getItem(position);
        if (result.BSSID.equals(WifiAdmin.getInstance(parent.getContext()).getBSSID()) && NetUtils.isWifi(parent.getContext())) {
            Log.d("lishihui_netId","result.SSID："+result.SSID);
            Log.d("lishihui_netId","WifiAdmin.getInstance(parent.getContext()).getNetworkId()："+WifiAdmin.getInstance(parent.getContext()).getNetworkId());
//            WifiConfig.getInstance(parent.getContext()).setSsid(result.SSID);
//            WifiConfig.getInstance(parent.getContext()).setSaveWifiId(result.SSID, WifiAdmin.getInstance(parent.getContext()).getNetworkId());
            mPresenter.savePassword(result.SSID);
            viewHolder.connected.setVisibility(View.VISIBLE);
        } else {
            viewHolder.connected.setVisibility(View.INVISIBLE);
        }
        if (textSize!=0){
            viewHolder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
        }
        if (textColor!=0){
            viewHolder.name.setTextColor(textColor);
        }
        viewHolder.name.setText(result.SSID);
        String security = result.capabilities.toLowerCase();
        if (security.contains("wpa")) {
            viewHolder.lock.setVisibility(View.VISIBLE);
        } else if (security.contains("wep")) {
            viewHolder.lock.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lock.setVisibility(View.INVISIBLE);
        }
        int signalLevel = WifiManager.calculateSignalLevel(result.level, 4);
        viewHolder.wifi.setVisibility(View.VISIBLE);
        switch (signalLevel) {
            case 0:
                viewHolder.wifi.setImageResource(R.drawable.ic_set_wifi_1);
                break;
            case 1:
                viewHolder.wifi.setImageResource(R.drawable.ic_set_wifi_2);
                break;
            case 2:
                viewHolder.wifi.setImageResource(R.drawable.ic_set_wifi_3);
                break;
            case 3:
                viewHolder.wifi.setImageResource(R.drawable.ic_set_wifi_4);
                break;
        }
        return convertView;
    }

    class ViewHolder {
        private TextView name;
        private ImageView connected, lock, wifi;
    }
}
