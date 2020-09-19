package com.abhiyantrik.dentalhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.perf.metrics.AddTrace
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SplashActivity : Activity() {
    private lateinit var context: Context

    @AddTrace(name = "onCreateTraceSplashActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        context = this

        //startService(Intent(this, BootstrapService::class.java))

        Handler().postDelayed({
            val token: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
            val email: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL, "")
            val password: String =
                DentalApp.readFromPreference(context, Constants.PREF_AUTH_PASSWORD, "")
            if (token.isEmpty() || email.isEmpty() || password.isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                val setupComplete =
                    DentalApp.readFromPreference(context, Constants.PREF_SETUP_COMPLETE, "false")
                if (setupComplete == "true") {
                    val selectedLocationId = DentalApp.readIntFromPreference(
                        context,
                        Constants.PREF_SELECTED_LOCATION_ID
                    )
                    val selectedLocationName = DentalApp.readFromPreference(
                        context,
                        Constants.PREF_SELECTED_LOCATION_NAME,
                        ""
                    )
                    val selectedActivityName =
                        DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_NAME, "")
                    val selectedActivityId =
                        DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_ID, "")
                    val remarks =
                        DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_REMARKS, "")


//                    if(DentalApp.geography.isEmpty() || DentalApp.activity.isEmpty()){
//                        startActivity(Intent(this, LocationSelectorActivity::class.java))
//                    }
                    if (selectedActivityName.isEmpty() || selectedLocationName.isEmpty()) {
                        startActivity(Intent(this, LocationSelectorActivity::class.java))
                    } else {
                        DentalApp.fullName = DentalApp.readFromPreference(
                            context,
                            Constants.PREF_PROFILE_FULL_NAME,
                            ""
                        )
                        DentalApp.geography_id = selectedLocationId
                        DentalApp.ward_name = selectedLocationName
                        DentalApp.activity_id = selectedActivityId
                        DentalApp.activity_name = selectedActivityName
                        DentalApp.activityRemarks = remarks
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                } else {
                    startActivity(Intent(this, SetupActivity::class.java))
                }

            }

        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }


}
