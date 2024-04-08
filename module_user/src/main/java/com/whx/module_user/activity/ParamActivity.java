package com.whx.module_user.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.whx.module_common.model.TestModel;
import com.whx.module_common.utils.ToastUtils;
import com.whx.router.exception.ParamException;
import com.whx.module_common.model.TestModel;
import com.whx.module_common.utils.ToastUtils;
import com.whx.module_user.databinding.UserActivityParamBinding;
import com.whx.router.annotation.Param;
import com.whx.router.annotation.Route;
import com.whx.router.exception.ParamException;

@Route(path = "/new/param/activity", remark = "参数页面")
public class ParamActivity extends BaseParamActivity {

    UserActivityParamBinding vb;

    @Param
    int age = 18;

    @Param(name = "nickname", remark = "昵称", required = true)
    String name;

    @Param(name = "test", remark = "自定义类型", required = true)
    TestModel testModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = UserActivityParamBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        try {
            ParamActivity$$Param.injectCheck(this);
        } catch (ParamException e) {
            ToastUtils.makeText(this, e.getMessage());
            finish();
            return;
        }

        vb.tvTitle.setText("base:" + base + ",age:" + age + ",name:" + name + "\ntest:" + testModel.toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ParamActivity$$Param.inject(this, intent);
        vb.tvTitle.setText("base:" + base + ",age:" + age + ",name:" + name);
    }
}