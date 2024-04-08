package com.whx.router.utils;

import androidx.annotation.Nullable;

import com.whx.router.exception.RouterException;
import com.whx.router.module.interfaces.IRouteModuleGroup;
import com.whx.router.MRouter;
import com.whx.router.exception.RouterException;
import com.whx.router.module.interfaces.IRouteModuleGroup;

import java.util.HashMap;

public class RouteGroupHashMap extends HashMap<String, IRouteModuleGroup> {

    @Nullable
    @Override
    public IRouteModuleGroup put(String key, IRouteModuleGroup value) {
        // 发现已经存在的分组,直接执行分组里的加载方法,再添加新的路由数据进来
        if (containsKey(key)) {
            try {
                MRouter.logger.warning(null, "Discover an existing group[" + key + "], execute the loading method in the group, and add new route data.");
                get(key).load();
            } catch (Exception e) {
                throw new RouterException("A fatal exception occurred while loading the route group[" + key + "]. [" + e.getMessage() + "]");
            }
        }
        return super.put(key, value);
    }

}
