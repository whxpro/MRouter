package com.whx.module_main.activity;

import androidx.fragment.app.FragmentActivity;

import com.whx.router.callback.impl.GoCallbackImpl;
import com.whx.router.model.Card;
import com.whx.router.annotation.Route;
import com.whx.router.callback.impl.GoCallbackImpl;
import com.whx.router.helper.module_main.group_main.MainActivityGoRouter;
import com.whx.router.model.Card;

@Route(path = "/main/splash/activity", remark = "欢迎页")
public class SplashActivity extends FragmentActivity {

    @Override
    protected void onStart() {
        super.onStart();
        goMainActivity();
    }

    private void goMainActivity() {
        MainActivityGoRouter.build().go(this, new GoCallbackImpl() {
            @Override
            public void onArrival(Card card) {
                finish();
            }
        });
    }

}
