package com.jack.check

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

/**
 * @author jack
 * @since 2021/3/31 11:31
 */

fun getCompiledDependencies(
    project: Project,
    variant: ApplicationVariant
): Collection<MavenDependency> {
    val configuration: Configuration? = try {
        // 3.x
        project.configurations.findByName("${variant.name}CompileClasspath")
    } catch (e: Exception) {
        // 2.x
        project.configurations.findByName("${variant.name}Compile")
    }
    val hashSet = HashSet<MavenDependency>()
    collectDependenciesFromConfiguration(configuration, hashSet)
    return hashSet
}

fun collectDependenciesFromConfiguration(
    configuration: Configuration?,
    hashSet: HashSet<MavenDependency>
) {
    try {
        configuration?.resolvedConfiguration?.lenientConfiguration?.allModuleDependencies?.forEach {
            val identifier = it.module.id
            if (isMavenDependency(identifier.name, identifier.group, identifier.version)) {
                hashSet.add(MavenDependency(identifier.group, identifier.name, identifier.version))
            }
        }
    } catch (e: Exception) {
        println(e)
    }
}

fun isMavenDependency(name: String, group: String?, version: String?): Boolean {
    if (group == null || version == null)
        return false
    if (UNSPECIFIED.equals(name, ignoreCase = true)) {
        return false
    }
    if (UNSPECIFIED.equals(version, ignoreCase = true)) {
        return false
    }
    return true
}


