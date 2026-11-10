package com.example.tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tracker.data.Achievement
import com.example.tracker.data.DayRecord
import com.example.tracker.data.HealthMilestone
import com.example.tracker.data.User
import com.example.tracker.database.AppDatabase
import com.example.tracker.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        database.userDao(),
        database.dayRecordDao(),
        database.healthMilestoneDao(),
        database.achievementDao()
    )

    // ===== USER =====
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    fun deleteUser(user: User) = viewModelScope.launch {
        repository.deleteUser(user)
    }

    fun getAllUsersFlow(): Flow<List<User>> = repository.getAllUsersFlow()

    fun observeCurrentUser(): Flow<User?> = repository.observeCurrentUser()

    // ===== DAY RECORD =====
    fun insertRecord(record: DayRecord) = viewModelScope.launch {
        repository.insertRecord(record)
    }

    fun updateRecord(record: DayRecord) = viewModelScope.launch {
        repository.updateRecord(record)
    }

    fun deleteRecord(record: DayRecord) = viewModelScope.launch {
        repository.deleteRecord(record)
    }

    fun getAllRecords(): Flow<List<DayRecord>> = repository.getAllRecords()

    suspend fun getRecordById(id: Int): DayRecord? {
        return repository.getRecordById(id)
    }

    // ===== HEALTH MILESTONE =====
    fun insertMilestone(milestone: HealthMilestone) = viewModelScope.launch {
        repository.insertMilestone(milestone)
    }

    fun getAllMilestones(): Flow<List<HealthMilestone>> = repository.getAllMilestones()

    fun getMilestonesForDays(days: Int): Flow<List<HealthMilestone>> =
        repository.getMilestonesForDays(days)

    fun updateMilestone(milestone: HealthMilestone) = viewModelScope.launch {
        repository.updateMilestone(milestone)
    }
    fun deleteMilestone(milestone: HealthMilestone) = viewModelScope.launch {
        repository.deleteMilestone(milestone)
    }

    // ===== ACHIEVEMENT =====
    fun updateAchievement(achievement: Achievement) = viewModelScope.launch {
        repository.updateAchievement(achievement)
    }
    fun deleteAchievement(achievement: Achievement) = viewModelScope.launch {
        repository.deleteAchievement(achievement)
    }

    fun insertAchievement(achievement: Achievement) = viewModelScope.launch {
        repository.insertAchievement(achievement)
    }

    fun getAllAchievements(): Flow<List<Achievement>> = repository.getAllAchievements()
}