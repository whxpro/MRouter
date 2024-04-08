package com.whx.module_user.route.service;

import com.whx.module_common.route.service.user.PayService;
import com.whx.router.annotation.Service;
import com.whx.module_common.route.service.user.PayService;
import com.whx.router.annotation.Service;

@Service(alias = "WechatPay", remark = "微信Pay服务")
public class WechatPayServiceImpl implements PayService {
    @Override
    public void init() {

    }

    @Override
    public String getPayType() {
        return "WechatPay";
    }
}
