package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.Patient
import com.abhiyantrik.dentalhub.utils.DateHelper
import io.objectbox.Box
import java.util.concurrent.TimeUnit

class DownloadPatientWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var patientsBox: Box<com.abhiyantrik.dentalhub.entities.Patient>

    override fun doWork(): Result {
        return try {
            patientsBox =
                ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Patient::class.java)

            downloadPatients()
            Result.success()
        } catch (e: Exception) {
            Log.d("Exception", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun downloadPatients() {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            "Syncing...",
            "Downloading patients ...",
            "Downloading patients ..."
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.getPatients("JWT $token")


        val response = call.execute()
        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val allPatients = response.body() as List<Patient>
                    for (patient in allPatients) {
                        val existingPatient = patientsBox.query().equal(
                            Patient_.remote_id,
                            patient.id
                        ).build().count()
                        if (existingPatient > 0) {
                            Log.d("DownloadPatientWorker",  "Patient already exists.")
                            //tvMessage.text = tvMessage.text.toString() + existingPatient.fullName()+" already exists.\n"

                            val existingPatientEncounter = patientsBox.query().equal(
                                Patient_.remote_id,
                                patient.id
                            ).build().findFirst()!!
                            loadEncounterData(existingPatientEncounter.remote_id)

                        } else {
                            val patientEntity = com.abhiyantrik.dentalhub.entities.Patient()
                            patientEntity.remote_id = patient.id
                            patientEntity.first_name = patient.first_name
                            try {
                                patientEntity.middle_name = patient.middle_name!!
                            } catch (e: java.lang.Exception) {
                                patientEntity.middle_name = ""
                            }
                            patientEntity.last_name = patient.last_name
                            patientEntity.gender = patient.gender
                            patientEntity.dob = patient.dob
                            patientEntity.phone = patient.phone
                            patientEntity.education = patient.education
                            patientEntity.ward = patient.ward
                            patientEntity.municipality = patient.municipality
                            patientEntity.district = patient.district
                            patientEntity.latitude = patient.latitude
                            patientEntity.longitude = patient.longitude
                            patientEntity.geography_id = patient.geography
                            patientEntity.activityarea_id = patient.activity_area
                            try {
                                patientEntity.recall_time = patient.recall_time!!
                            }catch (e: Exception){
                                patientEntity.recall_time = ""
                            }
                            try {
                                patientEntity.recall_date = patient.recall_date!!
                            }catch (e: Exception){
                                patientEntity.recall_date = ""
                            }
                            patientEntity.recall_geography = patient.recall_geography

                            patientEntity.uploaded = true
                            patientEntity.updated = false
                            patientEntity.recall = null
                            patientEntity.author = patient.author
                            try {
                                patientEntity.created_at = patient.created_at
                            }catch (e: Exception){
                                patientEntity.created_at = DateHelper.getCurrentDate()
                            }
                            try{
                                patientEntity.updated_at = patient.updated_at
                            }catch (e: Exception){
                                patientEntity.updated_at = DateHelper.getCurrentDate()
                            }

                            if(patient.updated_by==null){
                                patientEntity.updated_by = patient.author
                            }else{
                                patientEntity.updated_by = patient.updated_by
                            }

                            patientsBox.put(patientEntity)
                            DentalApp.displayNotification(
                                applicationContext,
                                1001,
                                applicationContext.resources.getString(R.string.sync_ticker),
                                patient.fullName(),
                                applicationContext.resources.getString(R.string.downloading_patient_detail)
                            )
                            loadEncounterData(patient.id)
                        }

                    }
                }
            }
        } else {
            Log.d("downloadPatients", response.message())
        }

        DentalApp.cancelNotification(applicationContext, 1001)
    }

    private fun loadEncounterData(patientId: String) {
        val data = Data.Builder().putString("PATIENT_ID", patientId)
        val downloadEncounterWorkerRequest = OneTimeWorkRequestBuilder<DownloadEncounterWorker>()
            .setInputData(data.build())
            .setConstraints(DentalApp.downloadConstraints)
            .setInitialDelay(
                100,
                TimeUnit.MILLISECONDS
            ).build()
        WorkManager.getInstance(applicationContext).enqueue(downloadEncounterWorkerRequest)
    }
}