package com.whx.router.core;

import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.MRouter;
import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IInterceptor;

public class InterceptorCenter {

    /**
     * 重复添加相同序号会catch
     *
     * @param ordinal
     * @param interceptor
     * @param isForce
     */
    public static void addInterceptor(int ordinal, Class<? extends IInterceptor> interceptor, boolean isForce) {
        String title = isForce ? "[setInterceptor]" : "[addInterceptor]";
        try {
            if (isForce) {
                Warehouse.interceptors.remove(ordinal);
            }
            IInterceptor instance = interceptor.getConstructor().newInstance();
            instance.init();
            Warehouse.interceptors.put(ordinal, instance);
            MRouter.logger.debug(null, title + " size:" + Warehouse.interceptors.size() + ", ordinal:" + ordinal + " -> " + interceptor.getSimpleName());
        } catch (Exception e) {
            throw new RouterException(title + " " + e.getMessage());
        }
    }

    /**
     * 重复添加相同序号会覆盖(更新)
     *
     * @param ordinal
     * @param interceptor
     */
    public static void setInterceptor(int ordinal, Class<? extends IInterceptor> interceptor) {
        addInterceptor(ordinal, interceptor, true);
    }

    public static void clearInterceptors() {
        Warehouse.interceptors.clear();
    }

}
