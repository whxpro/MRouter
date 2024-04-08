package com.whx.router.plugin.utils

import com.whx.router.plugin.utils.Constants.PROJECT

object Logger {

    fun i(tag: String, info: String) {
        println("[info] ${PROJECT}::Gradle-Plugin >>> TAG:$tag $info")
    }

    fun w(tag: String, info: String) {
        println("[warning] ${PROJECT}::Gradle-Plugin >>> TAG:$tag $info")
    }

    fun e(tag: String, info: String) {
        error("[error] ${PROJECT}::Gradle-Plugin >>> TAG:$tag $info")
    }

}