package com.whx.router.plugin.model

data class RouteModel(
    val path: String,
    val remark: String?,
    val moduleName: String,
    val type: String,
    val pathClass: String,
    val tag: Int?,
    val deprecated: Boolean?,
    val ignoreHelper: Boolean?,
    val paramsType: List<ParamModel>?
)