package com.jack.check

import com.android.build.gradle.AppExtension
import org.antlr.v4.misc.Utils
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * @author jack
 * @since 2021/3/31 10:58
 */
class SnapshotCheckPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.afterEvaluate { p -> afterEvaluate(p) }
        project.extensions.create(
            NAME, SnapshotCheckExtension::class.java
        )
    }

    private fun afterEvaluate(project: Project) {
        val android: AppExtension? = project.extensions.findByName("android") as AppExtension
        android?.applicationVariants?.forEach { variant ->
            val config: SnapshotCheckExtension? =
                project.extensions.findByName(NAME) as SnapshotCheckExtension
            if (config != null) {
                val buildType = variant.buildType
                var shouldCheck = false
                if (config.checkBuildTypes.isNotEmpty()) {
                    for (checkBuildType in config.checkBuildTypes) {
                        if (buildType.name.equals(checkBuildType, ignoreCase = true)) {
                            shouldCheck = true
                            break
                        }
                    }
                }
                if (config.needCheck)
                    shouldCheck = true
                val dependencies: Collection<MavenDependency> =
                    getCompiledDependencies(project, variant)
                val snapshots = HashSet<MavenDependency>()
                dependencies.forEach {
                    if (it.isSnapshotDependency())
                        snapshots.add(it)
                }
                if (config.dump) {
                    val file = File(project.buildDir, DEPENDENCY_FILE_NAME)
                    if (!project.buildDir.exists()) {
                        project.buildDir.mkdirs()
                    }
                    dump(dependencies, file)
                }
                if (shouldCheck && config.abortBuild && snapshots.size > 0) {
                    val capitalize = Utils.capitalize(variant.name)
                    project.tasks.findByName(String.format("merge%sAssets", capitalize))
                        ?.doFirst {
                            val stringBuilder = StringBuilder("应用包含 Snapshot Libraries:\n")
                            snapshots.forEach {
                                stringBuilder.append(it.toGradleDependency() + "\n")
                            }
                            throw RuntimeException(stringBuilder.toString())
                        }
                }
            } else {
                project.logger.warn("未配置 SnapshotCheck！")
            }
        }
    }
}