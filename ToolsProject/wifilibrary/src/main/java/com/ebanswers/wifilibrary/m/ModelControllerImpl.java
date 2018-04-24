package com.ebanswers.wifilibrary.m;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.Toast;

import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.WifiReceiver;
import com.ebanswers.wifilibrary.p.IPresenter;

import java.util.List;

/**
 * @author Created by lishihui on 2017/4/11.
 */

public class ModelControllerImpl implements WifiReceiver.WifiStateChange {
    private List<ScanResult> mlist;
    private IPresenter mPresenter;
    private Context mContext;

    public ModelControllerImpl(Context context, IPresenter presenter,List<ScanResult> mlist) {
        mContext = context;
        mPresenter = presenter;
        this.mlist = mlist;
    }

    @Override
    public void openWifi() {

    }

    @Override
    public void closeWifi() {

    }

    /**
     * wifi正在连接中
     */
    @Override
    public void openingWifi() {
        Log.d("loadDialog", "openingWifi");
        mPresenter.getViewController().showLoadDialog();
    }

    /**
     * 连接成功
     */
    @Override
    public void connectWifi() {
        mPresenter.getViewController().closeLoadDialog();
    }

    @Override
    public void disconnectWifi() {
        //        mPresenter.getViewController().closeLoadDialog();
    }

    @Override
    public void updateWifiList() {
        if (mlist != null) {
            mlist.clear();
            if (WifiAdmin.getInstance(mContext).getWifiListWithFilting() != null) {
                mlist.addAll(WifiAdmin.getInstance(mContext).getWifiListWithFilting());
                mPresenter.updateData();
            }
        }
    }

    @Override
    public void failWifi(String failMsg) {
        mPresenter.getViewController().closeLoadDialog();
        mPresenter.removePassword();
        Toast.makeText(mContext, failMsg, Toast.LENGTH_SHORT).show();
    }
}
