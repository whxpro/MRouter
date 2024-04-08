package com.whx.module_kotlin.route.interceptor

import com.whx.router.annotation.Interceptor
import com.whx.router.callback.InterceptorCallback
import com.whx.router.interfaces.IInterceptor
import com.whx.router.model.Card

@com.whx.router.annotation.Interceptor(ordinal = 50, remark = "Kotlin拦截器")
class KotlinInterceptor : com.whx.router.interfaces.IInterceptor {
    override fun init() {

    }

    override fun process(card: com.whx.router.model.Card?, callback: com.whx.router.callback.InterceptorCallback) {
        callback.onContinue(card)
    }
}