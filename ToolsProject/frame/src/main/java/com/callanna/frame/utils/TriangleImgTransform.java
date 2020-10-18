package com.callanna.frame.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

//import com.ebanswers.netkitchen.CoreApplication;
//import com.ebanswers.netkitchen.R;
//import com.squareup.picasso.Transformation;

/**
 * 生成微信带三角的图片
 * <p>
 * Created by liudong on 2016/10/21.
 */
public class TriangleImgTransform { //implements Transformation {

    public Bitmap getRoundCornerImage(Bitmap bitmap_bg, Bitmap bitmap_in) {
        Bitmap roundConcerImage = Bitmap.createBitmap(bitmap_in.getWidth(), bitmap_in.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        Rect rectF = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap_in, rectF, rect, paint);
        return roundConcerImage;
    }

    public Bitmap getShardImage(Bitmap bitmap_bg, Bitmap bitmap_in) {
        Bitmap roundConcerImage = Bitmap.createBitmap(bitmap_in.getWidth(), bitmap_in.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        //Rect rect2 = new Rect(10, 10, bitmap_in.getWidth() - 10, bitmap_in.getHeight() - 10);
        canvas.drawBitmap(bitmap_in, rect, rect, paint);
        return roundConcerImage;
    }

//    @Override
//    public Bitmap transform(Bitmap source) {
//        Bitmap bitmap_bg = BitmapFactory.decodeResource(CoreApplication.getInstance().getResources(), R.drawable.bg_dialog_a);
//        final Bitmap bp = getRoundCornerImage(bitmap_bg, source);
//        Log.d("TriangleImgTransform", "transform: source.width :" + source.getWidth() + " height:" + source.getHeight());
//        Bitmap bp2 = getShardImage(bitmap_bg, bp);
//        source.recycle();
//        return bp2;
//    }
//
//    @Override
//    public String key() {
//        return "TriangleImgTransform";
//    }
}
