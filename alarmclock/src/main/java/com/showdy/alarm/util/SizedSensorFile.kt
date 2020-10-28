package com.showdy.alarm.util

import android.content.Context
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by <b>Showdy</b> on 2020/10/20 17:07
 *
 * 创建固定大小的文件
 */
class SizedSensorFile(private val context: Context, private val fileName: String) {

    private val MAX_SIZE: Int = 2 * 1024 * 1024 //文件最大内存 100kb
    private val fileIndex: AtomicInteger = AtomicInteger(0)

    fun getValue(): File {
        //创建外层文件夹
        val dirFile = File(context.applicationContext.filesDir, fileName)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
            dirFile.setWritable(true)
        }

        var existFile: File? = null
        var newFile = File(dirFile, String.format("%s_%s.csv", fileName, fileIndex.get()))
        while (newFile.exists()) {
            existFile = newFile
            newFile =
                File(dirFile, String.format("%s_%s.csv", fileName, fileIndex.incrementAndGet()))
        }

        existFile?.let {
            return if (it.length() >= MAX_SIZE) {
                newFile
            } else it
        }
        if (!newFile.exists()) {
            newFile.createNewFile()
            newFile.setWritable(true)
        }
        return newFile
    }
}
