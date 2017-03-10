package com.example.gek.vkdemo;

import android.app.Application;

import com.vk.sdk.VKSdk;

/**
 * Created by gek on 10.03.17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(this);
    }
}
