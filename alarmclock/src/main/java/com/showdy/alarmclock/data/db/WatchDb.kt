package com.showdy.alarmclock.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.showdy.alarmclock.data.*

/**
 * Created by <b>Showdy</b> on 2020/10/20 10:27
 *
 */
@Database(
    entities = [Gravity::class, Accelerator::class, Step::class, Magnetic::class, HeartRate::class, Gyoscope::class, Clock::class],
    version = 1,
    exportSchema = true
)
abstract class WatchDb : RoomDatabase() {

    abstract fun gravityDao(): GravityDao

    abstract fun stepDao(): StepDao

    abstract fun acceleratorDao(): AcceleratorDao

    abstract fun magneticDao(): MagneticDao

    abstract fun heartRateDao(): HeartRateDao

    abstract fun gyoscopeDao(): GyoscopeDao

    abstract fun clockDao(): ClockDao


    companion object {

        @Volatile
        private var INSTANCE: WatchDb? = null

        operator fun invoke(context: Context): WatchDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WatchDb::class.java,
                    "watch.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(WatchDdCallback())
                    .build()
                INSTANCE = instance
                instance
            }


        }
    }

    /**
     * 数据库的一些操作
     */
    private class WatchDdCallback : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
        }
    }
}