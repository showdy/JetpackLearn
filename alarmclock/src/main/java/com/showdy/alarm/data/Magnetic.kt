package com.showdy.alarm.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:38
 *
 */
@Entity(tableName = "magnetic")
data class Magnetic(
    @ColumnInfo(name="值")
    val values: String,
    @ColumnInfo(name="时间戳")
    val timeStamp: Long
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}