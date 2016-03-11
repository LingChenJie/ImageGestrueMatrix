package com.matrix_yue.gesturesview;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Wxcily on 15/10/30.
 */
public class MyApplication extends Application {

    //Application单例
    private static MyApplication instance;
    //Context
    private Context context;
    //屏幕尺寸
    private int screenWidth;
    private int screenHeight;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this.getApplicationContext();
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;//获取屏幕宽度
        screenHeight = dm.heightPixels;//获取屏幕高度
    }

    public Context getContext() {
        return context;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

}
