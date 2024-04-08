package com.whx.module_user.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.whx.module_user.databinding.UserActivitySignInBinding;
import com.whx.router.annotation.Route;

@Route(path = "/user/sign_in/activity", remark = "登录页面")
public class SignInActivity extends FragmentActivity {

    UserActivitySignInBinding vb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = UserActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());
    }
}