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
import com.abhiyantrik.dentalhub.models.Screening as ScreeningModel

class UploadScreeningWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var screeningBox: Box<Screening>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {
        return try {
            screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            val tempScreening = screeningBox.query().equal(
                Screening_.encounterId,
                encounterId
            ).build().findFirst()!!
            val dbEncounterEntity =
                encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()
            saveScreeningToServer(dbEncounterEntity, tempScreening)
            Result.success()
        } catch (e: Exception) {
            Log.d("Exception", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun saveScreeningToServer(encounter: Encounter?, screening: Screening) {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_screening),
            applicationContext.resources.getString(R.string.uploading_screening)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)

        if(!screening.uploaded){
            val call = panelService.addScreening(
                "JWT $token",
                encounter!!.remote_id,
                screening.carries_risk,
                screening.decayed_primary_teeth,
                screening.decayed_permanent_teeth,
                screening.cavity_permanent_anterior_teeth,
                screening.cavity_permanent_posterior_teeth,
                screening.reversible_pulpitis,
                screening.need_art_filling,
                screening.need_sealant,
                screening.need_sdf,
                screening.need_extraction,
                screening.active_infection
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempScreening = response.body() as ScreeningModel
                        val dbScreeningEntity = screeningBox.query().equal(
                            Screening_.encounterId,
                            encounter.id
                        ).build().findFirst()!!
                        dbScreeningEntity.remote_id = tempScreening.id
                        dbScreeningEntity.uploaded = true
                        dbScreeningEntity.updated = false
                        screeningBox.put(dbScreeningEntity)


                    }
                }
            }
        } else if (screening.updated){
            val call = panelService.updateScreening(
                "JWT $token",
                encounter!!.remote_id,
                screening.carries_risk,
                screening.decayed_primary_teeth,
                screening.decayed_permanent_teeth,
                screening.cavity_permanent_anterior_teeth,
                screening.cavity_permanent_posterior_teeth,
                screening.reversible_pulpitis,
                screening.need_art_filling,
                screening.need_sealant,
                screening.need_sdf,
                screening.need_extraction,
                screening.active_infection
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val dbScreeningEntity = screeningBox.query().equal(
                            Screening_.encounterId,
                            encounter.id
                        ).build().findFirst()!!
                        dbScreeningEntity.uploaded = true
                        dbScreeningEntity.updated = false
                        screeningBox.put(dbScreeningEntity)
                    }
                }
            }
        }


        DentalApp.cancelNotification(applicationContext, 1001)

    }
}