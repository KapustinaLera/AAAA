package com.example.tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tracker.dao.*
import com.example.tracker.data.*

@Database(
    entities = [
        User::class,
        DayRecord::class,
        HealthMilestone::class,
        Achievement::class
    ],
    version = 2, // <-- СТАЛО 2
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
                )
                    .fallbackToDestructiveMigration() // <-- ДОБАВЛЕНО: удалит старую БД при изменении
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}