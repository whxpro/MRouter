package com.whx.module_user;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.whx.router.annotation.ApplicationModule;
import com.whx.router.interfaces.IApplicationModule;
import com.whx.router.annotation.ApplicationModule;
import com.whx.router.interfaces.IApplicationModule;

@ApplicationModule
public class UserApplication implements IApplicationModule {

    @Override
    public void onCreate(Application app) {
        Log.d("UserApplication", "onCreate()");
    }

    @Override
    public void onLoadAsync(Application app) {
        Log.d("UserApplication", "onLoadAsync()");
    }

}
