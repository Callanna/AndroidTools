package com.ebanswers.cleantool;

import android.content.Context;
import android.util.Log;

import com.ebanswers.cleantool.task.CleanTask;
import com.ebanswers.cleantool.tools.RootUtil;
import com.ebanswers.cleantool.tools.ShellUtils;
import com.ebanswers.cleantool.ui.CleanLayout;
import com.ebanswers.cleantool.ui.SpeedUpLayout;

import java.util.LinkedList;


/**
 * Created by Callanna on 2017/2/21.
 */

public class CleanManager {
    private static CleanManager instance;
    private Context mContext;
    private CleanManager(Context context){
        this.mContext = context;
        if(!ShellUtils.checkRootPermission()){
            Log.d("ROOT","root ing");
            RootUtil.preparezlsu(mContext);
        }
    }
    public static CleanManager getInstance(Context context){
        synchronized (CleanManager.class){
            if(instance == null){
                instance = new CleanManager(context);
            }
            return instance;
        }
    }


    public void showClean( ){
        CleanLayout.getInstance(mContext).showFloatLayout();
    }

    public void hideClean(){
        CleanLayout.getInstance(mContext).hideFloatLayout();
    }

    public void showSpeedUp(){
        SpeedUpLayout.getInstance(mContext).showFloatLayout();
    }

    public void hideSpeedUp(){
        SpeedUpLayout.getInstance(mContext).hideFloatLayout();
    }

    public void cleanOtherData(LinkedList<String> notDeleteFileNamne){
        CleanTask.getInstance(mContext).setListFiles(notDeleteFileNamne);
    }

    public void setIsCleanOtherData(boolean b) {
        if(b ) {
            LinkedList<String> listFiles = new LinkedList<>();
            listFiles.add("Android");
            listFiles.add("Alarms");
            listFiles.add("DCIM");
            listFiles.add("Movies");
            listFiles.add("Music");
            listFiles.add("Video");
            CleanTask.getInstance(mContext).setListFiles(listFiles);
        }else{
            CleanTask.getInstance(mContext).setListFiles(null);
        }
    }
}
