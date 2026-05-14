package com.jalsanchay.tracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalsanchay.tracker.data.Result
import com.jalsanchay.tracker.data.model.RainfallEntry
import com.jalsanchay.tracker.data.model.UserProfile
import com.jalsanchay.tracker.data.repository.AuthRepository
import com.jalsanchay.tracker.data.repository.RainfallRepository
import com.jalsanchay.tracker.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RainfallViewModel(
    private val rainfallRepository: RainfallRepository = RainfallRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _entries = MutableStateFlow<List<RainfallEntry>>(emptyList())
    val entries: StateFlow<List<RainfallEntry>> = _entries

    private val _totalLiters = MutableStateFlow(0.0)
    val totalLiters: StateFlow<Double> = _totalLiters

    private val _todayLiters = MutableStateFlow(0.0)
    val todayLiters: StateFlow<Double> = _todayLiters

    private val _profileUpdateState = MutableStateFlow<Result<Boolean>?>(null)
    val profileUpdateState: StateFlow<Result<Boolean>?> = _profileUpdateState

    private val _addEntryState = MutableStateFlow<Result<Boolean>?>(null)
    val addEntryState: StateFlow<Result<Boolean>?> = _addEntryState

    init {
        val uid = authRepository.currentUser?.uid
        if (uid != null) {
            fetchProfile(uid)
            fetchEntries(uid)
        }
    }

    private fun fetchProfile(uid: String) {
        viewModelScope.launch {
            rainfallRepository.getUserProfile(uid).collectLatest { result ->
                if (result is Result.Success) {
                    _userProfile.value = result.data.withAuthFallback(uid)
                }
            }
        }
    }

    private fun fetchEntries(uid: String) {
        viewModelScope.launch {
            rainfallRepository.getRainfallEntries(uid).collectLatest { result ->
                if (result is Result.Success) {
                    _entries.value = result.data
                    calculateStats(result.data)
                }
            }
        }
    }

    private fun calculateStats(entries: List<RainfallEntry>) {
        val total = entries.sumOf { it.litersHarvested }
        _totalLiters.value = total
        
        val today = entries.filter { DateUtils.isToday(it.date) }.sumOf { it.litersHarvested }
        _todayLiters.value = today
    }

    fun addEntry(entry: RainfallEntry) {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _addEntryState.value = Result.Error(Exception("Please login again"))
            return
        }
        viewModelScope.launch {
            val localEntry = if (entry.id.isBlank()) {
                entry.copy(id = "rain_${entry.timestamp}")
            } else {
                entry
            }

            _addEntryState.value = Result.Loading
            _entries.value = listOf(localEntry) + _entries.value.filterNot { it.id == localEntry.id }
            calculateStats(_entries.value)
            _addEntryState.value = rainfallRepository.addRainfallEntry(uid, localEntry)
        }
    }

    fun deleteEntry(entryId: String) {
        val uid = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            rainfallRepository.deleteRainfallEntry(uid, entryId)
        }
    }

    fun updateProfile(updates: Map<String, Any>) {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _profileUpdateState.value = Result.Error(Exception("Please login again"))
            return
        }
        viewModelScope.launch {
            _profileUpdateState.value = Result.Loading
            _userProfile.value = applyProfileUpdates(uid, _userProfile.value, updates)
            _profileUpdateState.value = rainfallRepository.updateUserProfile(uid, updates)
        }
    }

    private fun applyProfileUpdates(
        uid: String,
        currentProfile: UserProfile?,
        updates: Map<String, Any>
    ): UserProfile {
        return (currentProfile ?: UserProfile(uid = uid)).copy(
            roofAreaSqFt = updates["roofAreaSqFt"] as? Double ?: currentProfile?.roofAreaSqFt ?: 0.0,
            tankCapacityLiters = updates["tankCapacityLiters"] as? Double ?: currentProfile?.tankCapacityLiters ?: 1000.0,
            householdDailyUsageLiters = updates["householdDailyUsageLiters"] as? Double
                ?: currentProfile?.householdDailyUsageLiters
                ?: 540.0,
            runoffCoefficient = updates["runoffCoefficient"] as? Double ?: currentProfile?.runoffCoefficient ?: 0.8
        )
    }

    private fun UserProfile.withAuthFallback(uid: String): UserProfile {
        val authUser = authRepository.currentUser
        val fallbackEmail = authUser?.email.orEmpty()
        val fallbackName = authUser?.displayName.orEmpty()
            .ifBlank { fallbackEmail.substringBefore("@").takeIf { it.isNotBlank() }.orEmpty() }

        return copy(
            uid = this.uid.ifBlank { uid },
            name = name.ifBlank { fallbackName.ifBlank { "User" } },
            email = email.ifBlank { fallbackEmail.ifBlank { "Not available" } }
        )
    }

    fun clearProfileUpdateState() {
        _profileUpdateState.value = null
    }

    fun clearAddEntryState() {
        _addEntryState.value = null
    }
}
