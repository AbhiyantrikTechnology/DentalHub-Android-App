package com.abhiyantrik.dentalhub.workers

import android.content.Context
import androidx.work.*
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Patient
import io.objectbox.Box
import java.util.concurrent.TimeUnit

class UploadWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    val patientsBox: Box<Patient> = ObjectBox.boxStore.boxFor(Patient::class.java)

    override fun doWork(): Result {
        return try{
            val allPatients = patientsBox.query().build().find()
            for (patient in allPatients) {

                val data = Data.Builder().putLong("PATIENT_ID", patient.id)
                val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<UploadPatientWorker>()
                    .setInputData(data.build())
                    .setConstraints(DentalApp.uploadConstraints)
                    .setInitialDelay(100, TimeUnit.MILLISECONDS).build()

                val uploadEncounterWorkRequest = OneTimeWorkRequestBuilder<UploadEncounterWorker>()
                    .setInputData(data.build())
                    .setConstraints(DentalApp.uploadConstraints)
                    .build()

                val updatePatientWorkerRequest = OneTimeWorkRequestBuilder<UpdatePatientWorker>()
                    .setInputData(data.build())
                    .setConstraints(DentalApp.uploadConstraints)
                    .build()
                if(!patient.uploaded){
                    WorkManager.getInstance(applicationContext).beginWith(uploadPatientWorkRequest)
                        .then(uploadEncounterWorkRequest).enqueue()
                }else if(patient.updated){
                    WorkManager.getInstance(applicationContext).beginWith(updatePatientWorkerRequest)
                        .then(uploadEncounterWorkRequest).enqueue()
                }else{
                    WorkManager.getInstance(applicationContext).enqueue(uploadEncounterWorkRequest)
                }

            }
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }

    }
}