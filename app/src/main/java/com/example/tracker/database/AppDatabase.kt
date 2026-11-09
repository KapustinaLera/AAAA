package com.example.tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tracker.dao.AchievementDao
import com.example.tracker.dao.DayRecordDao
import com.example.tracker.dao.HealthMilestoneDao
import com.example.tracker.dao.UserDao
import com.example.tracker.data.Achievement
import com.example.tracker.data.DayRecord
import com.example.tracker.data.HealthMilestone
import com.example.tracker.data.User

@Database(
    entities = [
        User::class,
        DayRecord::class,
        HealthMilestone::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun dayRecordDao(): DayRecordDao
    abstract fun healthMilestoneDao(): HealthMilestoneDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}