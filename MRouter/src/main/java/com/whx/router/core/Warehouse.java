package com.whx.router.core;

import androidx.lifecycle.MutableLiveData;

import com.whx.router.interfaces.IApplicationModule;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.model.CardMeta;
import com.whx.router.model.ServiceMeta;
import com.whx.router.module.interfaces.IRouteModuleGroup;
import com.whx.router.utils.InterceptorTreeMap;
import com.whx.router.utils.RouteGroupHashMap;
import com.whx.router.utils.RouteHashMap;
import com.whx.router.utils.ServiceHashMap;
import com.whx.router.interfaces.IApplicationModule;
import com.whx.router.interfaces.IInterceptor;
import com.whx.router.model.CardMeta;
import com.whx.router.model.ServiceMeta;
import com.whx.router.module.interfaces.IRouteModuleGroup;
import com.whx.router.utils.InterceptorTreeMap;
import com.whx.router.utils.RouteGroupHashMap;
import com.whx.router.utils.RouteHashMap;
import com.whx.router.utils.ServiceHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Warehouse {

    static final List<IApplicationModule> applicationModules = new ArrayList<>();

    static final Map<String, IRouteModuleGroup> routeGroups = new RouteGroupHashMap();

    static final Map<String, CardMeta> routes = new RouteHashMap();

    static final Map<String, ServiceMeta> services = new ServiceHashMap();

    static final Map<Integer, IInterceptor> interceptors = new InterceptorTreeMap<>("More than one interceptors use same ordinal [%s]");

    static final Map<String, MutableLiveData> events = new HashMap<>();

}
