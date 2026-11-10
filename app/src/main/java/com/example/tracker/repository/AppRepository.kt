package com.example.tracker.repository

import com.example.tracker.dao.AchievementDao
import com.example.tracker.dao.DayRecordDao
import com.example.tracker.dao.HealthMilestoneDao
import com.example.tracker.dao.UserDao
import com.example.tracker.data.Achievement
import com.example.tracker.data.DayRecord
import com.example.tracker.data.HealthMilestone
import com.example.tracker.data.User
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userDao: UserDao,
    private val dayRecordDao: DayRecordDao,
    private val healthMilestoneDao: HealthMilestoneDao,
    private val achievementDao: AchievementDao
) {
    // ===== USER =====
    suspend fun insertUser(user: User) = userDao.insertUser(user)
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    suspend fun getUserById(userId: Int) = userDao.getUserById(userId)
    suspend fun getCurrentUser() = userDao.getCurrentUser()
    fun observeCurrentUser(): Flow<User?> = userDao.observeCurrentUser()

    fun getAllUsersFlow(): Flow<List<User>> = userDao.getAllUsersFlow()

    // ===== DAY RECORD =====
    suspend fun insertRecord(record: DayRecord) = dayRecordDao.insertRecord(record)
    suspend fun updateRecord(record: DayRecord) = dayRecordDao.updateRecord(record)
    suspend fun deleteRecord(record: DayRecord) = dayRecordDao.deleteRecord(record)
    fun getAllRecords(): Flow<List<DayRecord>> = dayRecordDao.getAllRecords()
    suspend fun getRecordById(recordId: Int) = dayRecordDao.getRecordById(recordId)
    suspend fun deleteAllRecords() = dayRecordDao.deleteAllRecords()

    // ===== HEALTH MILESTONE =====
    suspend fun insertMilestone(milestone: HealthMilestone) = healthMilestoneDao.insertMilestone(milestone)
    suspend fun updateMilestone(milestone: HealthMilestone) = healthMilestoneDao.updateMilestone(milestone)
    suspend fun deleteMilestone(milestone: HealthMilestone) = healthMilestoneDao.deleteMilestone(milestone)
    fun getAllMilestones(): Flow<List<HealthMilestone>> = healthMilestoneDao.getAllMilestones()
    fun getMilestonesForDays(days: Int): Flow<List<HealthMilestone>> = healthMilestoneDao.getMilestonesForDays(days)

    // ===== ACHIEVEMENT =====
    suspend fun insertAchievement(achievement: Achievement) = achievementDao.insertAchievement(achievement)
    suspend fun updateAchievement(achievement: Achievement) = achievementDao.updateAchievement(achievement)
    suspend fun deleteAchievement(achievement: Achievement) = achievementDao.deleteAchievement(achievement)
    fun getAllAchievements(): Flow<List<Achievement>> = achievementDao.getAllAchievements()
    suspend fun getAchievementByType(type: String) = achievementDao.getAchievementByType(type)
}