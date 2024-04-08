package com.whx.module_user.route.service;

import com.whx.module_common.route.service.user.PayService;
import com.whx.router.annotation.Service;
import com.whx.module_common.route.service.user.PayService;
import com.whx.router.annotation.Service;

@Service(alias = "Alipay", remark = "AliPay服务")
public class AliPayServiceImpl implements PayService {
    @Override
    public void init() {

    }

    @Override
    public String getPayType() {
        return "AliPay";
    }
}
