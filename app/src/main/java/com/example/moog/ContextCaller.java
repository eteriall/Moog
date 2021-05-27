package com.example.moog;

import android.app.Application;
import android.content.Context;

public class ContextCaller extends Application {
    private static ContextCaller instance;

    public static ContextCaller getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}