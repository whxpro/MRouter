package com.whx.router.utils;

import androidx.annotation.Nullable;

import com.whx.router.MRouter;
import com.whx.router.model.CardMeta;

import java.util.HashMap;
import java.util.Map;

public class RouteHashMap extends HashMap<String, CardMeta> {

    @Nullable
    @Override
    public CardMeta put(String key, CardMeta value) {
        // 检查路由是否有重复提交的情况(仅对使用java注册方式有效)
        if (MRouter.isDebug()) {
            if (containsKey(key)) {
                MRouter.logger.error(null, "route path[" + key + "] duplicate commit!!!");
            } else if (containsValue(value)) {
                MRouter.logger.error(null, "route pathClass[" + value.getPathClass() + "] duplicate commit!!!");
            }
        }
        return super.put(key, value);
    }

    public boolean containsValue(CardMeta value) {
        if (size() == 0)
            return false;
        for (Map.Entry<String, CardMeta> entry : entrySet()) {
            if (entry.getValue().getPathClass() == value.getPathClass()) {
                return true;
            }
        }
        return false;
    }
}
