package com.whx.module_user.route.interceptor;

import static com.whx.module_common.route.RouteTag.LOGIN;

import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;

import com.whx.module_common.route.RouteTag;
import com.whx.router.callback.InterceptorCallback;
import com.whx.router.exception.RouterException;
import com.whx.router.helper.module_user.group_user.UserSignInActivityGoRouter;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.annotation.Interceptor;
import com.whx.router.callback.InterceptorCallback;
import com.whx.router.exception.RouterException;
import com.whx.router.helper.module_user.group_user.UserSignInActivityGoRouter;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.model.Card;

@Interceptor(ordinal = 1, remark = "登录拦截器")
public class SignInInterceptor implements IInterceptor {

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void init() {
    }

    @Override
    public void process(Card card, InterceptorCallback callback) {
        if (card.isTagExist(RouteTag.LOGIN)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(card.getContext())
                            .setMessage("登录拦截器,用户是否登录?")
                            .setNegativeButton("没登录", (dialog, which) -> {
                                callback.onInterrupt(card, new RouterException("未登录,拦截自动跳转登录页!"));
                                UserSignInActivityGoRouter.go();
                            })
                            .setPositiveButton("已登录", (dialog, which) -> {
                                callback.onContinue(card);
                            })
                            .create().show();
                }
            });
            return;
        }
        callback.onContinue(card);
    }
}