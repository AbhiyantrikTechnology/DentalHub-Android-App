package com.abhiyantrik.dentalhub.utils

import com.abhiyantrik.dentalhub.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class FirebaseConfig {
    var mFirebaseRemoteConfig: FirebaseRemoteConfig
    var configSettings: FirebaseRemoteConfigSettings
    var cacheExpiration: Long = 43200
    var editatbleDuration: Long = 21600

    init {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        configSettings = FirebaseRemoteConfigSettings.Builder().build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults)
    }

    fun fetchEditableTime(): Long {
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener {
            mFirebaseRemoteConfig.fetchAndActivate()
        }

        editatbleDuration = mFirebaseRemoteConfig.getLong("editable_duration")
        return editatbleDuration
    }


}