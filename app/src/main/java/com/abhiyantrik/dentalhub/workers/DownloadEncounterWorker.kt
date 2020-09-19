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
import com.abhiyantrik.dentalhub.models.Encounter
import io.objectbox.Box

class DownloadEncounterWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<com.abhiyantrik.dentalhub.entities.Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentsBox: Box<Treatment>
    private lateinit var referralsBox: Box<Referral>

    override fun doWork(): Result {
        return try {
            patientsBox =
                ObjectBox.boxStore.boxFor(Patient::class.java)
            encountersBox =
                ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Encounter::class.java)
            screeningBox =
                ObjectBox.boxStore.boxFor(Screening::class.java)
            treatmentsBox =
                ObjectBox.boxStore.boxFor(Treatment::class.java)
            historyBox =
                ObjectBox.boxStore.boxFor(History::class.java)
            referralsBox =
                ObjectBox.boxStore.boxFor(Referral::class.java)

            val patientId = inputData.getString("PATIENT_ID")!!
            downloadEncounters(patientId)
            Result.success()
        } catch (e: Exception) {
            Log.d("Exception", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun downloadEncounters(patientId: String) {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.downloading_encounters),
            applicationContext.resources.getString(R.string.downloading_encounters)
        )
        val dbPatientEntity =
            patientsBox.query().equal(Patient_.remote_id, patientId).build().findFirst()

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.getEncounter("JWT $token", patientId)
        val response = call.execute()
        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val allEncounters = response.body() as List<Encounter>
                    for (encounter in allEncounters) {
                        if (encountersBox.query().equal(
                                Encounter_.remote_id,
                                encounter.id
                            ).build().find().size > 0
                        ) {
                            Log.d("", "Encounter already downloaded.")
                        } else {

                            Log.d("Encounter Download", "Encounter downloading of ${encounter.id}.")
                            Log.d("Encounter list", encounter.toString())

                            val encounterEntity = com.abhiyantrik.dentalhub.entities.Encounter()
                            encounterEntity.encounter_type = encounter.encounter_type
                            encounterEntity.created_at = encounter.created_at
                            if(encounter.updated_at==null){
                                encounterEntity.updated_at = encounter.created_at
                            }else{
                                encounterEntity.updated_at = encounter.updated_at!!
                            }

                            if(encounter.other_problem==null){
                                encounterEntity.other_problem = ""
                            }else{
                                encounterEntity.other_problem = encounter.other_problem!!
                            }

                            encounterEntity.remote_id = encounter.id
                            encounterEntity.patient?.target = dbPatientEntity
                            encounterEntity.ward_id = encounter.ward_id
                            encounterEntity.activityarea_id = encounter.activity_area
                            encounterEntity.author = encounter.author
                            if(encounterEntity.updated_by==null){
                                encounterEntity.updated_by = encounter.author
                            }else{
                                encounterEntity.updated_by = encounter.updated_by
                            }

                            encounterEntity.uploaded = true
                            encounterEntity.updated = false
                            encountersBox.put(encounterEntity)

                            val dbEncounterEntity = encountersBox.query().equal(Encounter_.patientId, dbPatientEntity!!.id).orderDesc(
                                Encounter_.id
                            ).build().findFirst()

                            // save history
                            if(encounter.history != null ){
                                if (historyBox.query().equal(
                                        History_.remote_id,
                                        encounter.history!!.id
                                    ).build().find().size > 0
                                ) {
                                    Log.d("History", "History Already downloaded")
                                } else {
                                    DentalApp.displayNotification(
                                        applicationContext,
                                        1001,
                                        applicationContext.resources.getString(R.string.sync_ticker),
                                        applicationContext.resources.getString(R.string.downloading_history),
                                        applicationContext.resources.getString(R.string.downloading_history)
                                    )

                                    val historyEntity = History()
                                    if (encounter.history != null) {
                                        historyEntity.remote_id = encounter.history!!.id
                                        historyEntity.encounter?.target = dbEncounterEntity
                                        historyEntity.blood_disorder =
                                            encounter.history!!.blood_disorder
                                        historyEntity.diabetes = encounter.history!!.diabetes
                                        historyEntity.liver_problem = encounter.history!!.liver_problem
                                        historyEntity.rheumatic_fever =
                                            encounter.history!!.rheumatic_fever
                                        historyEntity.seizuers_or_epilepsy =
                                            encounter.history!!.seizuers_or_epilepsy
                                        historyEntity.hepatitis_b_or_c =
                                            encounter.history!!.hepatitis_b_or_c
                                        historyEntity.hiv = encounter.history!!.hiv
                                        try {
                                            historyEntity.other = encounter.history!!.other
                                        }catch (e: Exception){
                                            historyEntity.other = ""
                                        }
                                        historyEntity.no_underlying_medical_condition =
                                            encounter.history!!.no_underlying_medical_condition
                                        try {
                                            historyEntity.medications = encounter.history!!.medications
                                        }catch (e: Exception){
                                            historyEntity.medications = ""
                                        }
                                        historyEntity.not_taking_any_medications =
                                            encounter.history!!.not_taking_any_medications
                                        historyEntity.no_allergies = encounter.history!!.no_allergies
                                        try {
                                            historyEntity.allergies = encounter.history!!.allergies
                                        }catch (e: Exception){
                                            historyEntity.allergies = ""
                                        }
                                        historyEntity.updated = false
                                        historyEntity.uploaded = true

                                    }
                                    historyBox.put(historyEntity)
                                }
                            } else{
                                var newHistory = History()
                                newHistory.encounter?.target = dbEncounterEntity
                                historyBox.put(newHistory)
                            }


                            if (encounter.screening != null) {
                                // save screening
                                if (screeningBox.query().equal(
                                        Screening_.remote_id,
                                        encounter.screening!!.id
                                    ).build().find().size > 0
                                ) {
                                    Log.d("Screening", "Already downloaded")
                                } else {
                                    DentalApp.displayNotification(
                                        applicationContext,
                                        1001,
                                        applicationContext.resources.getString(R.string.sync_ticker),
                                        applicationContext.resources.getString(R.string.downloading_screening),
                                        applicationContext.resources.getString(R.string.downloading_screening)
                                    )
                                    val screeningEntity = Screening()
                                    if (encounter.screening != null) {
                                        screeningEntity.remote_id = encounter.screening!!.id.toString()
                                        screeningEntity.encounter?.target = dbEncounterEntity
                                        screeningEntity.carries_risk =
                                            encounter.screening!!.carries_risk
                                        screeningEntity.decayed_primary_teeth =
                                            encounter.screening!!.decayed_primary_teeth
                                        screeningEntity.decayed_permanent_teeth =
                                            encounter.screening!!.decayed_permanent_teeth
                                        screeningEntity.cavity_permanent_anterior_teeth =
                                            encounter.screening!!.cavity_permanent_anterior_teeth
                                        screeningEntity.cavity_permanent_posterior_teeth =
                                            encounter.screening!!.cavity_permanent_posterior_teeth
                                        screeningEntity.reversible_pulpitis =
                                            encounter.screening!!.reversible_pulpitis
                                        screeningEntity.need_art_filling =
                                            encounter.screening!!.need_art_filling
                                        screeningEntity.need_sealant =
                                            encounter.screening!!.need_sealant
                                        screeningEntity.need_sdf = encounter.screening!!.need_sdf
                                        screeningEntity.need_extraction =
                                            encounter.screening!!.need_extraction
                                        screeningEntity.active_infection =
                                            encounter.screening!!.active_infection
                                        screeningEntity.updated = false
                                        screeningEntity.uploaded = true
                                    }
                                    screeningBox.put(screeningEntity)

                                }
                            } else {
                                var newScreening = Screening()
                                newScreening.encounter?.target = dbEncounterEntity
                                screeningBox.put(newScreening)
                            }

                            if (encounter.treatment != null) {
                                // save treatment
                                if (treatmentsBox.query().equal(
                                        Treatment_.remote_id,
                                        encounter.treatment!!.id
                                    ).build().find().size > 0
                                ) {
                                    Log.d("Treatment", "Already downloaded")
                                } else {
                                    DentalApp.displayNotification(
                                        applicationContext,
                                        1001,
                                        applicationContext.resources.getString(R.string.sync_ticker),
                                        applicationContext.resources.getString(R.string.downloading_treatment),
                                        applicationContext.resources.getString(R.string.downloading_treatment)
                                    )
                                    val treatmentEntity = Treatment()
                                    if (encounter.treatment != null) {
                                        treatmentEntity.remote_id = encounter.treatment!!.id
                                        treatmentEntity.encounter?.target = dbEncounterEntity
                                        treatmentEntity.sdf_whole_mouth =
                                            encounter.treatment!!.sdf_whole_mouth
                                        treatmentEntity.fv_applied = encounter.treatment!!.fv_applied
                                        treatmentEntity.treatment_plan_complete =
                                            encounter.treatment!!.treatment_plan_complete
                                        try {
                                            treatmentEntity.notes = encounter.treatment!!.notes
                                        }catch (e: Exception){
                                            treatmentEntity.notes = ""
                                        }

                                        treatmentEntity.tooth11 = encounter.treatment!!.tooth11
                                        treatmentEntity.tooth12 = encounter.treatment!!.tooth12
                                        treatmentEntity.tooth13 = encounter.treatment!!.tooth13
                                        treatmentEntity.tooth14 = encounter.treatment!!.tooth14
                                        treatmentEntity.tooth15 = encounter.treatment!!.tooth15
                                        treatmentEntity.tooth16 = encounter.treatment!!.tooth16
                                        treatmentEntity.tooth17 = encounter.treatment!!.tooth17
                                        treatmentEntity.tooth18 = encounter.treatment!!.tooth18


                                        treatmentEntity.tooth21 = encounter.treatment!!.tooth21
                                        treatmentEntity.tooth22 = encounter.treatment!!.tooth22
                                        treatmentEntity.tooth23 = encounter.treatment!!.tooth23
                                        treatmentEntity.tooth24 = encounter.treatment!!.tooth24
                                        treatmentEntity.tooth25 = encounter.treatment!!.tooth25
                                        treatmentEntity.tooth26 = encounter.treatment!!.tooth26
                                        treatmentEntity.tooth27 = encounter.treatment!!.tooth27
                                        treatmentEntity.tooth28 = encounter.treatment!!.tooth28

                                        treatmentEntity.tooth31 = encounter.treatment!!.tooth31
                                        treatmentEntity.tooth32 = encounter.treatment!!.tooth32
                                        treatmentEntity.tooth33 = encounter.treatment!!.tooth33
                                        treatmentEntity.tooth34 = encounter.treatment!!.tooth34
                                        treatmentEntity.tooth35 = encounter.treatment!!.tooth35
                                        treatmentEntity.tooth36 = encounter.treatment!!.tooth36
                                        treatmentEntity.tooth37 = encounter.treatment!!.tooth37
                                        treatmentEntity.tooth38 = encounter.treatment!!.tooth38


                                        treatmentEntity.tooth41 = encounter.treatment!!.tooth41
                                        treatmentEntity.tooth42 = encounter.treatment!!.tooth42
                                        treatmentEntity.tooth43 = encounter.treatment!!.tooth43
                                        treatmentEntity.tooth44 = encounter.treatment!!.tooth44
                                        treatmentEntity.tooth45 = encounter.treatment!!.tooth45
                                        treatmentEntity.tooth46 = encounter.treatment!!.tooth46
                                        treatmentEntity.tooth47 = encounter.treatment!!.tooth47
                                        treatmentEntity.tooth48 = encounter.treatment!!.tooth48


                                        treatmentEntity.tooth51 = encounter.treatment!!.tooth51
                                        treatmentEntity.tooth52 = encounter.treatment!!.tooth52
                                        treatmentEntity.tooth53 = encounter.treatment!!.tooth53
                                        treatmentEntity.tooth54 = encounter.treatment!!.tooth54
                                        treatmentEntity.tooth55 = encounter.treatment!!.tooth55

                                        treatmentEntity.tooth61 = encounter.treatment!!.tooth61
                                        treatmentEntity.tooth62 = encounter.treatment!!.tooth62
                                        treatmentEntity.tooth63 = encounter.treatment!!.tooth63
                                        treatmentEntity.tooth64 = encounter.treatment!!.tooth64
                                        treatmentEntity.tooth65 = encounter.treatment!!.tooth65

                                        treatmentEntity.tooth71 = encounter.treatment!!.tooth71
                                        treatmentEntity.tooth72 = encounter.treatment!!.tooth72
                                        treatmentEntity.tooth73 = encounter.treatment!!.tooth73
                                        treatmentEntity.tooth74 = encounter.treatment!!.tooth74
                                        treatmentEntity.tooth75 = encounter.treatment!!.tooth75

                                        treatmentEntity.tooth81 = encounter.treatment!!.tooth81
                                        treatmentEntity.tooth82 = encounter.treatment!!.tooth82
                                        treatmentEntity.tooth83 = encounter.treatment!!.tooth83
                                        treatmentEntity.tooth84 = encounter.treatment!!.tooth84
                                        treatmentEntity.tooth85 = encounter.treatment!!.tooth85

                                        treatmentEntity.updated = false
                                        treatmentEntity.uploaded = true

                                    }
                                    treatmentsBox.put(treatmentEntity)
                                }
                            } else {
                                var newTreatment = Treatment()
                                newTreatment.encounter?.target = dbEncounterEntity
                                treatmentsBox.put(newTreatment)
                            }

                            if (encounter.referral != null) {
                                // save referral
                                if (referralsBox.query().equal(
                                        Referral_.remote_id,
                                        encounter.referral!!.id.toString()
                                    ).build().find().size > 0
                                ) {
                                    Log.d("Referral", "Already downloaded")
                                } else {
                                    DentalApp.displayNotification(
                                        applicationContext,
                                        1001,
                                        applicationContext.resources.getString(R.string.sync_ticker),
                                        applicationContext.resources.getString(R.string.downloading_referral),
                                        applicationContext.resources.getString(R.string.downloading_referral)
                                    )
                                    val referralEntity = Referral()
                                    if (encounter.referral != null) {
                                        referralEntity.remote_id = encounter.referral!!.id
                                        referralEntity.encounter?.target = dbEncounterEntity
                                        referralEntity.no_referral = encounter.referral!!.no_referral
                                        referralEntity.health_post = encounter.referral!!.health_post
                                        referralEntity.hygienist = encounter.referral!!.hygienist
                                        referralEntity.dentist = encounter.referral!!.dentist
                                        referralEntity.general_physician =
                                            encounter.referral!!.general_physician
                                        referralEntity.other = encounter.referral!!.other
                                        try {
                                            referralEntity.other_details =
                                                encounter.referral!!.other_details
                                        }catch (e: Exception){
                                            referralEntity.other_details = ""
                                        }

                                        referralEntity.updated = false
                                        referralEntity.uploaded = true

                                    }
                                    referralsBox.put(referralEntity)
                                }
                            }
                            else {
                                var newReferral = Referral()
                                newReferral.encounter?.target = dbEncounterEntity
                                referralsBox.put(newReferral)
                            }
                        }
                    }
                }
            }
        }

        DentalApp.cancelNotification(applicationContext, 1001)
    }

}