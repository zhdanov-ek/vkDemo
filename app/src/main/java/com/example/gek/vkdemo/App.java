package com.example.gek.vkdemo;

import android.app.Application;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by gek on 10.03.17.
 */

public class App extends Application {
    public static final String TAG = "VK_DEMO";

    // Класс, который будет следить за тем, что бы токен был валидным
    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // VKAccessToken is invalid
                Log.d(TAG, "onVKAccessTokenChanged: token is invalid");
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // собственно запуск наблюдения за токеном
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}