package com.whx.module_kotlin.route.service

import com.whx.module_common.route.service.user.UserService
import com.whx.router.annotation.Service

@com.whx.router.annotation.Service(remark = "Kotlin模块用户服务", alias = "Kotlin")
class KotlinUserServiceImpl : com.whx.module_common.route.service.user.UserService {
    override fun init() {
    }

    override fun getUserId(): Long {
        return 789
    }
}