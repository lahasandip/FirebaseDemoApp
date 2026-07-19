package com.example.firebasedemoapp.repository

import com.example.firebasedemoapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        Result.success(result.user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.let { user ->
            val newUser = User(id = user.uid, email = email, displayName = email.split("@")[0])
            usersCollection.document(user.uid).set(newUser).await()
        }
        Result.success(result.user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserProfile(userId: String): User? = try {
        usersCollection.document(userId).get().await().toObject(User::class.java)
    } catch (e: Exception) {
        null
    }

    suspend fun updateUserProfile(user: User): Result<Unit> = try {
        usersCollection.document(user.id).set(user).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateFcmToken(token: String) {
        val userId = auth.currentUser?.uid ?: return
        try {
            usersCollection.document(userId).update("fcmToken", token).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    fun signOut() {
        auth.signOut()
    }

    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    val userId: String?
        get() = auth.currentUser?.uid
}
