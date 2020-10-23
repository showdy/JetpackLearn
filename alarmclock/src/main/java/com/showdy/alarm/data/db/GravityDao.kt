package com.showdy.alarm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.showdy.alarm.data.Gravity

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:32
 *
 */
@Dao
interface GravityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gravity: Gravity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(gravity:List<Gravity>)
}