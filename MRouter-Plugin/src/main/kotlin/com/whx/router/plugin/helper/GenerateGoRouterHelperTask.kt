package com.whx.router.plugin.helper

import com.android.build.api.variant.Variant
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.whx.router.plugin.model.RouteHelperModel
import com.whx.router.plugin.utils.Constants
import com.whx.router.plugin.utils.Logger
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.UnknownConfigurationException
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateGoRouterHelperTask : DefaultTask() {

    init {
        group = Constants.PROJECT_OTHER
    }

    @get:Input
    abstract var variant: Variant
    @get:Input
    abstract var rootModuleName: String

    private val TAG = "RH"

    private var dependModeList: ArrayList<String> = ArrayList()
    private var routeHelperModel: RouteHelperModel? = null

    private val catalog: String = "main" // main or variantName or buildType

    @TaskAction
    fun taskAction() {
        Logger.i(TAG, "GoRouterHelper task start.")
        val variantName = variant.name
        val buildType = variant.buildType
        val flavorName = variant.flavorName
        setDependModeList(variantName, buildType, flavorName)

        if (!scanRouteModule(variantName, buildType)) {
            Logger.i(TAG, "GoRouterHelper task end.")
            return
        }

        if (checkCache()) {
            Logger.i(TAG, "GoRouterHelper(UP-TO-DATE) task end.")
            return
        }

        val rootProject = try {
            project.project(":${rootModuleName}")
        } catch (e: Exception) {
            Logger.e(TAG, "Please check if the project name[${rootModuleName}] specified by the attribute 'helperToRootModuleName' is correct and exists")
            return
        }

        val dir = rootProject!!.projectDir
        val path = "/src/${catalog}/java/com/whx/router/helper"
        val packageFile = File(dir, path)
        AssembleGoRouteHelperCode(routeHelperModel!!).generateJavaFile(packageFile)
        saveCache()
        Logger.i(TAG, "GoRouterHelper task end. ${dir}${path}")
    }

    private fun checkCache(): Boolean {
        val cacheFile = File(project.buildDir, "/tmp/mrouter/route-helper-cache.json")
        if (!cacheFile.exists())
            return false
        try {
            return JsonParser.parseString(cacheFile.readText()).equals(JsonParser.parseString(Gson().toJson(routeHelperModel)))
        } catch (_: Exception) {
        }
        return false
    }

    private fun saveCache() {
        val cacheFile = File(project.buildDir, "/tmp/mrouter/route-helper-cache.json")
        cacheFile.parentFile.mkdirs()
        cacheFile.writeText(Gson().toJson(routeHelperModel), Charsets.UTF_8)
    }

    private fun scanRouteModule(variantName: String, buildType: String?): Boolean {
        project.dependProject().plus(project).forEach { curProject ->
            var file = searchJSONFile(curProject, variantName)
            if (file == null && buildType != null) {
                 file = searchJSONFile(curProject, buildType)
            }
            if (file != null) {
                mergeRouteModule(curProject, file)
            }
        }
        if (routeHelperModel == null) {
            Logger.e(TAG, "Failed to generate the route!")
            return false
        }
        return true
    }

    private fun searchJSONFile(curProject: Project, name: String): File? {
        var genFile = curProject.file("${curProject.buildDir}/generated/ap_generated_sources/${name}").listFiles()
        var collection = curProject.files(genFile).asFileTree.filter { it.name.endsWith(Constants.DOCUMENT_FILE_NAME) }
        if (collection.isEmpty) {
            genFile = curProject.file("${curProject.buildDir}/generated/source/kapt/${name}").listFiles()
            collection = curProject.files(genFile).asFileTree.filter { it.name.endsWith(Constants.DOCUMENT_FILE_NAME) }
        }
        if (collection.isEmpty) {
            Logger.w(TAG, "project[${curProject.name}] scan 0 route.")
            return null
        } else {
            val file = collection.first()
            Logger.i(TAG, "project[${curProject.name}] found the file[${file.name}].")
//            catalog = name
            return file
        }
    }

    private fun mergeRouteModule(curProject: Project, file: File) {
        if (file.readLines().isNotEmpty()) {
            try {
                val model = Gson().fromJson(file.readText(), RouteHelperModel::class.java)
                if (routeHelperModel == null) {
                    routeHelperModel = model
                } else {
                    routeHelperModel!!.services.putAll(model.services)
                    routeHelperModel!!.routes.putAll(model.routes)
                }
            } catch (e: Exception) {
                Logger.e(TAG, "Module[${curProject.name}] route json parsing failed, do not modify the generated route json, use the '${Constants.GENERATE_ROUTE_DOC}' task to generate a new route json.")
            }
        } else {
            Logger.e(TAG, "Module[${curProject.name}] route json content is empty and a new route json is generated using the '${Constants.GENERATE_ROUTE_DOC}' task.")
        }
    }

    private fun setDependModeList(variantName: String, buildType: String?, flavorName: String?) {
        dependModeList.clear()
        dependModeList.add("api")
        dependModeList.add("implementation")
        dependModeList.add("${variantName}Api")
        dependModeList.add("${variantName}Implementation")
        if (variantName != buildType) {
            if (buildType != null) {
                dependModeList.add("${buildType}Api")
                dependModeList.add("${buildType}Implementation")
            }
            if (flavorName != null) {
                dependModeList.add("${flavorName}Api")
                dependModeList.add("${flavorName}Implementation")
            }
        }
    }

    /**
     * 查询项目下的依赖项目,递归子项目
     */
    private fun Project.dependProject(): List<Project> {
        val projects = ArrayList<Project>()
        dependModeList.forEach { name ->
            try {
                val dependencyProjects = configurations.getByName(name).dependencies
                    .filterIsInstance<DefaultProjectDependency>()
                    .filter { it.dependencyProject.isAndroid() }
                    .map { it.dependencyProject }
                projects.addAll(dependencyProjects)
                dependencyProjects.forEach { projects.addAll(it.dependProject()) }
            } catch (_: UnknownConfigurationException) {
            }
        }
        return projects.distinct()
    }

    private fun Project.isAndroid() = plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")

}