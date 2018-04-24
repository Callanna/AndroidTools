package com.ebanswers.wifilibrary.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ebanswers.wifilibrary.R;
import com.ebanswers.wifilibrary.WifiAdmin;
import com.ebanswers.wifilibrary.WifiConfig;

/**
 * @author Created by lishihui on 2017/4/11.
 */

public class DialogUtils {
    public static Dialog createPassWordDialog(final Context activity, final ScanResult scanResult, final DialogCallBack callBack) {

        final Dialog dialog = new Dialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        try {
            Context context = dialog.getContext();
            int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = dialog.findViewById(divierId);
            divider.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            //上面的代码，是用来去除Holo主题的蓝色线条
            e.printStackTrace();
        }
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        if (width > height) {
            window.setLayout(width / 2, WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            window.setLayout(width - 80, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        View view = View.inflate(activity, R.layout.dialog_input_password, null);
        TextView name = (TextView) view.findViewById(R.id.id_tv_dialog_wifi_ssid_name);
        final EditText password = (EditText) view.findViewById(R.id.id_et_dialog_password);
        final CheckBox showPassWord = (CheckBox) view.findViewById(R.id.id_cb_dialog_show_password);
        final TextView connect = (TextView) view.findViewById(R.id.id_tv_dialog_connect_wifi);
        final TextView forget = (TextView) view.findViewById(R.id.id_tv_dialog_forget);
        final TextView close = (TextView) view.findViewById(R.id.id_tv_dialog_close);
        name.setText(scanResult.SSID);
        Log.d("lishihui_wifi","ssid:"+scanResult.SSID);
        Log.d("lishihui_wifi","netId:"+WifiAdmin.getInstance(activity).IsConfiguration("\""+scanResult.SSID+"\""));
        if (WifiAdmin.getInstance(activity).IsConfiguration("\""+scanResult.SSID+"\"") != -1) {
            password.setVisibility(View.GONE);
            showPassWord.setVisibility(View.GONE);
        } else {
            password.setVisibility(View.VISIBLE);
            showPassWord.setVisibility(View.VISIBLE);
        }
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8) {
                    connect.setEnabled(true);
                } else {
                    connect.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (!TextUtils.isEmpty(WifiConfig.getInstance(activity).getPasswd(scanResult.SSID))) {
            password.setText(WifiConfig.getInstance(activity).getPasswd(scanResult.SSID));
            password.setSelection(WifiConfig.getInstance(activity).getPasswd(scanResult.SSID).length());
        }
        showPassWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                password.setSelection(password.getText().toString().trim().length());
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.callBack(password, scanResult, password.getText().toString().trim());
                }
            }
        });

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.ignore();
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.cancel();
                }
            }
        });
        window.setContentView(view);
        if (activity instanceof Activity) {
            Activity ac = (Activity) activity;
            if (!ac.isFinishing()) {
                dialog.show();
            }
        }
        return dialog;
    }


    public static Dialog createLoadDialog(Context activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        if (activity instanceof Activity) {
            Activity ac = (Activity) activity;
            if (!ac.isFinishing()) {
                alertDialog.show();
            }
        }
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        if (width > height) {
            window.setLayout(width / 3, WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            window.setLayout(width - 80, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        View view = View.inflate(activity, R.layout.dialog_load_layout, null);
        window.setContentView(view);

        return alertDialog;
    }

    public static Dialog createForgetWifiDialog(final Context activity, final ScanResult scanResult, final DialogCallBack dialogCallBack) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        if (activity instanceof Activity) {
            Activity ac = (Activity) activity;
            if (!ac.isFinishing()) {
                alertDialog.show();
            }
        }
        alertDialog.setCanceledOnTouchOutside(false);
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        if (width > height) {
            window.setLayout(width / 2, WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            window.setLayout(width - 80, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        View view = View.inflate(activity, R.layout.dialog_disconnect_layout, null);
        TextView forget = (TextView) view.findViewById(R.id.id_tv_dialog_forget_wifi);
        TextView close = (TextView) view.findViewById(R.id.id_tv_dialog_forget_close);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogCallBack != null) {
                    dialogCallBack.callBack(v, scanResult, "");
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogCallBack != null) {
                    dialogCallBack.cancel();
                }
            }
        });
        window.setContentView(view);
        return alertDialog;
    }


    public static Dialog createAddWifiDialog(final Context activity, final DialogAddWifiCallBack dialogAddWifiCallBack) {
        final Dialog dialog = new Dialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        try {
            Context context = dialog.getContext();
            int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = dialog.findViewById(divierId);
            divider.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {
            //上面的代码，是用来去除Holo主题的蓝色线条
            e.printStackTrace();
        }
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;
        if (width > height) {
            window.setLayout(width / 2, WindowManager.LayoutParams.WRAP_CONTENT);
        } else {
            window.setLayout(width - 80, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        View view = View.inflate(activity, R.layout.dialog_wifi_add_layout, null);
        final EditText wifi_name = (EditText) view.findViewById(R.id.id_et_add_wifi_name);
        final EditText wifi_password = (EditText) view.findViewById(R.id._id_et_add_wifi_password);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.id_rg_group);
        final LinearLayout psd_ll = (LinearLayout) view.findViewById(R.id.id_ll_password_input);
        final TextView connect = (TextView) view.findViewById(R.id.id_tv_dialog_add_wifi_connect);
        final CheckBox psd_cansee = (CheckBox) view.findViewById(R.id.id_cb_password_cansee);
        final TextView close = (TextView) view.findViewById(R.id.id_tv_dialog_add_wifi_close);
        radioGroup.setTag(0);
        psd_cansee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifi_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    wifi_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                wifi_password.setSelection(wifi_password.getText().toString().trim().length());
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.id_rb_open) {
                    radioGroup.setTag(0);
                    psd_ll.setVisibility(View.GONE);
                } else if (checkedId == R.id.id_rb_wep) {
                    radioGroup.setTag(2);
                    psd_ll.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.id_rb_wpa) {
                    radioGroup.setTag(3);
                    psd_ll.setVisibility(View.VISIBLE);
                }
                if (checkedId == R.id.id_rb_open) {
                    if (wifi_name.getText().toString().length() > 0) {
                        connect.setEnabled(true);
                    } else {
                        connect.setEnabled(false);
                    }
                } else {
                    if (wifi_name.getText().toString().length() > 0 && wifi_password.getText().toString().length() >= 8) {
                        connect.setEnabled(true);
                    } else {
                        connect.setEnabled(false);
                    }
                }

            }
        });
        wifi_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    if (psd_ll.getVisibility() == View.VISIBLE) {
                        if (wifi_password.getText().toString().length() >= 8) {
                            connect.setEnabled(true);
                        } else {
                            connect.setEnabled(false);
                        }
                    } else {
                        connect.setEnabled(true);
                    }
                } else {
                    connect.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        wifi_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8 && wifi_name.getText().toString().length() >= 1) {
                    connect.setEnabled(true);
                } else {
                    connect.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogAddWifiCallBack != null) {
                    dialogAddWifiCallBack.callBack(wifi_name.getText().toString().trim(), wifi_password.getText().toString().trim(), (Integer) radioGroup.getTag());
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogAddWifiCallBack != null) {
                    dialogAddWifiCallBack.cancel();
                }
            }
        });
        window.setContentView(view);
        if (activity instanceof Activity) {
            Activity ac = (Activity) activity;
            if (!ac.isFinishing()) {
                dialog.show();
            }
        }

        return dialog;
    }


    public interface DialogCallBack {
        void callBack(View view, ScanResult scanResult, String str);

        void ignore();

        void cancel();
    }

    public interface DialogAddWifiCallBack {
        void callBack(String ssid, String password, int type);

        void cancel();
    }
}
