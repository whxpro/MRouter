package com.whx.router.plugin.launch

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.whx.router.plugin.core.application.AssembleApplicationModuleTask
import com.whx.router.plugin.core.route.AssembleRouteModuleTask
import com.whx.router.plugin.core.doc.GenerateRouteDocTask
import com.whx.router.plugin.helper.GenerateGoRouterHelperTask
import com.whx.router.plugin.utils.Constants
import com.whx.router.plugin.utils.Constants.ASSEMBLE_APPLICATION_MODULE
import com.whx.router.plugin.utils.Constants.ASSEMBLE_ROUTE_MODULE
import com.whx.router.plugin.utils.Constants.GENERATE_ROUTE_DOC
import com.whx.router.plugin.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

class MainGradlePluginLaunch : Plugin<Project> {

    private val TAG = "MainLaunch"

    private val variantList: ArrayList<Variant> = ArrayList()

    override fun apply(project: Project) {
        val isApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (!isApp) {
            Logger.e(TAG, "Plugin ['com.whx.mrouter'] can only be used under the application, not under the module library invalid!")
            return
        }
        variantList.clear()
        project.extensions.add("MRouter", com.whx.router.plugin.config.GoRouterConfig::class.java)
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            variantList.add(variant)
            val goRouterConfig = project.extensions.getByType(com.whx.router.plugin.config.GoRouterConfig::class.java)
            val variantName = variant.name
            val variantNameCapitalized = variantName.capitalized()

            project.tasks.register("${GENERATE_ROUTE_DOC}$variantNameCapitalized", GenerateRouteDocTask::class.java){
                it.variant = variant
            }.dependsOn("assemble${variantNameCapitalized}")

            if (goRouterConfig.helperToRootModuleName.isNotEmpty()){
                project.tasks.register("${Constants.GENERATE_GOROUTER_HELPER}$variantNameCapitalized", GenerateGoRouterHelperTask::class.java) {
                    it.variant = variant
                    it.rootModuleName = goRouterConfig.helperToRootModuleName
                }.dependsOn("assemble${variantNameCapitalized}")
            }

            // 处理允许执行自动注册任务的集合,未设置表示全部任务都可执行
            var isRunTask = false
            if (goRouterConfig.runAutoRegisterBuildTypes.isNotEmpty()) {
                for (buildType in goRouterConfig.runAutoRegisterBuildTypes) {
                    if (buildType.equals(variant.buildType, true)) {
                        isRunTask = true
                        break
                    }
                }
            } else {
                isRunTask = true
            }
            if (isRunTask) {
                val rmTask = project.tasks.register("${ASSEMBLE_ROUTE_MODULE}$variantNameCapitalized", AssembleRouteModuleTask::class.java)
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.ALL)
                    .use(rmTask)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        AssembleRouteModuleTask::allJars,
                        AssembleRouteModuleTask::allDirectories,
                        AssembleRouteModuleTask::output
                    )

                val amTask = project.tasks.register("${ASSEMBLE_APPLICATION_MODULE}$variantNameCapitalized", AssembleApplicationModuleTask::class.java)
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.ALL)
                    .use(amTask)
                    .toTransform(
                        ScopedArtifact.CLASSES,
                        AssembleApplicationModuleTask::allJars,
                        AssembleApplicationModuleTask::allDirectories,
                        AssembleApplicationModuleTask::output
                    )
            }
        }
        project.afterEvaluate {
            val goRouterConfig = it.extensions.getByType(com.whx.router.plugin.config.GoRouterConfig::class.java)
            if (goRouterConfig.helperToRootModuleName.isNotEmpty()){
                for (variant in variantList) {
                    val variantName = variant.name
                    val variantNameCapitalized = variantName.capitalized()
                    project.tasks.findByName("assemble${variantNameCapitalized}")?.finalizedBy("${Constants.GENERATE_GOROUTER_HELPER}$variantNameCapitalized")
                }
            }
        }
    }
}
