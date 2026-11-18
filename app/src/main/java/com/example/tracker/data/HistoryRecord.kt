package com.example.tracker.data

data class HistoryRecord(
    val record: DayRecord,
    val streak: Int,
    val isRelapse: Boolean,
    val targetDays: Int
)