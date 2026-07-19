package com.example.firebasedemoapp.model

data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val category: String = "",
    val status: String = "Available", // Available, Sold
    val timestamp: Long = System.currentTimeMillis()
)
