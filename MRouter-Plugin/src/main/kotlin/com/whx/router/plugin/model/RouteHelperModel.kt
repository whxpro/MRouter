package com.whx.router.plugin.model

data class RouteHelperModel(
    val services: HashMap<String, ServiceModel> = HashMap(),
    val routes: HashMap<String, List<RouteModel>> = HashMap()
)