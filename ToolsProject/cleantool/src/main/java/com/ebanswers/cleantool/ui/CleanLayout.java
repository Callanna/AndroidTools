package com.ebanswers.cleantool.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ebanswers.cleantool.R;
import com.ebanswers.cleantool.data.CacheListItem;
import com.ebanswers.cleantool.data.SDCardInfo;
import com.ebanswers.cleantool.task.CleanTask;
import com.ebanswers.cleantool.tools.AppUtils;
import com.ebanswers.cleantool.tools.FloatWindowUtil;
import com.ebanswers.cleantool.tools.StorageUtil;
import com.ebanswers.cleantool.view.DynamicCircleProgressBar;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Callanna on 2017/2/21.
 */

public class CleanLayout implements View.OnClickListener {
    private Context mContext;
    private View mView;
    private DynamicCircleProgressBar progressBarView;
    private TextView tv_cleantip;
    private View top, left, right, bottom;
    private Button btn_clean;
    private boolean isShow = false;
    private static CleanLayout instance;

    long nAvailaBlock;
    long TotalBlocks;
    private CleanTask.OnActionListener onActionListener = new CleanTask.OnActionListener() {
        @Override
        public void onScanStarted() {
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    progressBarView.setCenterText(mContext.getString(R.string.scaning));
                    countMemory();
                }
            });

        }

        @Override
        public void onScanProgressUpdated(int current, int max, final long cacheSize, String packageName) {
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    progressBarView.setCenterText(StorageUtil.convertStorage(cacheSize)+mContext.getString(R.string.canclean));
                }
            });
        }

        @Override
        public void onScanCompleted(List<CacheListItem> apps) {
            startCountMemory();
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    float p = nAvailaBlock * 1.0f/TotalBlocks * 100.0f;
                    progressBarView.setProgress(p);
                    btn_clean.setEnabled(true);
                    btn_clean.setText(mContext.getString(R.string.clicktoclean));
                    tv_cleantip.setText(mContext.getString(R.string.lastcache)+ AppUtils.convertStorage(nAvailaBlock)+"/"+AppUtils.convertStorage(TotalBlocks));
                }
            });
        }

        @Override
        public void onCleanStarted() {
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    progressBarView.setCenterText(mContext.getString(R.string.cleaning));
                }
            });
        }

        @Override
        public void onCleanCompleted(final long cacheSize) {
            Log.d("Clean","cacheSize:"+cacheSize);
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    progressBarView.setCenterText("0B "+mContext.getString(R.string.canclean));
                    btn_clean.setText(mContext.getString(R.string.cleaned));
                    btn_clean.setEnabled(false);
                    Toast.makeText(mContext,mContext.getString(R.string.cleanchcahe)+AppUtils.convertStorage(Math.abs(cacheSize)),Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void countMemory() {
        SDCardInfo mSDCardInfo = StorageUtil.getSDCardInfo();
        SDCardInfo mSystemInfo = StorageUtil.getSystemSpaceInfo(mContext);
        if (mSDCardInfo != null) {
            nAvailaBlock = mSDCardInfo.free + mSystemInfo.free;
            TotalBlocks = mSDCardInfo.total + mSystemInfo.total;
        } else {
            nAvailaBlock = mSystemInfo.free;
            TotalBlocks = mSystemInfo.total;
        }
    }

    private ScheduledExecutorService singleThreadPoll;

    public static CleanLayout getInstance(Context context) {
        if (instance == null) {
            synchronized (CleanLayout.class) {
                instance = new CleanLayout(context);
            }
        }
        return instance;
    }


    private CleanLayout(Context context) {
        this.mContext = context;
        initView();
        CleanTask.getInstance(mContext).setmOnActionListener(onActionListener);
    }


    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_clean, null);
        progressBarView = (DynamicCircleProgressBar) mView.findViewById(R.id.progress_clean);
        tv_cleantip = (TextView) mView.findViewById(R.id.memory_tip);
        progressBarView.setCenterText(mContext.getString(R.string.clicktoclean));
        progressBarView.setOnClickListener(this);
        top = mView.findViewById(R.id.top);
        left = mView.findViewById(R.id.left);
        right = mView.findViewById(R.id.right);
        bottom = mView.findViewById(R.id.bottom);
        btn_clean = (Button) mView.findViewById(R.id.btn_clean);
//        progressBarView.setOnClickListener(this);
        btn_clean.setOnClickListener(this);
        top.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        bottom.setOnClickListener(this);
    }

    private void startCountMemory(){
        stopCountMemory();
        singleThreadPoll = Executors.newScheduledThreadPool(1);
        singleThreadPoll.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                countMemory();
                progressBarView.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBarView.setProgressSync(nAvailaBlock*1.0f/TotalBlocks * 100.0f);
                        tv_cleantip.setText(mContext.getString(R.string.lastcache)+AppUtils.convertStorage(nAvailaBlock)+"/"+AppUtils.convertStorage(TotalBlocks));
                    }
                });
            }
        },10,1000, TimeUnit.MILLISECONDS);
    }
    private void stopCountMemory(){

        if(singleThreadPoll != null){
            singleThreadPoll.shutdown();
            singleThreadPoll = null;
        }
    }
    /**
     * 显示
     */
    public void showFloatLayout() {
        FloatWindowUtil.getIntsance().showPopupWindow(mContext.getApplicationContext(), mView, FloatWindowUtil.FLOAT_MAIN);
        CleanTask.getInstance(mContext).startScanCache();
        isShow = true;
    }

    /**
     * 隐藏
     */
    public void hideFloatLayout() {
        FloatWindowUtil.getIntsance().hidePopupWindow(FloatWindowUtil.FLOAT_MAIN);
        stopCountMemory();
        isShow = false;
    }

    public boolean isShow() {
        return isShow;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top || i == R.id.left || i == R.id.right || i == R.id.bottom) {
            hideFloatLayout();
        } else if(i == R.id.btn_clean){
            CleanTask.getInstance(mContext).cleanCache();
        }
    }
}
