package com.example.firebasedemoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.firebasedemoapp.repository.AuthRepository
import com.example.firebasedemoapp.ui.navigation.MarketplaceNavGraph
import com.google.firebase.messaging.FirebaseMessaging

import androidx.compose.runtime.LaunchedEffect
import com.example.firebasedemoapp.ui.navigation.Screen
import com.example.firebasedemoapp.ui.theme.FirebaseDemoAppTheme

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.firebasedemoapp.repository.NotificationRepository
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val authRepository = AuthRepository()
    private val notificationRepository = NotificationRepository()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM", "Notification permission granted")
        } else {
            Log.d("FCM", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        askNotificationPermission()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                lifecycleScope.launch {
                    authRepository.updateFcmToken(token)
                }
            }
        }

        lifecycleScope.launch {
            authRepository.currentUser.collectLatest { user ->
                user?.uid?.let { userId ->
                    notificationRepository.listenForNotifications(userId).collectLatest { notifications ->
                        // Show notification for the latest one if it's new
                        notifications.firstOrNull()?.let { lastPing ->
                            showLocalNotification(lastPing.fromName, lastPing.message, lastPing.chatId)
                        }
                    }
                }
            }
        }

        setContent {
            FirebaseDemoAppTheme {
                val navController = rememberNavController()
                
                // Handle intent from notification
                LaunchedEffect(intent) {
                    val chatId = intent.getStringExtra("chatId")
                    if (!chatId.isNullOrBlank()) {
                        navController.navigate(Screen.ChatDetail.createRoute(chatId))
                    }
                }

                MarketplaceNavGraph(
                    navController = navController,
                    isLoggedIn = authRepository.isUserLoggedIn
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showLocalNotification(title: String, message: String, chatId: String) {
        val channelId = "chat_messages"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Chat Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("chatId", chatId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
