package com.appsnipp.education;

/***
 * This is the main app entrance which is the real application file
 * @author Shuning Zhang
 * @version 1.0
 */

import android.app.Application;
import android.util.Log;


public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.w("Warn","Entering App");
    }
}
