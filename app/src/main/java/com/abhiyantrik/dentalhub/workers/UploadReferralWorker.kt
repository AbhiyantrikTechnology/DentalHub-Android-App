package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Referral
import com.abhiyantrik.dentalhub.entities.Referral_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import com.abhiyantrik.dentalhub.models.Referral as ReferralModel

class UploadReferralWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var referralBox: Box<Referral>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {
        return try {
            referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            val tempReferral =
                referralBox.query().equal(Referral_.encounterId, encounterId).build().findFirst()!!
            val dbReferralEntity =
                encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()
            saveReferralToServer(dbReferralEntity, tempReferral)
            Result.success()
        } catch (e: Exception) {
            Log.d("Exception", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun saveReferralToServer(encounter: Encounter?, referral: Referral) {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_referral),
            applicationContext.resources.getString(R.string.uploading_referral)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)

        if(!referral.uploaded){
            val call = panelService.addReferral(
                "JWT $token",
                encounter!!.remote_id,
                referral.id,
                referral.no_referral,
                referral.health_post,
                referral.hygienist,
                referral.dentist,
                referral.general_physician,
                referral.other_details
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempReferral = response.body() as ReferralModel
                        val dbReferralEntity = referralBox.query().equal(
                            Referral_.encounterId,
                            encounter.id
                        ).build().findFirst()!!
                        dbReferralEntity.remote_id = tempReferral.id
                        dbReferralEntity.uploaded = true
                        dbReferralEntity.updated = false
                        referralBox.put(dbReferralEntity)
                    }
                }
            }
        }else if(referral.updated){
            val call = panelService.updateReferral(
                "JWT $token",
                encounter!!.remote_id,
                referral.id,
                referral.no_referral,
                referral.health_post,
                referral.hygienist,
                referral.dentist,
                referral.general_physician,
                referral.other_details
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val dbReferralEntity = referralBox.query().equal(
                            Referral_.encounterId,
                            encounter.id
                        ).build().findFirst()!!
                        dbReferralEntity.uploaded = true
                        dbReferralEntity.updated = false
                        referralBox.put(dbReferralEntity)
                    }
                }
            }
        }


        DentalApp.cancelNotification(applicationContext, 1001)

    }
}