package com.whx.module_main;

import android.app.Application;
import android.util.Log;

import com.whx.router.annotation.ApplicationModule;
import com.whx.router.interfaces.IApplicationModule;
import com.whx.router.annotation.ApplicationModule;
import com.whx.router.interfaces.IApplicationModule;

@ApplicationModule
public class MainApplication implements IApplicationModule {

    @Override
    public void onCreate(Application app) {
        Log.d("MainApplication", "onCreate()");
    }

    @Override
    public void onLoadAsync(Application app) {
        Log.d("MainApplication", "onLoadAsync()");
    }

}
