package com.abhiyantrik.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Activity
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import retrofit2.Callback
import retrofit2.Response

import retrofit2.Call

class BootstrapService : Service() {
    private lateinit var activitiesBox: Box<Activity>
    val TAG = "BootstrapService"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        activitiesBox = ObjectBox.boxStore.boxFor(Activity::class.java)

        //listGeographies()
        //listActivities()
        return super.onStartCommand(intent, flags, startId)
    }

}