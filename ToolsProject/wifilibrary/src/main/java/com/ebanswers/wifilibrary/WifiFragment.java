package com.ebanswers.wifilibrary;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ebanswers.wifilibrary.adapter.WifiAdapter;
import com.ebanswers.wifilibrary.adapter.WifiAdapter2;
import com.ebanswers.wifilibrary.dialog.DialogUtils;
import com.ebanswers.wifilibrary.p.PresenterImpl;
import com.ebanswers.wifilibrary.v.IViewController;

import java.util.ArrayList;
import java.util.List;

import static com.ebanswers.wifilibrary.StyleConfig.TYPE1_1;

/**
 * @author Created by lishihui on 2017/4/10.
 */

public class WifiFragment extends Fragment implements IViewController, CompoundButton.OnCheckedChangeListener {

    private Context mContext;
    private View mContentView;
    private ListView mListView;
    private GridView mGridview;
    private CheckBox mWifiToggle;
    private TextView mTip, mTitle;
    private LinearLayout add_wifi, search_wifi;
    private WifiReceiver.WifiStateChange mWifiStateChange;
    private List<ScanResult> mListScanResult;
    private WifiAdapter mWifiAdapter;
    private WifiAdapter2 mWifiAdapter2;
    private PresenterImpl mPresenterImpl;
    private Dialog loadDialog, passwordDialog, disconnectDialog, addWifiDialog;
    private int layout_type = 1, top_bg_color = 0, top_bg_drawable = 0, top_title_size = 0, top_title_color = 0, item_text_size = 0, item_text_color = 0, bg_color = 0, bg_drawable = 0;
    private RelativeLayout type1_bg_color_rl, type2_bg_color_rl;
    private RelativeLayout rl_title_top_bg;

    public static WifiFragment getInstance(StyleConfig styleConfig) {
        WifiFragment wifiFragment = new WifiFragment();
        if (styleConfig != null) {
            wifiFragment.setArguments(styleConfig.getBundle());
        }
        return wifiFragment;
    }

    public static WifiFragment getInstance(int type) {
        StyleConfig styleConfig = new StyleConfig.Builder().setLayoutType(type).build();
        return WifiFragment.getInstance(styleConfig);
    }

