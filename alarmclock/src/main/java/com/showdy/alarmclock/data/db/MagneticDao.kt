package com.showdy.alarmclock.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.showdy.alarmclock.data.Magnetic

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:32
 *
 */
@Dao
interface MagneticDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(magnetic: Magnetic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(magnetic:List<Magnetic>)
}