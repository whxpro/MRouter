package com.whx.module_user.activity;

import static com.whx.module_common.route.RouteTag.AUTHENTICATION;
import static com.whx.module_common.route.RouteTag.LOGIN;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.whx.module_common.route.RouteTag;
import com.whx.module_user.route.interceptor.SignInInterceptor;
import com.whx.module_user.databinding.UserActivityUserInfoBinding;
import com.whx.router.annotation.Route;

/**
 * 这个页面需要登录后才能进入,未登录会触发登录拦截器{@link SignInInterceptor}
 */
@Route(path = "/user/info/activity", remark = "用户信息页面", tag = RouteTag.LOGIN | RouteTag.AUTHENTICATION)
public class UserInfoActivity extends FragmentActivity {

    UserActivityUserInfoBinding vb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = UserActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());
    }
}