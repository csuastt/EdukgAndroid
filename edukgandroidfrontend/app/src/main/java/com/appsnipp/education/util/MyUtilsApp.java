/*
 * Copyright (c) 2021. rogergcc
 */
/***
 * This is a file which wraps toast and log and is customized to show
 * @author Shuning Zhang
 * @version 1.0
 */


package com.appsnipp.education.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public final class MyUtilsApp {
    private MyUtilsApp(){}
    public static void showToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLog(String TAG,String message){
        Log.d(TAG, "showLog: "+ message);
    }
}
