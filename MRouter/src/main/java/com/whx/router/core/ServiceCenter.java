package com.whx.router.core;

import androidx.annotation.Nullable;

import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IService;
import com.whx.router.model.ServiceMeta;
import com.whx.router.utils.TextUtils;
import com.whx.router.MRouter;
import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IService;
import com.whx.router.model.ServiceMeta;
import com.whx.router.utils.TextUtils;

public class ServiceCenter {

    /**
     * 实现相同接口的service会被覆盖(更新)
     *
     * @param serviceClass 实现类.class
     */
    public static void addService(Class<? extends IService> serviceClass) {
        addService(serviceClass, null);
    }

    /**
     * 添加服务
     *
     * @param serviceClass
     * @param alias        别名
     */
    public static void addService(Class<? extends IService> serviceClass, @Nullable String alias) {
        Class<? extends IService> serviceInterfaceClass = (Class<? extends IService>) serviceClass.getInterfaces()[0];
        String key = serviceInterfaceClass.getCanonicalName();
        if (!TextUtils.isEmpty(alias)) {
            key += "$" + alias;
        }
        Warehouse.services.put(key, new ServiceMeta(serviceClass));
        MRouter.logger.debug(null, "[addService] size:" + Warehouse.services.size() + ", " + key + " -> " + serviceClass.getSimpleName());
    }

    /**
     * 获取service接口的实现
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getService(Class<? extends T> serviceClass) {
        return getService(serviceClass, null);
    }

    /**
     * 获取service接口的实现
     *
     * @param serviceClass 接口.class
     * @param alias        别名
     * @param <T>
     * @return
     */
    @Nullable
    public static <T> T getService(Class<? extends T> serviceClass, @Nullable String alias) {
        String key = serviceClass.getCanonicalName();
        if (!TextUtils.isEmpty(alias)) {
            key += "$" + alias;
        }
        ServiceMeta meta = Warehouse.services.get(key);
        if (meta != null) {
            if (serviceClass.isAssignableFrom(meta.getServiceClass())) {
                IService instance = meta.getService();
                if (instance == null) {
                    try {
                        instance = meta.getServiceClass().getConstructor().newInstance();
                        instance.init();
                        meta.setService(instance);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RouterException("serviceClass constructor new instance failed!");
                    }
                }
                MRouter.logger.info(null, "[getService] " + serviceClass.getSimpleName() + " -> " + meta.getServiceClass().getSimpleName());
                return (T) instance;
            }
        }
        MRouter.logger.warning(null, "[getService] " + serviceClass.getSimpleName() + ", No registered service found!");
        return null;
    }

}
