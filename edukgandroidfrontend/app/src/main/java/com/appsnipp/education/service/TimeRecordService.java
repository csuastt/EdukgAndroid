/***
 * This media Image Observer class is a class containing the media Image loading process file
 * @author Shuning Zhang
 * @version 1.0
 */
package com.appsnipp.education.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appsnipp.education.AppSingleton;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeRecordService extends Service {
    public TimeRecordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("out");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("test".equals(intent.getAction())){
                    Log.e("EDUKG_DEBUG", "onReceive");
                    /**发请求*/
                    AppSingleton.endTime = new Date().getTime();
                    String url = AppSingleton.URL_PREFIX +  "/api/add_time";
                    System.err.println(url);
                    // build request
                    StringRequest strRequest = new StringRequest
                            (com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // success
                                    System.err.println(response);
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    System.err.println(error.toString());
                                    String body;
                                    try {
                                        try {
                                            body = new String(error.networkResponse.data, "UTF-8");
                                            System.err.println(body);
                                        } catch(NullPointerException e) {
                                            Log.w("Warn","Null Pointer Exception");
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        // exception
                                        body = "";
                                    }
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams()
                        {
                           // must change otherwise raise Exception
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                            Map<String, String> params = new HashMap<String, String>();
                            Log.w("EDUKG_DEBUG", "1630043199261");
                            Log.w("EDUKG_DEBUG", ((Integer)((int)(AppSingleton.endTime - AppSingleton.startTime))).toString());
                            Log.w("EDUKG_DEBUG",df.format(new Date()));
                            params.put("id", "1630043199261");
                            params.put("time", ((Integer)((int)(AppSingleton.endTime - AppSingleton.startTime))).toString());
                            params.put("date", df.format(new Date()));
                            return params;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(getApplication());
                    queue.add(strRequest);

                    /**请求成功后解绑 */
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        };
        registerReceiver(receiver, filter);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
