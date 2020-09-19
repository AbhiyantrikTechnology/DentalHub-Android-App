package com.abhiyantrik.dentalhub.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.abhiyantrik.dentalhub.DentalApp
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*


class LocationTrackerService : Service(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {
    override fun onConnected(p0: Bundle?) {
        // location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    override fun onConnectionSuspended(p0: Int) {
//
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
//
    }

    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    private lateinit var googleApiClient: GoogleApiClient

    var isGPS: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        googleApiClient =
            GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        startLocationUpdates()

        googleApiClient.connect()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.d("LocationTrackerService", "onLocationResult")
                if (locationResult != null) {
                    for (location in locationResult.locations) {
                        Log.d("onLocationResult", location.toString())
                        if (location != null) {
                            DentalApp.location.latitude = location.latitude.toString()
                            DentalApp.location.longitude = location.longitude.toString()
                            Log.d("Location", DentalApp.location.toString())
                        }
                    }
                }
            }
        }

        mFusedLocationClient.lastLocation.addOnSuccessListener {
            Log.d("LocationTrackerService", "onSuccessListener")
            Log.d("locationRequest", locationRequest.toString())
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 60 * 1000 // 60 seconds
        locationRequest.fastestInterval = 30 * 1000 // 30 seconds

        //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this)
    }

}