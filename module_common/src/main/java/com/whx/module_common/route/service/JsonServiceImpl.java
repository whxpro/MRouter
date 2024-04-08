package com.whx.module_common.route.service;

import com.google.gson.Gson;
import com.whx.router.interfaces.IJsonService;
import com.whx.router.annotation.Service;
import com.whx.router.interfaces.IJsonService;

import java.lang.reflect.Type;

@Service(remark = "json服务")
public class JsonServiceImpl implements IJsonService {

    @Override
    public void init() {

    }

    @Override
    public String toJson(Object instance) {
        return new Gson().toJson(instance);
    }

    @Override
    public <T> T parseObject(String input, Type typeOfT) {
        return new Gson().fromJson(input, typeOfT);
    }
}