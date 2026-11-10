package com.example.tracker.dao

import androidx.room.*
import com.example.tracker.data.HealthMilestone
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthMilestoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: HealthMilestone)

    @Update
    suspend fun updateMilestone(milestone: HealthMilestone)

    @Delete
    suspend fun deleteMilestone(milestone: HealthMilestone)

    @Query("SELECT * FROM health_milestones ORDER BY daysCount ASC")
    fun getAllMilestones(): Flow<List<HealthMilestone>>

    @Query("SELECT * FROM health_milestones WHERE daysCount <= :days ORDER BY daysCount DESC")
    fun getMilestonesForDays(days: Int): Flow<List<HealthMilestone>>
}