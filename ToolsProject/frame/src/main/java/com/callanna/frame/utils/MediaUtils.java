package com.callanna.frame.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * Description  音乐媒体播放工具类,因为是静态，所以每次使用前请release。
 * Created by chenqiao on 2015/10/16.
 */
public class MediaUtils {
    /**
     * 播放Assets中的媒体文件
     *
     * @param context  上下文
     * @param fileName 媒体文件名
     */
    public static void playFromAssets(Context context, String fileName) {
        try {
            AssetFileDescriptor fileDescript = context.getAssets().openFd(fileName);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileDescript.getFileDescriptor(), fileDescript.getStartOffset(), fileDescript.getLength());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    Log.d("setOnCompletionListener","===============");
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MediaPlayer playFromAssets(Context context, String fileName, final boolean isLooping) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor fileDescript = context.getAssets().openFd(fileName);
            mediaPlayer.setDataSource(fileDescript.getFileDescriptor(), fileDescript.getStartOffset(), fileDescript.getLength());
            if (isLooping) {
                mediaPlayer.setLooping(true);
            }
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!isLooping) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                    Log.d("loop","===============");
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
            return mediaPlayer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    /**
     * 播放资源中的媒体文件
     *
     * @param context 上下文
     * @param resId   资源ID
     */
    public static void playFromRes(Context context, int resId, final boolean isLoop) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.setLooping(isLoop);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!isLoop) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放Uri指向的媒体文件
     *
     * @param context 上下文
     * @param uri     资源Uri
     * @param isLoop  是否循环播放
     */
    public static void playFromUri(Context context, Uri uri, final boolean isLoop) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, uri);
            mediaPlayer.setLooping(isLoop);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (!isLoop) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            LogUtils.d("duanyl==========MediaPlayer>" + e.getMessage());
            e.printStackTrace();
        }
    }
}
