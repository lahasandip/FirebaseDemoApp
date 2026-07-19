package com.example.firebasedemoapp.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.firebasedemoapp.R

class ConfigRepository(private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()) {

    private val _promoMessage = MutableStateFlow("")
    val promoMessage = _promoMessage.asStateFlow()

    private val _showPromo = MutableStateFlow(false)
    val showPromo = _showPromo.asStateFlow()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // Fetch immediately for testing
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(
            "promo_message" to "Welcome to our Marketplace!",
            "show_promo_banner" to false
        ))

        fetchAndActivate()
    }

    fun fetchAndActivate() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _promoMessage.value = remoteConfig.getString("promo_message")
                _showPromo.value = remoteConfig.getBoolean("show_promo_banner")
            }
        }
    }
}
