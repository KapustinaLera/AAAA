package com.example.tracker.dao

import androidx.room.*
import com.example.tracker.data.DayRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DayRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DayRecord): Long

    @Update
    suspend fun updateRecord(record: DayRecord)

    @Delete
    suspend fun deleteRecord(record: DayRecord)

    @Query("SELECT * FROM day_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<DayRecord>>

    @Query("SELECT * FROM day_records WHERE id = :recordId")
    suspend fun getRecordById(recordId: Int): DayRecord?

    @Query("DELETE FROM day_records")
    suspend fun deleteAllRecords()
}