    public static WifiFragment getInstance() {
        return WifiFragment.getInstance(TYPE1_1);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layout_type = bundle.getInt("layout_type");
            Log.d("lishihui_aaa", "layout_type：" + layout_type);
            top_bg_color = bundle.getInt("top_bg_color");
            top_bg_drawable = bundle.getInt("top_bg_drawable");
            Log.d("lishihui_aaa", "top_bg_drawable：" + top_bg_drawable);
            top_title_size = bundle.getInt("top_title_size");
            top_title_color = bundle.getInt("top_title_color");
            item_text_size = bundle.getInt("item_text_size");
            item_text_color = bundle.getInt("item_text_color");
            bg_color = bundle.getInt("bg_color");
            bg_color = bundle.getInt("background_drawable");
        }
    }

    private WifiFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (layout_type == TYPE1_1) {
            mContentView = inflater.inflate(R.layout.wifi_fragment1_1, container, false);
        } else if (layout_type == StyleConfig.TYPE1_2) {
            mContentView = inflater.inflate(R.layout.wifi_fragment1_2, container, false);
        } else if (layout_type == StyleConfig.TYPE2_1) {
            mContentView = inflater.inflate(R.layout.wifi_fragment2_1, container, false);
        } else if (layout_type == StyleConfig.TYPE2_2) {
            mContentView = inflater.inflate(R.layout.wifi_fragment2_2, container, false);
        } else {
            mContentView = inflater.inflate(R.layout.wifi_fragment1_1, container, false);
        }
        initViews();
        initData();
        return mContentView;
    }

    private void initData() {
        mListScanResult = new ArrayList<>();
        mPresenterImpl = new PresenterImpl(mContext, this, mListScanResult);
        if (layout_type == StyleConfig.TYPE1_1 || layout_type == StyleConfig.TYPE1_2) {
            mWifiAdapter = new WifiAdapter(mListScanResult, item_text_size, item_text_color, this, mPresenterImpl);
            mListView.setAdapter(mWifiAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < mListScanResult.size())
                        mPresenterImpl.connect(mListScanResult.get(position));
                }
            });

        } else if (layout_type == StyleConfig.TYPE2_1 || layout_type == StyleConfig.TYPE2_2) {
            mWifiAdapter2 = new WifiAdapter2(mListScanResult, item_text_size, item_text_color, this, mPresenterImpl);
            mGridview.setAdapter(mWifiAdapter2);
            mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < mListScanResult.size())
                        mPresenterImpl.connect(mListScanResult.get(position));
                }
            });
        }
        add_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenterImpl.addWifi();
            }
        });
        search_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenterImpl.init(mListScanResult);
            }
        });
        mPresenterImpl.init(mListScanResult);
    }

    private void initViews() {
        mTitle = (TextView) mContentView.findViewById(R.id.id_tv_wifi_header);
        rl_title_top_bg = (RelativeLayout) mContentView.findViewById(R.id.id_rl_title_top_bg);
        mWifiToggle = (CheckBox) mContentView.findViewById(R.id.id_cb_toggle);
        mWifiToggle.setOnCheckedChangeListener(this);
        if (top_title_size != 0) {
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, top_title_size);
        }
        if (top_title_color != 0) {
            mTitle.setTextColor(top_title_color);
        }
        if (top_bg_drawable != 0) {
            rl_title_top_bg.setBackgroundResource(top_bg_drawable);
        } else if (top_bg_color != 0) {
            rl_title_top_bg.setBackgroundColor(top_bg_color);
        }
        if (layout_type == StyleConfig.TYPE1_1 || layout_type == StyleConfig.TYPE1_2) {
            mTip = (TextView) mContentView.findViewById(R.id.id_tv_wifi_toggle_tip);
            mListView = (ListView) mContentView.findViewById(R.id.id_lv_wifi_list);
            type1_bg_color_rl = (RelativeLayout) mContentView.findViewById(R.id.id_rl1_bg);
            if (bg_drawable != 0) {
                type1_bg_color_rl.setBackgroundResource(bg_drawable);
            } else if (bg_color != 0) {
                type1_bg_color_rl.setBackgroundColor(bg_color);
            }

        } else if (layout_type == StyleConfig.TYPE2_1 || layout_type == StyleConfig.TYPE2_2) {
            mTip = (TextView) mContentView.findViewById(R.id.id_tv_wifi_toggle2_tip);
            mGridview = (GridView) mContentView.findViewById(R.id.id_gv_wifi2_list);
            type2_bg_color_rl = (RelativeLayout) mContentView.findViewById(R.id.id_rl2_bg);
            if (bg_drawable != 0) {
                type2_bg_color_rl.setBackgroundResource(bg_drawable);
            } else if (bg_color != 0) {
                type2_bg_color_rl.setBackgroundColor(bg_color);
            }
        } else {
            mTip = (TextView) mContentView.findViewById(R.id.id_tv_wifi_toggle_tip);
            mListView = (ListView) mContentView.findViewById(R.id.id_lv_wifi_list);
            type1_bg_color_rl = (RelativeLayout) mContentView.findViewById(R.id.id_rl1_bg);
            if (bg_drawable != 0) {
                type1_bg_color_rl.setBackgroundResource(bg_drawable);
            } else if (bg_color != 0) {
                type1_bg_color_rl.setBackgroundColor(bg_color);
            }
        }
        add_wifi = (LinearLayout) mContentView.findViewById(R.id.id_ll_wifi_add);
        search_wifi = (LinearLayout) mContentView.findViewById(R.id.id_ll_wifi_search);
    }

    @Override
    public void refreshList() {
        if (mListScanResult.size() > 0) {
            mTip.setVisibility(View.GONE);
        }
        if (layout_type == StyleConfig.TYPE1_1 || layout_type == StyleConfig.TYPE1_2) {
            mListView.setVisibility(View.VISIBLE);
            mWifiAdapter.notifyDataSetChanged();
            mListView.smoothScrollToPosition(0);
        } else if (layout_type == StyleConfig.TYPE2_1 || layout_type == StyleConfig.TYPE2_2) {
            mGridview.setVisibility(View.VISIBLE);
            mWifiAdapter2.notifyDataSetChanged();
            mGridview.smoothScrollToPosition(0);
        }

    }

    @Override
    public void showInputPasswordDialog(ScanResult scanResult, DialogUtils.DialogCallBack dialogCallBack) {
        closeInputPasswordDialog();
        if (passwordDialog == null)
            passwordDialog = DialogUtils.createPassWordDialog(mContext, scanResult, dialogCallBack);
        Log.d("disconnect", "password:" + WifiConfig.getInstance(mContext).getPasswd(scanResult.SSID));

    }

    @Override
    public void closeInputPasswordDialog() {
        if (passwordDialog != null) {
            if (passwordDialog.isShowing())
                passwordDialog.dismiss();
            passwordDialog = null;
        }

    }

    @Override
    public void showDisconnectDialog(ScanResult scanResult, DialogUtils.DialogCallBack dialogCallBack) {
        closeDisconnectDialog();
        if (disconnectDialog == null)
            disconnectDialog = DialogUtils.createForgetWifiDialog(mContext, scanResult, dialogCallBack);
    }

    @Override
    public void closeDisconnectDialog() {
        if (disconnectDialog != null) {
            if (disconnectDialog.isShowing())
                disconnectDialog.dismiss();
            disconnectDialog = null;
        }

    }

    @Override
    public void showLoadDialog() {
        closeDisconnectDialog();
        closeInputPasswordDialog();
        closeLoadDialog();
        if (loadDialog == null) {
            loadDialog = DialogUtils.createLoadDialog(mContext);
            Log.d("loadDialog", "loadDialog:");
        }
    }

    @Override
    public void closeLoadDialog() {
        Log.d("loadDialog", "关闭load");
        if (loadDialog != null) {
            if (loadDialog.isShowing())
                loadDialog.dismiss();
            loadDialog = null;
        }

    }

    @Override
    public void showAddWifiDialog(DialogUtils.DialogAddWifiCallBack dialogAddWifiCallBack) {
        closeAddWifiDialog();
        if (addWifiDialog == null) {
            addWifiDialog = DialogUtils.createAddWifiDialog(mContext, dialogAddWifiCallBack);
        }
    }

    @Override
    public void closeAddWifiDialog() {
        if (addWifiDialog != null) {
            if (addWifiDialog.isShowing())
                addWifiDialog.dismiss();
            addWifiDialog = null;
        }
    }

    @Override
    public void openToggle() {
        mWifiToggle.setChecked(true);
        mWifiToggle.setBackgroundResource(R.drawable.ic_set_on);
    }

    @Override
    public void closeToggle() {
        mWifiToggle.setChecked(false);
        mWifiToggle.setBackgroundResource(R.drawable.ic_set_off);
    }


    @Override
    public void showOpenTip() {
        if (layout_type == StyleConfig.TYPE1_1 || layout_type == StyleConfig.TYPE1_2) {
            mListView.setVisibility(View.INVISIBLE);
        } else if (layout_type == StyleConfig.TYPE2_1 || layout_type == StyleConfig.TYPE2_2) {
            mGridview.setVisibility(View.INVISIBLE);
        } else {
            mListView.setVisibility(View.INVISIBLE);
        }
        mTip.setText("正在扫描Wifi...");
        mTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCloseTip() {
        if (layout_type == StyleConfig.TYPE1_1 || layout_type == StyleConfig.TYPE1_2) {
            mListView.setVisibility(View.INVISIBLE);
        } else if (layout_type == StyleConfig.TYPE2_1 || layout_type == StyleConfig.TYPE2_2) {
            mGridview.setVisibility(View.INVISIBLE);
        } else {
            mListView.setVisibility(View.INVISIBLE);
        }
        mTip.setText("Wifi未打开");
        mTip.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            openToggle();
            mPresenterImpl.openWifiAndScan(mListScanResult);
        } else {
            closeToggle();
            mPresenterImpl.closeSystemWifi();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeLoadDialog();
        mPresenterImpl.destory();
    }


}
