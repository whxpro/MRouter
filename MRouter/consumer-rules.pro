
# #   ########## MRouter # start ##########
# 自动注册模块路由加载类不混淆
-keep class * implements com.whx.router.module.interfaces.IRouteModule
# 自动注册模块Application代理类不混淆
-keep class * implements com.whx.router.interfaces.IApplicationModule
# 如果开启了代码优化,需要加入这两句,已保证实现了接口的类正常
-keep class * implements com.whx.router.interfaces.IInterceptor
-keep class * implements com.whx.router.interfaces.IService
# 在R8 3.0及更高版本中保留TypeToken及其子类的泛型签名
-keep,allowobfuscation,allowshrinking class com.whx.router.utils.TypeWrapper
-keep,allowobfuscation,allowshrinking class * extends com.whx.router.utils.TypeWrapper
# #   ########## MRouter # end ##########


# #   ########## delete MRouter logger # start ##########
# 1.使用混淆优化文件 proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
# 2.不开启 -dontoptimize
# 3.满足以上两点下面的配置才会生效
-assumenosideeffects class com.whx.router.logger.DefaultLogger {
    public *** debug(...);
    public *** info(...);
    public *** warning(...);
    public *** error(...);
}
# #   ########## MRouter # end ##########