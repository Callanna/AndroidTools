package com.ebanswers.cleantool.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanswers.cleantool.R;
import com.ebanswers.cleantool.data.AppProcessInfo;
import com.ebanswers.cleantool.task.SpeedUpTask;
import com.ebanswers.cleantool.tools.AppUtils;
import com.ebanswers.cleantool.tools.FloatWindowUtil;
import com.ebanswers.cleantool.view.ProgressBarView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by Callanna on 2017/2/21.
 */

public class SpeedUpLayout implements View.OnClickListener {
    private static ScheduledExecutorService singleThreadPoll;
    private Context mContext;
    private View mView;
    private ProgressBarView progressBarView;
    private TextView tv_speedtip;
    private View top,left,right,bottom;
    private boolean isShow = false;
    private static SpeedUpLayout instance;
    private long totalMemory = AppUtils.getTotalMemory();

    private SpeedUpTask.OnProcessActionListener processActionListener = new SpeedUpTask.OnProcessActionListener() {
        @Override
        public void onScanStarted() {
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    totalMemory = AppUtils.getTotalMemory();
                    Log.d("DUAN","app memory:"+totalMemory);
                    progressBarView.setCenterText(mContext.getString(R.string.scaning));
                }
            });
        }

        @Override
        public void onScanProgressUpdated(int current, int max, final long memory, final String processName) {
            Log.d("SPEED","current:"+current+"max:"+max+"memory:"+memory+"processName:"+processName);
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    float p = memory * 1.0f/totalMemory * 100.0f;
                    Log.d("duanyl", "run  p: "+p);
                    progressBarView.setProgressSync(p);
                    tv_speedtip.setText(mContext.getString(R.string.lastmemory)+AppUtils.convertStorage(memory)+"/"+AppUtils.convertStorage(totalMemory));
                }
            });
        }

        @Override
        public void onScanCompleted(List<AppProcessInfo> apps) {
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    progressBarView.setEnabled(true);
                    progressBarView.setCenterText(mContext.getString(R.string.clicktoclean));
                }
            });
            startCountMemory();
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
            Log.d("SPEED","cachesize:"+cacheSize);
            progressBarView.post(new Runnable() {
                @Override
                public void run() {
                    progressBarView.setCenterText(mContext.getString(R.string.cleaned));
                    Toast.makeText(mContext,mContext.getString(R.string.cleanmemory)+AppUtils.convertStorage(Math.abs(cacheSize)),Toast.LENGTH_LONG).show();
                }
            });
           }
    };

    public static SpeedUpLayout getInstance(Context context) {
        if (instance == null) {
            synchronized (SpeedUpLayout.class) {
                instance = new SpeedUpLayout(context);
            }
        }
        return instance;
    }

    private void startCountMemory(){
        stopCountMemory();
        singleThreadPoll = Executors.newScheduledThreadPool(1);
        singleThreadPoll.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                final long memory = totalMemory  - AppUtils.getAvailMemory(mContext);
                Log.d("duanyl","memory now:"+memory);
                progressBarView.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBarView.setProgressSync(memory*1.0f/totalMemory * 100.0f);
                        tv_speedtip.setText(mContext.getString(R.string.lastmemory)+AppUtils.convertStorage(memory)+"/"+AppUtils.convertStorage(totalMemory));
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

    private SpeedUpLayout(Context context) {
        this.mContext = context;
        initView();
        SpeedUpTask.getInstance(mContext).setOnActionListener(processActionListener);
    }


    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_speedup, null);
        progressBarView = (ProgressBarView) mView.findViewById(R.id.progress_speed);
        tv_speedtip = (TextView) mView.findViewById(R.id.speed_tip);
        top = mView.findViewById(R.id.top);
        left = mView.findViewById(R.id.left);
        right = mView.findViewById(R.id.right);
        bottom = mView.findViewById(R.id.bottom);
        progressBarView.setCenterText(mContext.getString(R.string.clicktoclean));
        progressBarView.setOnClickListener(this);
        top.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        bottom.setOnClickListener(this);
    }
    /**
     * 显示
     *
     */
    public void showFloatLayout() {
        FloatWindowUtil.getIntsance().showPopupWindow(mContext.getApplicationContext(), mView, FloatWindowUtil.FLOAT_MAIN);
        SpeedUpTask.getInstance(mContext).stratScanProcess();
        isShow = true;
    }
    /**
     * 隐藏
     */
    public void hideFloatLayout() {
        FloatWindowUtil.getIntsance().hidePopupWindow(FloatWindowUtil.FLOAT_MAIN);
        isShow = false;
        stopCountMemory();
    }

    public boolean isShow() {
        return isShow;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top || i == R.id.left || i == R.id.right || i == R.id.bottom) {
            hideFloatLayout();

        } else if (i == R.id.progress_speed) {//一键加速
            SpeedUpTask.getInstance(mContext).killAllProcess();
            progressBarView.setEnabled(false);

        }
    }
}
