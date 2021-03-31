package com.jack.check

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * @author jack
 * @since 2021/3/31 11:26
 */
fun dump(dependencies: Collection<MavenDependency>?, file: File) {
    if (dependencies?.isNotEmpty() == true) {
        try {
            var bufferedWriter = BufferedWriter(FileWriter(file))
            var count = 0
            dependencies.forEach {
                bufferedWriter.write(it.toGradleDependency())
                if (count < dependencies.size - 1) {
                    bufferedWriter.newLine()
                }
                count++;
            }
            bufferedWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
