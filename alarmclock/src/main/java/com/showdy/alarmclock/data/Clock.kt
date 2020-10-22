package com.showdy.alarmclock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by <b>Showdy</b> on 2020/10/19 17:39
 *
 * 打卡包含服药打卡和状态打卡
 */
@Entity(tableName = "clock")
data class Clock(
    @ColumnInfo(name = "打卡类型")
    val type: String,
    @ColumnInfo(name = "时间")
    val time: String,
    @ColumnInfo(name = "状态")
    val status: String
) {

    @PrimaryKey(autoGenerate = true)
    var id :Int =0

    companion object{
        const val MED_CLOCK = "服药打卡"
        const val STATUS_CLOCK = "状态打卡"
    }

    override fun toString(): String {
        return "{type=$type,status=$status,time=$time}\r\n"
    }
}