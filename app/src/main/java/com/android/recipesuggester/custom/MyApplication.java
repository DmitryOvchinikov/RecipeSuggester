package com.android.recipesuggester.custom;


import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyToast.initHelper(this);
    }
}
