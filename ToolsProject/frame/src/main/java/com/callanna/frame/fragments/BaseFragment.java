package com.callanna.frame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.callanna.frame.activity.BaseActivity;
import com.callanna.frame.utils.KeyBoardUtils;

import org.simple.eventbus.EventBus;



/**
 * Fragment基类
 * <p>
 * 继承：
 * 1）onBaseFragmentCreate()
 * 2）继承setLayoutId（）或者 setLayoutView()
 * 3) App-gradle内添加：
 * apply plugin: 'com.neenbedankt.android-apt'
 * apt 'com.jakewharton:butterknife-compiler:8.4.0'
 * compile 'com.jakewharton:butterknife:8.4.0'
 * <p>
 * Created by liudong on 2016/9/18.
 */
public abstract class BaseFragment extends Fragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    public View mContentView;
    public BaseActivity mContext;
    private boolean isRegisterEventBus = false;
    private FragmentManager fragmentManager;
    private FragmentManager childFragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (BaseActivity) getActivity();
        int layoutId = getLayoutId();
        if (layoutId != 0 && mContentView == null) {
            mContentView = inflater.inflate(layoutId, container, false);
        }
        fragmentManager = getActivity().getSupportFragmentManager();
        childFragmentManager = getChildFragmentManager();
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onBaseFragmentCreate(savedInstanceState);
        //状态恢复
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setBaseContentView(View view) {
        mContentView = view;
    }
    /**
     * 获取LayoutId
     *
     * @return
     */
    protected abstract int getLayoutId();

    protected abstract void onBaseFragmentCreate(Bundle savedInstanceState);

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus) {
            EventBus.getDefault().unregister(this);
            isRegisterEventBus = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //状态保存，防止Show和hide恢复混乱
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    public void registerEventBus() {
        EventBus.getDefault().register(this);
        isRegisterEventBus = true;
    }


    /**
     * 返回，退出当前Fragment
     *
     * @author YOLANDA
     */
    public void finish() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    /**
     * 更换Fragment
     */
    public void replaceFragment(int id, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (tag == null || tag.equals("")) {
            fragmentTransaction.replace(id, fragment);
        } else {
            fragmentTransaction.replace(id, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
        }
        KeyBoardUtils.closeKeyboard(getActivity());
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 替换该Fragment内部的layout显示为fragment
     */
    public void replaceChildFragment(int id, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        if (tag == null || tag.equals("")) {
            fragmentTransaction.replace(id, fragment);
        } else {
            fragmentTransaction.replace(id, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
        }
        KeyBoardUtils.closeKeyboard(getActivity());
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 退出当前Fragment内部的Fragment
     */
    protected void finishChild() {
        if (childFragmentManager.getBackStackEntryCount() > 0) {
            childFragmentManager.popBackStack();
        }
    }

}