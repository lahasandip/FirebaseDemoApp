package com.example.firebasedemoapp.model

data class User(
    val id: String = "",
    val displayName: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val fcmToken: String = ""
)
