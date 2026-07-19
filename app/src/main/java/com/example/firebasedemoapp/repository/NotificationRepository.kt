package com.example.firebasedemoapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class ChatNotification(
    val id: String = "",
    val fromName: String = "",
    val message: String = "",
    val chatId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class NotificationRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun listenForNotifications(userId: String): Flow<List<ChatNotification>> = callbackFlow {
        val subscription = firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatNotification::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(notifications)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun clearNotifications(userId: String) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("notifications")
                .get()
                .await()
            
            val batch = firestore.batch()
            snapshot.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        } catch (e: Exception) {
            // Log error
        }
    }

    suspend fun sendNotificationPing(toUserId: String, notification: ChatNotification) {
        try {
            firestore.collection("users")
                .document(toUserId)
                .collection("notifications")
                .add(notification)
                .await()
        } catch (e: Exception) {
            // Log error
        }
    }
}
