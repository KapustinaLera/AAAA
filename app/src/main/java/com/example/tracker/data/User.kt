package com.example.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val birthDate: Long,
    val gender: String,
    val quitStartDate: Long,
    val addictionType: String,
    val dailyBudget: Double,
    val targetDays: Int
)