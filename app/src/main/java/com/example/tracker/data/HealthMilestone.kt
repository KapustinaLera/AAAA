package com.example.tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_milestones")
data class HealthMilestone(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val daysCount: Int,
    val description: String,
    val icon: String
)