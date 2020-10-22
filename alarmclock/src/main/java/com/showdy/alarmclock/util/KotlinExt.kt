package com.showdy.alarmclock.util

import com.showdy.alarmclock.App
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by <b>Showdy</b> on 2020/10/16 10:48
 *
 * 拓展方法
 */
const val CLOCK_IN = "clock_in"

fun String.createFile(): File {
    return File(App.instance.filesDir.path + "/${this}.txt").also {
        if (!it.exists()) it.createNewFile()
        it.setWritable(true)
    }
}

private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

private val millFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")

/**
 * 将当前时间格式化为yyyy-MM-dd HH:mm:ss
 */
fun Date.format(): String = formatter.format(this)

fun Date.millFormat():String = millFormatter.format(this)