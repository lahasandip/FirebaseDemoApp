package com.example.firebasedemoapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemoapp.model.User
import com.example.firebasedemoapp.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    var user by mutableStateOf<User?>(null)
    var isLoading by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val userId = repository.userId ?: return
        viewModelScope.launch {
            isLoading = true
            user = repository.getUserProfile(userId)
            isLoading = false
        }
    }

    fun updateProfile(displayName: String, bio: String, onComplete: () -> Unit) {
        val currentUser = user ?: return
        viewModelScope.launch {
            isUpdating = true
            val updatedUser = currentUser.copy(displayName = displayName, bio = bio)
            val result = repository.updateUserProfile(updatedUser)
            if (result.isSuccess) {
                user = updatedUser
                onComplete()
            }
            isUpdating = false
        }
    }
}
