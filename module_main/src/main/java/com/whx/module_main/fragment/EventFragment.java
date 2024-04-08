package com.whx.module_main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.whx.module_common.entity.UserEntity;
import com.whx.module_common.entity.UserEntity;
import com.whx.module_main.databinding.MainFragmentEventBinding;
import com.whx.router.MRouter;
import com.whx.router.annotation.Route;
import com.whx.router.helper.module_main.group_main.MainEventActivityGoRouter;

@Route(path = "/main/event/fragment", remark = "事件片段")
public class EventFragment extends Fragment {

    MainFragmentEventBinding vb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vb = MainFragmentEventBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 订阅一下事件
        // 订阅String类型事件(页面处于活跃状态下才会收到)
        MRouter.getInstance().registerEvent(this, String.class, data -> {
            vb.tvShow.setText("EventFragment->String data:" + data);
        });
        // 订阅String类型事件(页面无乱处于何种状态下都会收到)
        MRouter.getInstance().registerEventForever(this, String.class, data -> {
            // 做一些处理...
        });
        // 订阅自定义类型事件
        MRouter.getInstance().registerEvent(this, UserEntity.class, data -> {
            vb.tvShow.setText("EventFragment->UserEntity data:" + data.toString());
        });

        vb.tvSendString.setOnClickListener(v -> {
            // 向EventActivity发送String类型
            MainEventActivityGoRouter.postEvent("Hi");
        });

        vb.tvSendCustom.setOnClickListener(v -> {
            // 向EventActivity发送自定义类型
            MainEventActivityGoRouter.postEvent(new UserEntity(123, "jack"));
        });
    }
}
