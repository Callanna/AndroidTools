package com.callanna.frame.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.callanna.frame.utils.KeyBoardUtils;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;


/**
 * Activity基类
 * <p>
 * 继承：
 * 1）onBaseCreate();
 * 2) 继承setLayoutId（）或者 setLayoutView()
 * 3）继承isFullScreen()设置是否全屏
 * 4）继承showToolBar()设置是否显示ToolBar
 * 5）App-gradle内添加：
 * apply plugin: 'com.neenbedankt.android-apt'
 * apt 'com.jakewharton:butterknife-compiler:8.4.0'
 * compile 'com.jakewharton:butterknife:8.4.0'
 * <p>
 * Created by liudong on 2016/9/18.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public Map<String, Fragment> mFragments = new HashMap<>();
    private FragmentTransaction fragmentTransaction;

    protected abstract void onBaseCreate(Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (!showToolBar()) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        }
        View contentView = onViewCreate();
        if (isFullScreen() && contentView != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(contentView);
        onBaseCreate(savedInstanceState);
    }

    /**
     * 是否显示系统标题栏
     */
    public boolean showToolBar() {
        return false;
    }

    /**
     * 获取ContentView
     *
     * @return
     */
    protected abstract View onViewCreate();

    /**
     * 是否全屏
     */
    public boolean isFullScreen() {
        return false;
    }

    /**
     * 更换Fragment
     */
    public void replaceFragment(int id, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (tag == null || tag.equals("")) {
            fragmentTransaction.replace(id, fragment);
        } else {
            fragmentTransaction.replace(id, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
        }
        KeyBoardUtils.closeKeyboard(this);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 添加Fragments
     *
     * @param id       要替换的View id
     * @param fragment 要添加的Fragment
     * @param tag      标记
     */
    public void addFragments(int id, Fragment fragment, String tag) {
        if (TextUtils.isEmpty(tag) || fragment == null) {
            return;
        }
        if (mFragments.get(tag) == null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(id, fragment, tag);
            fragmentTransaction.commitAllowingStateLoss();
            mFragments.put(tag, fragment);
        }
        //同时需要隐藏其他fragment
        showFragment(tag);
    }

    /**
     * 显示当前Fragment
     *
     * @param tag 标记
     */
    public void showFragment(String tag) {
        KeyBoardUtils.closeKeyboard(this);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        for (String fragmentkey : mFragments.keySet()) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (tag.equals(fragmentkey)) {
                fragmentTransaction.show(mFragments.get(fragmentkey));
            } else {
                fragmentTransaction.hide(mFragments.get(fragmentkey));
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
