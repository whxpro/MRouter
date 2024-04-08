package com.whx.mrouter;

import android.app.Application;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import com.whx.router.MRouter;
import com.whx.module_common.BuildConfig;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            MRouter.openDebug();
        }
        // 这部分是多模块application,如不使用多模块application可以不调用
        MRouter.callAMOnCreate(this);
        // 自动加载所有路由模块
        MRouter.autoLoadRouteModule(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 这部分是多模块application,如不使用多模块application可以不调用
        MRouter.callAMOnTerminate();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 这部分是多模块application,如不使用多模块application可以不调用
        MRouter.callAMOnConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 这部分是多模块application,如不使用多模块application可以不调用
        MRouter.callAMOnLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // 这部分是多模块application,如不使用多模块application可以不调用
        MRouter.callAMOnTrimMemory(level);
    }
}
