package com.ebanswers.cleanmemory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ebanswers.cleantool.ui.CleanLayout;
import com.ebanswers.cleantool.ui.SpeedUpLayout;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * Created by Callanna on 2017/2/21.
 */

public class CleanFloatView  extends LinearLayout implements View.OnClickListener {
    private static CleanFloatView instance;

    private WindowManager wm;
    private WindowManager.LayoutParams windowManagerParams;
    private Context mContext;
    private ImageView imv_clean,imv_speed,imv_menu;
    private boolean isShown = false;
    private OnClickListener listener;
    private View view ;
    private float mRawX, mRawY, mStartX, mStartY;
    private boolean isMove,isShowClean = false;
    private int accurate = 8;//判断是移动还是点击的精确度,太小会导致判断出的都是移动

    private CleanFloatView(Context context) {
        super(context);
        this.mContext = context;
        Log.d("duanyl", "CleanFloatView: "+context.getClass());
        view = View.inflate(context,  R.layout.float_window_clean, null);
        imv_clean = (ImageView) view.findViewById( R.id.imv_clean);
        imv_speed = (ImageView) view.findViewById( R.id.imv_speed);
        imv_menu = (ImageView) view.findViewById( R.id.imv_menu);
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManagerParams = new WindowManager.LayoutParams();
        initView();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowClean) {
                    isShowClean = false;
                    imv_speed.setVisibility(GONE);
                    imv_clean.setVisibility(GONE);
                    setMenuRotation(0);
                }else{
                    isShowClean = true;
                    setMenuRotation(45);
                    imv_speed.setVisibility(VISIBLE);
                    imv_clean.setVisibility(VISIBLE);
                }
                delayBack();
            }
        });

        imv_clean.setOnClickListener(this);
        imv_speed.setOnClickListener(this);
    }
    public static CleanFloatView getInstance(Context contxet){
        synchronized (CleanFloatView.class){
            if(instance == null){
                instance = new CleanFloatView(contxet.getApplicationContext());
            }
            return instance;
        }
    }


    //矩阵类,用于对图像进行旋转
    private Matrix matrix = new Matrix();
    private void setMenuRotation(int degrees){
      imv_menu.setRotation(degrees);
    }
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
               closeMenu();
            }
        }
    };

    private void closeMenu() {
        isShowClean = false;
        setMenuRotation(0);
        imv_speed.setVisibility(GONE);
        imv_clean.setVisibility(GONE);
        int sx = (mRawX - mStartX) >wm.getDefaultDisplay().getWidth()/2?wm.getDefaultDisplay().getWidth():0 ;
        Log.d("duanyl", "handleMessage:sx  "+sx);
        windowManagerParams.x = sx;
        windowManagerParams.y = (int) (mRawY - mStartY);
        if (isShown) {
            wm.updateViewLayout(this, windowManagerParams);
        }
    }

    private void delayBack() {
         handler.removeMessages(1);
         handler.sendEmptyMessageDelayed(1,5000);
    }

    /**
     * 初始化相关参数
     */
    private void initView() {
        setLayoutParams(new LayoutParams(WRAP_CONTENT,WRAP_CONTENT));
        setGravity(Gravity.CENTER);
        addView(view);
        windowManagerParams.type = WindowManager.LayoutParams.TYPE_TOAST; // 设置window type
        windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 注意，flag的值可以为： LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
		 * LayoutParams.FLAG_NOT_FOCUSABLE 不可聚焦 LayoutParams.FLAG_NOT_TOUCHABLE
		 * 不可触摸
		 */
        //什么是gravity属性呢？简单地说，就是窗口如何停靠。  当设置了 Gravity.LEFT 或 Gravity.RIGHT 之后，x值就表示到特定边的距离
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，使按钮定位在右下角
        windowManagerParams.x =25;
        windowManagerParams.y = wm.getDefaultDisplay().getHeight();
        // 设置悬浮窗口长宽数据
        windowManagerParams.width = WRAP_CONTENT;
        windowManagerParams.height = WRAP_CONTENT;
    }
    /**
     * 重写onTouchEvent方法，处理移动和点击事件的消费
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //拿到触摸点对于屏幕左上角的坐标
        mRawX = event.getRawX();
        mRawY = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mStartX) > accurate || Math.abs(event.getY() - mStartY) > accurate) {
                    isMove = true;
                    updatePosition();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    updatePosition();
                } else {
                    if (listener != null) {
                        listener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    private void updatePosition() {
        windowManagerParams.x = (int) (mRawX - mStartX);
        windowManagerParams.y = (int) (mRawY - mStartY);
        if (isShown) {
            wm.updateViewLayout(this, windowManagerParams);
        }
        delayBack();
    }

    public void show() {
        // 显示myFloatView图像
        if(!isShown) {
            wm.addView(this, windowManagerParams);
            isShown = true;
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.listener = l;
    }

    public void dismiss() {
        if( isShown) {
            wm.removeView(this);
            isShown = false;
        }
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void onClick(View v) {
        closeMenu();
        switch (v.getId()){
            case  R.id.imv_clean:
                CleanLayout.getInstance(mContext).showFloatLayout();
                break;
            case R.id.imv_speed:
                SpeedUpLayout.getInstance(mContext).showFloatLayout();
                break;
        }
    }
}
