package com.example.firebasedemoapp.repository

import com.example.firebasedemoapp.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

import com.example.firebasedemoapp.model.Conversation

class ChatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Message::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    fun getConversations(userId: String): Flow<List<Conversation>> = callbackFlow {
        val subscription = firestore.collection("users")
            .document(userId)
            .collection("conversations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val convos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Conversation::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(convos)
            }
        awaitClose { subscription.remove() }
    }

    fun generateChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    suspend fun getOtherUserName(userId: String): String? = try {
        val doc = firestore.collection("users").document(userId).get().await()
        doc.getString("displayName") ?: doc.getString("email")?.split("@")?.get(0)
    } catch (e: Exception) {
        null
    }

    suspend fun sendMessage(chatId: String, message: Message, senderName: String, recipientId: String, recipientName: String) = try {
        val batch = firestore.batch()
        
        // 1. Add message to chat
        val msgRef = firestore.collection("chats").document(chatId).collection("messages").document()
        batch.set(msgRef, message.copy(id = msgRef.id))
        
        // 2. Update sender's conversation list
        val senderConvoRef = firestore.collection("users").document(message.senderId).collection("conversations").document(recipientId)
        batch.set(senderConvoRef, Conversation(id = recipientId, lastMessage = message.content, otherUserName = recipientName, timestamp = message.timestamp))
        
        // 3. Update recipient's conversation list
        val recipientConvoRef = firestore.collection("users").document(recipientId).collection("conversations").document(message.senderId)
        batch.set(recipientConvoRef, Conversation(id = message.senderId, lastMessage = message.content, otherUserName = senderName, timestamp = message.timestamp))
        
        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
