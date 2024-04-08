package com.whx.router.core.interfaces;

import com.whx.router.callback.InterceptorCallback;
import com.whx.router.interfaces.IService;
import com.whx.router.model.Card;
import com.whx.router.callback.InterceptorCallback;
import com.whx.router.model.Card;
import com.whx.router.interfaces.IService;

public interface IInterceptorService extends IService {
    void doInterceptions(Card card, InterceptorCallback callback);
}