package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import com.abhiyantrik.dentalhub.models.Location
import com.abhiyantrik.dentalhub.utils.FirebaseConfig
import com.abhiyantrik.dentalhub.utils.NotificationHelper


class DentalApp : MultiDexApplication(), Configuration.Provider {

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setMinimumLoggingLevel(Log.VERBOSE).build()

    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)

        defaultChannelId =
            applicationContext.packageName + applicationContext.getString(R.string.app_name)
        syncChannelId =
            applicationContext.packageName + applicationContext.getString(R.string.app_name) + "-sync"

        val firebaseConfig = FirebaseConfig()
        editableDuration = firebaseConfig.fetchEditableTime()

        if(readStringSetFromPreference(this, Constants.PREF_ACTIVITY_SUGGESTIONS) != null){
            activitySuggestions = readStringSetFromPreference(this, Constants.PREF_ACTIVITY_SUGGESTIONS)!!.toMutableSet()
        }


        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel."
        )

        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name) + "-sync", "Notification channel for sync service."
        )

    }


    companion object Factory {
        private const val PREF_FILE_NAME = "dentalhub"
        var location: Location = Location("0", "0")

        var geography_id: Int = 0
        var ward_name: String = ""
        var activity_id: String = ""
        var activity_name: String = ""
        var activityRemarks: String = ""

        var defaultChannelId: String = ""
        var syncChannelId: String = ""
        var editableDuration: Long = 86400
        var fullName = ""

        var lastRecallDate = ""
        var lastRecallTime = ""
        var lastDobYearIndex = 0
        var lastDobMonthIndex = 0
        var lastDobDayIndex = 0

        var lastDistrictIndex = Constants.DEFAULT_SELECTED_DISTRICT
        var lastMunicipalityIndex = 0
        var lastWardIndex = 0

        var lastEducationLevel = 0


        var uploadSyncRunning = false
        var downloadSyncRunning = false

        var uploadConstraints: Constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        var downloadConstraints: Constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        var activitySuggestions = mutableSetOf<String>()


        fun saveToPreference(context: Context, preferenceName: String, preferenceValue: String) {
            val sharedPreferences =
                context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(preferenceName, preferenceValue)
            editor.apply()
        }

        fun canMakeCall(context: Context): Boolean{
            val pm = context.packageManager
            return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        }

        fun saveIntToPreference(context: Context, preferenceName: String, preferenceValue: Int) {
            val sharedPreferences =
                context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(preferenceName, preferenceValue)
            editor.apply()
        }

        fun addStringToPreference(context: Context, preferenceValue: String) {
            activitySuggestions.add(preferenceValue)
            Log.d("Suggestions", activitySuggestions.toString())
            val sharedPreferences =
                context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putStringSet(Constants.PREF_ACTIVITY_SUGGESTIONS, activitySuggestions)
            editor.apply()
        }

        fun readStringSetFromPreference(context: Context, preferenceName: String): MutableSet<String>? {
            val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            return prefs.getStringSet(preferenceName, emptySet())
        }


        fun removeFromPreference(context: Context, preferenceName: String) {
            val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.remove(preferenceName)
            editor.apply()
        }

        fun readFromPreference(
            context: Context,
            preferenceName: String,
            defaultValue: String
        ): String {
            val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            return prefs.getString(preferenceName, defaultValue).toString()
        }

        fun readIntFromPreference(context: Context, preferenceName: String): Int {
            val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            return prefs.getInt(preferenceName, 0)
        }

        fun hasAuthDetails(context: Context): Boolean {
            val email = readFromPreference(context, Constants.PREF_AUTH_EMAIL, "")
            val password = readFromPreference(context, Constants.PREF_AUTH_PASSWORD, "")
            val socialAuth = readFromPreference(context, Constants.PREF_AUTH_SOCIAL, "false")
            var status = false
            if (socialAuth == "true") {
                status = true
            }
            if (email.isNotEmpty() && password.isNotEmpty()) {
                status = true
            }
            return status
        }

        fun clearAuthDetails(context: Context) {
            removeFromPreference(context, Constants.PREF_AUTH_TOKEN)
            removeFromPreference(context, Constants.PREF_AUTH_EMAIL)
            removeFromPreference(context, Constants.PREF_AUTH_PASSWORD)
            removeFromPreference(context, Constants.PREF_AUTH_SOCIAL)
            removeFromPreference(context, Constants.PREF_ACTIVITY_NAME)
            removeFromPreference(context, Constants.PREF_SELECTED_LOCATION_ID)
            removeFromPreference(context, Constants.PREF_SELECTED_LOCATION_NAME)
        }

        fun isConnectedToWifi(context: Context): Boolean {
            val connManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return mWifi.isConnected

        }

        fun displayNotification(
            context: Context,
            id: Int,
            title: String,
            desc: String,
            longDesc: String
        ) {
            val notificationBuilder = NotificationCompat.Builder(context, syncChannelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(desc)
                .setTicker(context.getString(R.string.sync_ticker))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(longDesc)
                )
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(context)

            notificationManager.notify(id, notificationBuilder.build())
        }

        fun cancelNotification(context: Context, id: Int) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(id)
        }


    }
}