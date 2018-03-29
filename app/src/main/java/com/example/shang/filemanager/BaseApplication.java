package com.example.shang.filemanager;

import android.app.Application;
import android.content.Context;

/**
 * Created by Shang on 2017/3/30.
 */
public class BaseApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
