package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import com.abhiyantrik.dentalhub.models.Treatment as TreatmentModel

class UploadTreatmentWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {
        return try {
            treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            val tempTreatment = treatmentBox.query().equal(
                Treatment_.encounterId,
                encounterId
            ).build().findFirst()!!
            val dbEncounterEntity =
                encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()
            saveTreatmentToServer(dbEncounterEntity, tempTreatment)
            Result.success()
        } catch (e: Exception) {
            Log.d("Exception", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun saveTreatmentToServer(encounter: Encounter?, treatment: Treatment) {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_treatment),
            applicationContext.resources.getString(R.string.uploading_treatment)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)

        if(!treatment.uploaded){
            val call = panelService.addTreatment(
                "JWT $token",
                encounter!!.remote_id,
                treatment.id,
                treatment.tooth18,
                treatment.tooth17,
                treatment.tooth16,
                treatment.tooth15,
                treatment.tooth14,
                treatment.tooth13,
                treatment.tooth12,
                treatment.tooth11,

                treatment.tooth21,
                treatment.tooth22,
                treatment.tooth23,
                treatment.tooth24,
                treatment.tooth25,
                treatment.tooth26,
                treatment.tooth27,
                treatment.tooth28,

                treatment.tooth48,
                treatment.tooth47,
                treatment.tooth46,
                treatment.tooth45,
                treatment.tooth44,
                treatment.tooth43,
                treatment.tooth42,
                treatment.tooth41,

                treatment.tooth31,
                treatment.tooth32,
                treatment.tooth33,
                treatment.tooth34,
                treatment.tooth35,
                treatment.tooth36,
                treatment.tooth37,
                treatment.tooth38,

                treatment.tooth55,
                treatment.tooth54,
                treatment.tooth53,
                treatment.tooth52,
                treatment.tooth51,

                treatment.tooth61,
                treatment.tooth62,
                treatment.tooth63,
                treatment.tooth64,
                treatment.tooth65,

                treatment.tooth85,
                treatment.tooth84,
                treatment.tooth83,
                treatment.tooth82,
                treatment.tooth81,

                treatment.tooth71,
                treatment.tooth72,
                treatment.tooth73,
                treatment.tooth74,
                treatment.tooth75,

                treatment.sdf_whole_mouth,
                treatment.fv_applied,
                treatment.treatment_plan_complete,
                treatment.notes
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempTreatment = response.body() as TreatmentModel
                        val dbTreatmentEntity = treatmentBox.query().equal(
                            Treatment_.encounterId,
                            encounter!!.id
                        ).build().findFirst()!!
                        dbTreatmentEntity.remote_id = tempTreatment.id
                        dbTreatmentEntity.uploaded = true
                        dbTreatmentEntity.updated = false
                        treatmentBox.put(dbTreatmentEntity)
                    }
                }
            }
        }else if(treatment.updated){
            val call = panelService.updateTreatment(
                "JWT $token",
                encounter!!.remote_id,
                treatment.id,
                treatment.tooth18,
                treatment.tooth17,
                treatment.tooth16,
                treatment.tooth15,
                treatment.tooth14,
                treatment.tooth13,
                treatment.tooth12,
                treatment.tooth11,

                treatment.tooth21,
                treatment.tooth22,
                treatment.tooth23,
                treatment.tooth24,
                treatment.tooth25,
                treatment.tooth26,
                treatment.tooth27,
                treatment.tooth28,

                treatment.tooth48,
                treatment.tooth47,
                treatment.tooth46,
                treatment.tooth45,
                treatment.tooth44,
                treatment.tooth43,
                treatment.tooth42,
                treatment.tooth41,

                treatment.tooth31,
                treatment.tooth32,
                treatment.tooth33,
                treatment.tooth34,
                treatment.tooth35,
                treatment.tooth36,
                treatment.tooth37,
                treatment.tooth38,

                treatment.tooth55,
                treatment.tooth54,
                treatment.tooth53,
                treatment.tooth52,
                treatment.tooth51,

                treatment.tooth61,
                treatment.tooth62,
                treatment.tooth63,
                treatment.tooth64,
                treatment.tooth65,

                treatment.tooth85,
                treatment.tooth84,
                treatment.tooth83,
                treatment.tooth82,
                treatment.tooth81,

                treatment.tooth71,
                treatment.tooth72,
                treatment.tooth73,
                treatment.tooth74,
                treatment.tooth75,

                treatment.sdf_whole_mouth,
                treatment.fv_applied,
                treatment.treatment_plan_complete,
                treatment.notes
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempTreatment = response.body() as com.abhiyantrik.dentalhub.models.Treatment
                        val dbTreatmentEntity = treatmentBox.query().equal(
                            Treatment_.encounterId,
                            encounter!!.id
                        ).build().findFirst()!!
                        dbTreatmentEntity.remote_id = tempTreatment.id
                        dbTreatmentEntity.uploaded = true
                        dbTreatmentEntity.updated = false
                        treatmentBox.put(dbTreatmentEntity)
                    }
                }
            }
        }



        DentalApp.cancelNotification(applicationContext, 1001)
    }
}