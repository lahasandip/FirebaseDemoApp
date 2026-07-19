package com.example.firebasedemoapp.model

data class Conversation(
    val id: String = "", // This is the other user's ID
    val lastMessage: String = "",
    val otherUserName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
