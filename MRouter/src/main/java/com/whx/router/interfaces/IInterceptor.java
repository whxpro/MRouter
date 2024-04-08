package com.whx.router.interfaces;

import com.whx.router.callback.InterceptorCallback;
import com.whx.router.model.Card;
import com.whx.router.callback.InterceptorCallback;

/**
 * 拦截器
 */
public interface IInterceptor {

    void init();

    /**
     * {@link InterceptorCallback#onContinue(Card)} 继续执行,
     * {@link InterceptorCallback#onInterrupt(Card, Throwable)}} 拦截
     *
     * @param card
     * @param callback
     */
    void process(Card card, InterceptorCallback callback);
}