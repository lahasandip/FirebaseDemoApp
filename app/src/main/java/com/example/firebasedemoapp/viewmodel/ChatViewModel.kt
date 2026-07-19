package com.example.firebasedemoapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemoapp.model.Message
import com.example.firebasedemoapp.repository.AuthRepository
import com.example.firebasedemoapp.repository.ChatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.firebasedemoapp.repository.ChatNotification
import com.example.firebasedemoapp.repository.NotificationRepository

class ChatViewModel(
    private val otherUserId: String,
    private val repository: ChatRepository = ChatRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val notificationRepository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val currentUserId = authRepository.userId ?: ""
    private val chatId = repository.generateChatId(currentUserId, otherUserId)
    private var otherUserName by mutableStateOf("User")

    val messages: StateFlow<List<Message>> = repository.getMessages(chatId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.getOtherUserName(otherUserId)?.let { name ->
                otherUserName = name
            }
        }
    }

    fun sendMessage(content: String) {
        val user = authRepository.currentUser.value
        val userId = user?.uid ?: return
        val senderName = user.displayName ?: user.email?.split("@")?.get(0) ?: "Unknown"
        
        val message = Message(
            senderId = userId,
            senderName = senderName,
            content = content
        )
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId, 
                message = message, 
                senderName = senderName,
                recipientId = otherUserId,
                recipientName = otherUserName
            )
            
            // Send real-time notification ping
            val ping = ChatNotification(
                fromName = senderName,
                message = content,
                chatId = userId
            )
            notificationRepository.sendNotificationPing(otherUserId, ping)
        }
    }
}
