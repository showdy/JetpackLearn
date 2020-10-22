package com.showdy.alarmclock.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.showdy.alarmclock.data.Gyoscope

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:32
 *
 */
@Dao
interface GyoscopeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gyoscope: Gyoscope)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(gyoscope:List<Gyoscope>)
}