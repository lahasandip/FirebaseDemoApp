package com.example.firebasedemoapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemoapp.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    val currentUser = repository.currentUser

    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            error = null
            repository.signIn(email, password).fold(
                onSuccess = { onSuccess() },
                onFailure = { error = it.message }
            )
            isLoading = false
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            error = null
            repository.signUp(email, password).fold(
                onSuccess = { onSuccess() },
                onFailure = { error = it.message }
            )
            isLoading = false
        }
    }

    fun signOut() {
        repository.signOut()
    }
}
