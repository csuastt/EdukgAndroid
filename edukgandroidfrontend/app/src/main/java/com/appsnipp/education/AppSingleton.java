/*
 * Copyright (c) 2021. rogergcc
 */
/***
 * This is an application singleton file which can create an concrete instance of the singleton
 * @author Shuning Zhang
 * @version 1.0
 */

package com.appsnipp.education;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.appsnipp.education.widget.ACache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 这个单例主要用于发送HTTP请求
// 生命周期和App相同
public class AppSingleton {
    private static AppSingleton instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    public static final String URL_PREFIX = "";
    public static final String EDUKG_PREFIX = "http://open.edukg.cn/opedukg";
    public static final String EUDKG_PHONE = "";
    public static final String EDUKG_PASSWORD = "";
    public static final String STATIC_ID = "b00c6e76-1ab4-4e2d-a1f5-de76443bac56";
    public static ArrayList<String> selectedCourse = new ArrayList<>();
    public static ArrayList<String> notSelected = new ArrayList<>();
    public static String nowCourse = "";
    public static String searchString = "";
    public static long endTime;
    public static long startTime;
    public static ACache acache;
    public static Map<String, String> courseMap = new HashMap<>();
    public static Map<String, String> courseInverseMap = new HashMap<>();
    public static final String[] courseArray = {"数学","语文","英语","物理","化学","生物","历史","地理","政治"};
    private AppSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized AppSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new AppSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    // 下面的函数用来换算dp和像素
    public static int dip2px(Context context, float dipValue)
    {
        float m=context.getResources().getDisplayMetrics().density ;

        return (int)(dipValue * m + 0.5f) ;

    }



    public static int px2dip(Context context, float pxValue)

    {
        float m=context.getResources().getDisplayMetrics().density;

        return (int)(pxValue / m + 0.5f) ;

    }
}
