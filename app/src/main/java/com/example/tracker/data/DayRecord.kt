package com.example.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_records")
data class DayRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val cravingLevel: Int,
    val consumedAmount: String,
    val wellBeing: Int,
    val mood: String,
    val sleepQuality: Int,
    val physicalActivity: Boolean,
    val notes: String,
    val photoPath: String?
)