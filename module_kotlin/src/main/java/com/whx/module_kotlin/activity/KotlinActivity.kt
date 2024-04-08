package com.whx.module_kotlin.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.whx.module_kotlin.databinding.KotlinActivityKotlinBinding
import com.whx.router.annotation.Param
import com.whx.router.annotation.Route

@com.whx.router.annotation.Route(path = "/kotlin/activity", remark = "这是一个kotlin页面，本库支持kapt")
class KotlinActivity : FragmentActivity() {

    @JvmField
    @com.whx.router.annotation.Param
    var age: Int = 18

    @JvmField
    @com.whx.router.annotation.Param(name = "nickname", remark = "昵称", required = true)
    var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vb = KotlinActivityKotlinBinding.inflate(layoutInflater)
        setContentView(vb.root)

        `KotlinActivity$$Param`.inject(this)
        vb.tvTitle.text = "age:${age},name:${name}"
    }
}