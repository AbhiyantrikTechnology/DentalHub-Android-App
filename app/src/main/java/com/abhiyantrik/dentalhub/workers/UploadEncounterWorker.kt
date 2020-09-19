package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import java.util.*
import java.util.concurrent.TimeUnit
import com.abhiyantrik.dentalhub.models.Encounter as EncounterModel

class UploadEncounterWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private var patientsBox: Box<Patient> = ObjectBox.boxStore.boxFor(Patient::class.java)
    private var encountersBox: Box<Encounter> =  ObjectBox.boxStore.boxFor(Encounter::class.java)

    override fun doWork(): Result {
        return  try {
            val patientId = inputData.getLong("PATIENT_ID", 0)

            val dbPatientEntity =
                patientsBox.query().equal(Patient_.id, patientId).build().findFirst()


            val allEncounters =
            encountersBox.query().equal(Encounter_.patientId, patientId).build().find()
            Log.d(dbPatientEntity!!.fullName(), allEncounters.size.toString())

            for (eachEncounter in allEncounters) {
                Thread.sleep(500L)

                val data = Data.Builder().putLong("ENCOUNTER_ID", eachEncounter.id)
                    .putLong("PATIENT_ID", dbPatientEntity.id)

                val uploadHistoryWorkerRequest =
                    OneTimeWorkRequestBuilder<UploadHistoryWorker>()
                        .setInputData(data.build())
                        .setConstraints(DentalApp.uploadConstraints)
                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                val uploadScreeningWorkerRequest =
                    OneTimeWorkRequestBuilder<UploadScreeningWorker>()
                        .setInputData(data.build())
                        .setConstraints(DentalApp.uploadConstraints)
                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                val uploadTreatmentWorkerRequest =
                    OneTimeWorkRequestBuilder<UploadTreatmentWorker>()
                        .setInputData(data.build())
                        .setConstraints(DentalApp.uploadConstraints)
                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                val uploadReferralWorkerRequest =
                    OneTimeWorkRequestBuilder<UploadReferralWorker>()
                        .setInputData(data.build())
                        .setConstraints(DentalApp.uploadConstraints)
                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()

                if (!eachEncounter.uploaded) {
                    val uploadIndividualEncounterWorkerRequest =
                        OneTimeWorkRequestBuilder<UploadIndividualEncounterWorker>()
                            .setInputData(data.build())
                            .setConstraints(DentalApp.uploadConstraints)
                            .setInitialDelay(
                            100,
                            TimeUnit.MILLISECONDS
                            ).build()

                    WorkManager.getInstance(applicationContext).beginWith(uploadIndividualEncounterWorkerRequest)
                        .then(listOf(uploadHistoryWorkerRequest, uploadScreeningWorkerRequest, uploadTreatmentWorkerRequest, uploadReferralWorkerRequest))
                        .enqueue()
                }else if(eachEncounter.updated){
                    val updateIndividualEncounterWorkerRequest =
                        OneTimeWorkRequestBuilder<UpdateIndividualEncounterWorker>()
                            .setInputData(data.build())
                            .setConstraints(DentalApp.uploadConstraints)
                            .setInitialDelay(
                                100,
                                TimeUnit.MILLISECONDS
                            ).build()
                    WorkManager.getInstance(applicationContext).beginWith(updateIndividualEncounterWorkerRequest)
                        .then(listOf(uploadHistoryWorkerRequest, uploadScreeningWorkerRequest, uploadTreatmentWorkerRequest, uploadReferralWorkerRequest))
                        .enqueue()
                }else{
                    WorkManager.getInstance(applicationContext).enqueue(listOf(uploadHistoryWorkerRequest, uploadScreeningWorkerRequest, uploadTreatmentWorkerRequest, uploadReferralWorkerRequest))
                }
            }

            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

}