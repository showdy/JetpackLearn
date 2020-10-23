package com.showdy.alarm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.showdy.alarm.data.Clock

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:32
 *
 */
@Dao
interface ClockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clock:Clock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(clocks:List<Clock>)
}