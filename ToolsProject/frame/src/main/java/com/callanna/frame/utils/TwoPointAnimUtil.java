package com.callanna.frame.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author
 * Created by Callanna on 2016/10/24.
 */

public class TwoPointAnimUtil {

    public static final int LineAnim = 0x01;

    public static final int ParabolicAnim = 0x02;

    private static TwoPointAnimUtil instance;
    private Context mContext;
    private PathMeasure mPathMeasure;
    private float[] mCurrentPosition = new float[2];

    private TwoPointAnimUtil(Activity context){
        mContext = context;
        animation_viewGroup = createAnimLayout(context);
    }

    public static TwoPointAnimUtil getInstance(Activity context){
        if(instance == null){
            instance = new TwoPointAnimUtil(context);
        }

        return instance;
    }
    //动画时间
    private int AnimationDuration = 1000;
    //正在执行的动画数量
    private int number = 0;
    //是否完成清理
    private boolean isClean = false;
    private FrameLayout animation_viewGroup;
    private Drawable drawable;
    private int[] start_location = new int[2];
    private Handler myHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 0:
                    //用来清除动画后留下的垃圾
                    try{
                        animation_viewGroup.removeAllViews();
                    }catch(Exception e){

                    }
                    isClean = false;
                    break;
                default:
                    break;
            }
        }
    };
    public void doAnim(int animtype,Drawable drawable, int[] start_location,int[] end_location){

        if(!isClean){
            if(animtype == ParabolicAnim){
                setParabolicAnim(drawable, start_location, end_location);
            }else {
                setAnim(drawable, start_location, end_location);
            }
        }else{
            try{
                animation_viewGroup.removeAllViews();
                isClean = false;
                if(animtype == ParabolicAnim){
                    setParabolicAnim(drawable, start_location, end_location);
                }else {
                    setAnim(drawable, start_location, end_location);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            finally{
                isClean = true;
            }
        }
    }
    /**
     * @Description: 创建动画层
     * @param
     * @return void
     * @throws
     */
    private FrameLayout createAnimLayout(Activity activity){
        ViewGroup rootView = (ViewGroup)activity.getWindow().getDecorView();
        FrameLayout animLayout = new FrameLayout(activity);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;

    }

    /**
     * @deprecated 将要执行动画的view 添加到动画层
     * @param vg
     *        动画运行的层 这里是frameLayout
     * @param view
     *        要运行动画的View
     * @param location
     *        动画的起始位置
     * @return
     */
    private View addViewToAnimLayout(ViewGroup vg, View view, int[] location){
        int x = location[0];
        int y = location[1];
        vg.addView(view);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                dip2px(mContext,60),dip2px(mContext,60));
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);

        return view;
    }
    /**
     * dip，dp转化成px 用来处理不同分辨路的屏幕
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale +0.5f);
    }

    /**
     * 动画效果设置
     * @param drawable
     *       将要加入购物车的商品
     * @param start_location
     *        起始位置
     */
    private void setAnim(Drawable drawable,int[] start_location,int[] end_location){


        Animation mScaleAnimation = new ScaleAnimation(1.5f,0.0f,1.5f,0.0f,Animation.RELATIVE_TO_SELF,0.1f,Animation.RELATIVE_TO_SELF,0.1f);
        mScaleAnimation.setDuration(AnimationDuration);
        mScaleAnimation.setFillAfter(true);


        final ImageView iview = new ImageView(mContext);
        iview.setImageDrawable(drawable);
        final View view = addViewToAnimLayout(animation_viewGroup,iview,start_location);
        view.setAlpha(0.6f);

//        int[] end_location = new int[2];
//        imbtnCart.getLocationInWindow(end_location);
        int endX = end_location[0]-start_location[0];
        int endY = end_location[1]-start_location[1];

        Animation mTranslateAnimation = new TranslateAnimation(0,endX,0,endY);
        Animation mRotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(AnimationDuration);
        mTranslateAnimation.setDuration(AnimationDuration);
        AnimationSet mAnimationSet = new AnimationSet(true);

        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(mRotateAnimation);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mTranslateAnimation);
        mAnimationSet.setInterpolator(new DecelerateInterpolator());
        mAnimationSet.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

                number--;
                if(number==0){
                    isClean = true;
                    myHandler.sendEmptyMessage(0);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

        });
        view.startAnimation(mAnimationSet);

    }

   //两点之间半抛物线
   private void setParabolicAnim(Drawable drawable, final int[] start_location, int[] end_location){
       int startX = start_location[0];
       int startY = start_location[1];

       int toX = end_location[0];
       int toY = end_location[1];

       final ImageView iview = new ImageView(mContext);
       iview.setImageDrawable(drawable);
       final View view = addViewToAnimLayout(animation_viewGroup,iview,start_location);
       view.setAlpha(0.9f);
       //开始绘制贝塞尔曲线
       Path path = new Path();
       //移动到起始点（贝塞尔曲线的起点）
       path.moveTo(startX, startY);
       //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
       path.quadTo((startX + toX) / 2, toY, toX, toY);
       //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
       // 如果是true，path会形成一个闭环
       mPathMeasure = new PathMeasure(path, false);

       //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
       ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
       valueAnimator.setDuration(AnimationDuration);
       // 匀速线性插值器
       valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
       valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
           @Override
           public void onAnimationUpdate(ValueAnimator animation) {
               // 当插值计算进行时，获取中间的每个值，
               // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
               float value = (Float) animation.getAnimatedValue();
               // ★★★★★获取当前点坐标封装到mCurrentPosition
               // boolean getPosTan(float distance, float[] pos, float[] tan) ：
               // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
               // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
               mPathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
               // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
               Log.d("duan","duanyl==========>cx:"+mCurrentPosition[0]+",cy:"+mCurrentPosition[1]);
               view.post(new Runnable() {
                   @Override
                   public void run() {
                       view.setX(mCurrentPosition[0]);
                       view.setY(mCurrentPosition[1]);
                   }
               });

           }
       });

       valueAnimator.addListener(new Animator.AnimatorListener() {
           @Override
           public void onAnimationStart(Animator animator) {
               number++;
           }

           @Override
           public void onAnimationEnd(Animator animator) {

               number--;
               if(number==0){
                   isClean = true;
                   myHandler.sendEmptyMessage(0);
               }
           }

           @Override
           public void onAnimationCancel(Animator animator) {

           }

           @Override
           public void onAnimationRepeat(Animator animator) {

           }
       });
       valueAnimator.start();
   }

  public void clean(){
      if(animation_viewGroup != null) {
          animation_viewGroup.removeAllViews();
          isClean = false;
          instance = null;
      }
  }
}
