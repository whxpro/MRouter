package com.whx.module_user.route.interceptor;

import static com.whx.module_common.route.RouteTag.AUTHENTICATION;

import com.whx.module_common.route.RouteTag;
import com.whx.router.callback.InterceptorCallback;
import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.model.Card;
import com.whx.router.annotation.Interceptor;
import com.whx.router.callback.InterceptorCallback;
import com.whx.router.exception.RouterException;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.model.Card;

@Interceptor(ordinal = 100, remark = "身份验证拦截器")
public class AuthenticationInterceptor implements IInterceptor {

    @Override
    public void init() {

    }

    @Override
    public void process(Card card, InterceptorCallback callback) {
        if (card.isTagExist(RouteTag.AUTHENTICATION)) {
            if (false) {// 判断用户是否身份验证了
                callback.onInterrupt(card, new RouterException("未身份认证,拦截!"));
                return;
            }
        }
        callback.onContinue(card);
    }
}