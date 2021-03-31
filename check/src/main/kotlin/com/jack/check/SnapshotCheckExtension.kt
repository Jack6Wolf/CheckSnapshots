package com.jack.check


/**
 * @author jack
 * @since 2021/3/31 11:07
 */

const val NAME = "snapshotCheck"
const val UNSPECIFIED = "unspecified"
const val DEPENDENCY_FILE_NAME: String = "dependency.txt"
const val SEPARATOR = ":"
const val VERSION_SEPARATOR = "-"
const val SNAPSHOT = "SNAPSHOT"

open class SnapshotCheckExtension {
    var abortBuild = true // 是否中断打包
    var checkBuildTypes: List<String> = ArrayList() // 需要做snapshot检查的build types
    var needCheck = false //是否需要做snapshot检查
    var dump = true // 是否dump到build文件夹
}