package com.jalsanchay.tracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalsanchay.tracker.data.Result
import com.jalsanchay.tracker.data.model.UserProfile
import com.jalsanchay.tracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow<Result<UserProfile>?>(null)
    val authState: StateFlow<Result<UserProfile>?> = _authState

    private val _signupState = MutableStateFlow<Result<Boolean>?>(null)
    val signupState: StateFlow<Result<Boolean>?> = _signupState

    private val _resetState = MutableStateFlow<Result<Boolean>?>(null)
    val resetState: StateFlow<Result<Boolean>?> = _resetState

    val currentUser = repository.currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Result.Loading
            _authState.value = repository.login(email, password)
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signupState.value = Result.Loading
            _signupState.value = repository.signUp(name, email, password)
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _resetState.value = Result.Loading
            _resetState.value = repository.sendPasswordReset(email)
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = null
    }

    fun clearStates() {
        _signupState.value = null
        _resetState.value = null
    }
}
