package com.whx.router.utils;

import androidx.annotation.Nullable;

import com.whx.router.MRouter;
import com.whx.router.model.ServiceMeta;

import java.util.HashMap;

public class ServiceHashMap extends HashMap<String, ServiceMeta> {

    @Nullable
    @Override
    public ServiceMeta put(String key, ServiceMeta value) {
        // [xx]服务已经在[xxx]实现中注册，并将被覆盖(更新)。
        if (containsKey(key)) {
            MRouter.logger.warning(null, "The [" + key + "] service has been registered in the [" + value.getServiceClass().getSimpleName() + "] implementation and will be overridden (updated).");
        }
        return super.put(key, value);
    }

}
