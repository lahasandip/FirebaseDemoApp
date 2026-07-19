package com.example.firebasedemoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemoapp.model.Product
import com.example.firebasedemoapp.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

import com.example.firebasedemoapp.repository.ConfigRepository

import com.example.firebasedemoapp.repository.AuthRepository
import com.example.firebasedemoapp.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ProductRepository = ProductRepository(),
    private val configRepository: ConfigRepository = ConfigRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val notificationRepository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    val products: StateFlow<List<Product>> = repository.getProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val promoMessage = configRepository.promoMessage
    val showPromo = configRepository.showPromo

    private val _notificationCount = MutableStateFlow(0)
    val notificationCount = _notificationCount.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.userId?.let { userId ->
                notificationRepository.listenForNotifications(userId).collectLatest { notifications ->
                    // Count unique buyers/senders
                    _notificationCount.value = notifications.distinctBy { it.chatId }.size
                }
            }
        }
    }

    fun resetNotificationCount() {
        viewModelScope.launch {
            authRepository.userId?.let { userId ->
                notificationRepository.clearNotifications(userId)
            }
        }
    }
}
