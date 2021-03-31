package com.jack.check

/**
 * @author jack
 * @since 2021/3/31 11:24
 */
class MavenDependency(val group: String?, val name: String, val version: String?) {
    fun isSnapshotDependency(): Boolean {
        val versionSplit = version?.split(VERSION_SEPARATOR.toRegex())?.toTypedArray()
        if (versionSplit != null)
            for (v in versionSplit) {
                if (SNAPSHOT.equals(v, ignoreCase = true)) {
                    return true
                }
            }
        return false
    }

    fun toGradleDependency(): String {
        val builder = StringBuilder()
        builder.append(group).append(SEPARATOR)
            .append(name).append(SEPARATOR)
            .append(version)
        return builder.toString()
    }

}