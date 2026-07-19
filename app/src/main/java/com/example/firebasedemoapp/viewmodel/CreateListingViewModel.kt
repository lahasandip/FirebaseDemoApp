package com.example.firebasedemoapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebasedemoapp.model.Product
import com.example.firebasedemoapp.repository.AuthRepository
import com.example.firebasedemoapp.repository.ProductRepository
import kotlinx.coroutines.launch

class CreateListingViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var title by mutableStateOf("")
    var price by mutableStateOf("")
    var description by mutableStateOf("")
    var category by mutableStateOf("")
    var imageUrl by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun postProduct(onSuccess: () -> Unit) {
        val user = authRepository.currentUser.value
        val userId = user?.uid ?: run {
            error = "You must be logged in to post an item"
            return
        }

        if (title.isBlank() || price.isBlank()) {
            error = "Title and Price are required"
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null
            
            val product = Product(
                title = title,
                price = price.toDoubleOrNull() ?: 0.0,
                description = description,
                category = category,
                imageUrl = imageUrl,
                sellerId = userId,
                sellerName = user.displayName ?: user.email ?: "Unknown Seller"
            )
            productRepository.addProduct(product).fold(
                onSuccess = { onSuccess() },
                onFailure = { error = it.message }
            )
            isLoading = false
        }
    }
}
