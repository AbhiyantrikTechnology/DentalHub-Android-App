package com.abhiyantrik.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.Encounter
import com.abhiyantrik.dentalhub.models.Patient
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.workers.DownloadPatientWorker
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SyncDownloadService : Service() {

    private val TAG = "SyncDownloadService"
    private lateinit var patientsBox: Box<com.abhiyantrik.dentalhub.entities.Patient>
    private lateinit var encountersBox: Box<com.abhiyantrik.dentalhub.entities.Encounter>
    private lateinit var historyBox: Box<com.abhiyantrik.dentalhub.entities.History>
    private lateinit var screeningBox: Box<com.abhiyantrik.dentalhub.entities.Screening>
    private lateinit var treatmentsBox: Box<com.abhiyantrik.dentalhub.entities.Treatment>
    private lateinit var referralsBox: Box<com.abhiyantrik.dentalhub.entities.Referral>

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        patientsBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Patient::class.java)
        encountersBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Encounter::class.java)
        historyBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.History::class.java)
        treatmentsBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Treatment::class.java)
        referralsBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Referral::class.java)
        screeningBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Screening::class.java)

        DentalApp.downloadSyncRunning = true

        val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<DownloadPatientWorker>()
            .setInitialDelay(100, TimeUnit.MILLISECONDS)
            .setConstraints(DentalApp.downloadConstraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(uploadPatientWorkRequest)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        DentalApp.downloadSyncRunning = false
    }

    private fun loadPatientData() {
        //tvMessage.text = tvMessage.text.toString() + "Loading patients...\n"
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.getPatients("JWT $token")
        call.enqueue(object : Callback<List<Patient>> {
            override fun onFailure(call: Call<List<Patient>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                //tvMessage.text = tvMessage.text.toString() + "Failed to load patients\n"
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<List<Patient>>, response: Response<List<Patient>>) {
                if (null != response.body()) {
                    Log.d("SetupActivity", response.code().toString())
                    when (response.code()) {
                        200 -> {
                            val allPatients = response.body() as List<Patient>
                            for (patient in allPatients) {
                                val existingPatient = patientsBox.query().equal(
                                    Patient_.remote_id,
                                    patient.id
                                ).build().findFirst()
                                if (existingPatient != null) {
                                    Log.d(
                                        "SyncDownloadService",
                                        existingPatient.fullName() + " already exists."
                                    )
                                    //tvMessage.text = tvMessage.text.toString() + existingPatient.fullName()+" already exists.\n"
                                    loadEncounterData(existingPatient.remote_id)
                                    DentalApp.displayNotification(
                                        applicationContext,
                                        1001,
                                        applicationContext.resources.getString(R.string.sync_ticker),
                                        existingPatient.fullName(),
                                        applicationContext.resources.getString(R.string.already_exists)
                                    )
                                } else {
                                    val patientEntity = Patient()
                                    patientEntity.remote_id = patient.id
                                    patientEntity.first_name = patient.first_name
                                    patientEntity.middle_name = patient.middle_name!!
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
                                    patientEntity.uploaded = true
                                    patientEntity.updated = false
                                    patientEntity.recall = null
                                    patientEntity.author = patient.author

                                    if (patient.created_at == null) {
                                        patientEntity.created_at = DateHelper.getCurrentNepaliDate()
                                    } else {
                                        patientEntity.created_at = patient.created_at
                                    }
                                    if (patient.updated_at == null) {
                                        patientEntity.updated_at = DateHelper.getCurrentNepaliDate()
                                    } else {
                                        patientEntity.updated_at = patient.updated_at
                                    }
                                    patientEntity.updated_by = patient.updated_by

                                    patientsBox.put(patientEntity)
                                    DentalApp.displayNotification(
                                        applicationContext,
                                        1001,
                                        applicationContext.resources.getString(R.string.sync_ticker),
                                        patient.fullName(),
                                        applicationContext.resources.getString(R.string.downloading_patient_detail)
                                    )
                                    loadEncounterData(patient.id)
                                    //tvMessage.text = tvMessage.text.toString() + patient.fullName()+" downloaded.\n"
                                }

                            }
                        }
                    }
                } else {
                    Log.d(TAG, response.code().toString())
                }
            }

        })

    }

    fun loadEncounterData(patientId: String) {
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.getEncounter("JWT $token", patientId)
        call.enqueue(object : Callback<List<Encounter>> {
            override fun onFailure(call: Call<List<Encounter>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                Log.d(TAG, t.toString())
            }

            override fun onResponse(
                call: Call<List<Encounter>>,
                response: Response<List<Encounter>>
            ) {

                if (null != response.body()) {
                    val dbPatientEntity =
                        patientsBox.query().equal(Patient_.id, patientId).build().findFirst()
                    Log.d("SyncDownloadService", response.code().toString())
                    when (response.code()) {
                        200 -> {
                            val allEncounters = response.body() as List<Encounter>
                            for (encounter in allEncounters) {
                                if (encountersBox.query().equal(
                                        Encounter_.remote_id,
                                        encounter.id
                                    ).build().find().size > 0
                                ) {
                                    Log.d("SyncDownloadService", "Encounter already downloaded.")
                                } else {
                                    val encounterEntity =
                                        com.abhiyantrik.dentalhub.entities.Encounter()
                                    encounterEntity.encounter_type = encounter.encounter_type
                                    encounterEntity.created_at = encounter.created_at
                                    encounterEntity.updated_at = encounter.updated_at!!
                                    encounterEntity.other_problem = encounter.other_problem!!
                                    encounterEntity.remote_id = encounter.id
                                    encounterEntity.patient?.target = dbPatientEntity
                                    encounterEntity.uploaded = true
                                    encountersBox.put(encounterEntity)

                                    val dbEncounterEntity =
                                        encountersBox.query().orderDesc(Encounter_.id).build()
                                            .findFirst()

                                    // save history
                                    if (historyBox.query().equal(
                                            History_.remote_id,
                                            encounter.history!!.id
                                        ).build().find().size > 0
                                    ) {
                                        Log.d("History", "Already downloaded")
                                    } else {
                                        val historyEntity = History()
                                        if (encounter.history != null) {
                                            historyEntity.remote_id =
                                                encounter.history!!.id
                                            historyEntity.encounter?.target = dbEncounterEntity
                                            historyEntity.blood_disorder =
                                                encounter.history!!.blood_disorder
                                            historyEntity.diabetes = encounter.history!!.diabetes
                                            historyEntity.liver_problem =
                                                encounter.history!!.liver_problem
                                            historyEntity.rheumatic_fever =
                                                encounter.history!!.rheumatic_fever
                                            historyEntity.seizuers_or_epilepsy =
                                                encounter.history!!.seizuers_or_epilepsy
                                            historyEntity.hepatitis_b_or_c =
                                                encounter.history!!.hepatitis_b_or_c
                                            historyEntity.hiv = encounter.history!!.hiv
                                            historyEntity.other = encounter.history!!.other
                                            historyEntity.high_blood_pressure =
                                                encounter.history!!.high_blood_pressure
                                            historyEntity.low_blood_pressure =
                                                encounter.history!!.low_blood_pressure
                                            historyEntity.thyroid_disorder =
                                                encounter.history!!.thyroid_disorder
                                            historyEntity.no_underlying_medical_condition =
                                                encounter.history!!.no_underlying_medical_condition
                                            historyEntity.medications =
                                                encounter.history!!.medications
                                            historyEntity.not_taking_any_medications =
                                                encounter.history!!.not_taking_any_medications
                                            historyEntity.no_allergies =
                                                encounter.history!!.no_allergies
                                            historyEntity.allergies = encounter.history!!.allergies

                                        }
                                        historyBox.put(historyEntity)
                                    }


                                    // save screening
                                    if (screeningBox.query().equal(
                                            Screening_.remote_id,
                                            encounter.screening!!.id
                                        ).build().find().size > 0
                                    ) {
                                        Log.d("Screening", "Already downloaded")
                                    } else {
                                        val screeningEntity = Screening()
                                        if (encounter.screening != null) {
                                            screeningEntity.remote_id =
                                                encounter.screening!!.id
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
                                            screeningEntity.need_sdf =
                                                encounter.screening!!.need_sdf
                                            screeningEntity.need_extraction =
                                                encounter.screening!!.need_extraction
                                            screeningEntity.active_infection =
                                                encounter.screening!!.active_infection

                                        }
                                        screeningBox.put(screeningEntity)
                                    }


                                    // save treatment
                                    if (treatmentsBox.query().equal(
                                            Treatment_.remote_id,
                                            encounter.treatment!!.id
                                        ).build().find().size > 0
                                    ) {
                                        Log.d("Screening", "Already downloaded")
                                    } else {
                                        val treatmentEntity = Treatment()
                                        if (encounter.treatment != null) {
                                            treatmentEntity.remote_id =
                                                encounter.treatment!!.id
                                            treatmentEntity.encounter?.target = dbEncounterEntity
                                            treatmentEntity.sdf_whole_mouth =
                                                encounter.treatment!!.sdf_whole_mouth
                                            treatmentEntity.fv_applied =
                                                encounter.treatment!!.fv_applied
                                            treatmentEntity.treatment_plan_complete =
                                                encounter.treatment!!.treatment_plan_complete
                                            treatmentEntity.notes = encounter.treatment!!.notes

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

                                        }
                                        treatmentsBox.put(treatmentEntity)
                                    }


                                    // save referral
                                    if (referralsBox.query().equal(
                                            Referral_.remote_id,
                                            encounter.treatment!!.id
                                        ).build().find().size > 0
                                    ) {
                                        Log.d("Screening", "Already downloaded")
                                    } else {
                                        val referralEntity = Referral()
                                        if (encounter.referral != null) {
                                            referralEntity.remote_id =
                                                encounter.referral!!.id
                                            referralEntity.encounter?.target = dbEncounterEntity
                                            referralEntity.no_referral =
                                                encounter.referral!!.no_referral
                                            referralEntity.health_post =
                                                encounter.referral!!.health_post
                                            referralEntity.hygienist =
                                                encounter.referral!!.hygienist
                                            referralEntity.dentist = encounter.referral!!.dentist
                                            referralEntity.general_physician =
                                                encounter.referral!!.general_physician
                                            referralEntity.other = encounter.referral!!.other
                                            referralEntity.other_details =
                                                encounter.referral!!.other_details

                                        }
                                        // ToDo Here
                                        referralsBox.put(referralEntity)
                                    }

                                }


                            }
                        }
                        else -> {
                            Log.d(TAG, response.code().toString())
                        }
                    }
                }
            }

        })
    }

}