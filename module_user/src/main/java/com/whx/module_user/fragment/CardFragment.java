package com.whx.module_user.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.whx.module_user.databinding.UserFragmentCardBinding;
import com.whx.router.annotation.Route;

@Route(path = "/user/card/fragment", remark = "卡片片段")
public class CardFragment extends Fragment {

    UserFragmentCardBinding vb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vb = UserFragmentCardBinding.inflate(inflater, container, false);
        return vb.getRoot();
    }
}
