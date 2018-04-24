package com.callanna.frame.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * describe
 * Created by liudong on 2016/9/23.
 */
public class DialogUtil {

    /**
     * 修改系统自带Dialog的样式
     *
     * @param dialog
     * @param color
     */
    public static void dialogTitleLineColor(Dialog dialog, int color) {
        Context context = dialog.getContext();
        int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = dialog.findViewById(divierId);
        if (divider != null) {
            divider.setBackgroundColor(color);
        }
        int titleId = context.getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView title = (TextView) dialog.findViewById(titleId);
        if (title != null) {
            title.setTextColor(color);
        }
        int textId = context.getResources().getIdentifier("android:id/alertText", null, null);
        TextView text = (TextView) dialog.findViewById(textId);
        if (text != null) {
            text.setTextColor(color);
        }
    }
}
