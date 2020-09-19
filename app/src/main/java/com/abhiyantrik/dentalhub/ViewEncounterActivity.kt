package com.abhiyantrik.dentalhub

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.utils.DateHelper
import io.objectbox.Box

class ViewEncounterActivity : AppCompatActivity() {

    private var history = History()
    private var screening = Screening()
    private var treatment = Treatment()
    private var referral = Referral()
    private lateinit var encounter: Encounter
    private var recall = Recall()
    private lateinit var patient: Patient

    private lateinit var patientBox: Box<Patient>
    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>
    private lateinit var wardBox: Box<Ward>
    private lateinit var activityBox: Box<Activity>
    //private lateinit var recallBox: Box<Recall>

    // Geography and Activity

    private lateinit var tvActivity: TextView
    private lateinit var tvWardName: TextView

    // history

    // History Titles TextView
    private lateinit var tvBloodDisorderOrBleedingProblemTitle: TextView
    private lateinit var tvDiabetesTitle: TextView
    private lateinit var tvLiverProblemTitle: TextView
    private lateinit var tvRheumaticFeverTitle: TextView
    private lateinit var tvSeizuresOrEpilepsyTitle: TextView
    private lateinit var tvHepatitisBOrCTitle: TextView
    private lateinit var tvHIVTitle: TextView
    private lateinit var tvOtherTitle: TextView
    private lateinit var tvNoUnderlyingMedicalConditionTitle: TextView
    private lateinit var tvNotTakingAnyMedicationsTitle: TextView
    private lateinit var tvAllergiesTitle: TextView
    // Didn't saw the Medications information -----------

    // History Data TextView
    private lateinit var tvBloodDisorderOrBleedingProblem: TextView
    private lateinit var tvDiabetes: TextView
    private lateinit var tvLiverProblem: TextView
    private lateinit var tvRheumaticFever: TextView
    private lateinit var tvSeizuresOrEpilepsy: TextView
    private lateinit var tvHepatitisBOrC: TextView
    private lateinit var tvHIV: TextView
    private lateinit var tvOther: TextView
    private lateinit var tvNoUnderlyingMedicalCondition: TextView
    private lateinit var tvNotTakingAnyMedications: TextView
    private lateinit var tvAllergies: TextView

    // screening

    // Screening Titles TextView
    private lateinit var tvCarriesRiskTitle: TextView
    private lateinit var tvNoOfDecayedPrimaryTeethTitle: TextView
    private lateinit var tvNoOfDecayedPermanentTeethTitle: TextView
    private lateinit var tvCavityPermanentToothTitle: TextView
    private lateinit var tvCavityPermanentAnteriorTitle: TextView
    private lateinit var tvReversiblePulpitisTitle: TextView
    private lateinit var tvNeedARTFillingTitle: TextView
    private lateinit var tvNeedSealantTitle: TextView
    private lateinit var tvNeedSDFTitle: TextView
    private lateinit var tvNeedExtractionTitle: TextView
    private lateinit var tvActiveInfectionTitle: TextView
    private lateinit var tvLowBPTitle: TextView
    private lateinit var tvHighBPTitle: TextView
    private lateinit var tvThyroidDisorderTitle: TextView

    // Screening Data TextView
    private lateinit var tvCarriesRisk: TextView
    private lateinit var tvNoOfDecayedPrimaryTeeth: TextView
    private lateinit var tvNoOfDecayedPermanentTeeth: TextView
    private lateinit var tvCavityPermanentTooth: TextView
    private lateinit var tvCavityPermanentAnterior: TextView
    private lateinit var tvReversiblePulpitis: TextView
    private lateinit var tvNeedARTFilling: TextView
    private lateinit var tvNeedSealant: TextView
    private lateinit var tvNeedSDF: TextView
    private lateinit var tvNeedExtraction: TextView
    private lateinit var tvActiveInfection: TextView
    private lateinit var tvHighBP: TextView
    private lateinit var tvLowBP: TextView
    private lateinit var tvThyroidDisorder: TextView


    // treatment

    // Treatment Title TextView
    private lateinit var tvSDFWholeMouthTitle: TextView
    private lateinit var tvFVAppliedTitle: TextView
    private lateinit var tvTreatmentPlanCompleteTitle: TextView
    private lateinit var tvNotesTitle: TextView

    // Treatment Data TextView
    private lateinit var tvSDFWholeMouth: TextView
    private lateinit var tvFVApplied: TextView
    private lateinit var tvTreatmentPlanComplete: TextView
    private lateinit var tvNotes: TextView

    // Treatment Buttons

    // Treatment Buttons Row 1
    private lateinit var btn11: Button
    private lateinit var btn12: Button
    private lateinit var btn13: Button
    private lateinit var btn14: Button
    private lateinit var btn15: Button
    private lateinit var btn16: Button
    private lateinit var btn17: Button
    private lateinit var btn18: Button

    private lateinit var btn21: Button
    private lateinit var btn22: Button
    private lateinit var btn23: Button
    private lateinit var btn24: Button
    private lateinit var btn25: Button
    private lateinit var btn26: Button
    private lateinit var btn27: Button
    private lateinit var btn28: Button

    // Treatment Buttons Row 2
    private lateinit var btn51: Button
    private lateinit var btn52: Button
    private lateinit var btn53: Button
    private lateinit var btn54: Button
    private lateinit var btn55: Button

    private lateinit var btn61: Button
    private lateinit var btn62: Button
    private lateinit var btn63: Button
    private lateinit var btn64: Button
    private lateinit var btn65: Button

    // Treatment Buttons Row 3
    private lateinit var btn71: Button
    private lateinit var btn72: Button
    private lateinit var btn73: Button
    private lateinit var btn74: Button
    private lateinit var btn75: Button

    private lateinit var btn81: Button
    private lateinit var btn82: Button
    private lateinit var btn83: Button
    private lateinit var btn84: Button
    private lateinit var btn85: Button

    // Treatment Buttons Row 4
    private lateinit var btn41: Button
    private lateinit var btn42: Button
    private lateinit var btn43: Button
    private lateinit var btn44: Button
    private lateinit var btn45: Button
    private lateinit var btn46: Button
    private lateinit var btn47: Button
    private lateinit var btn48: Button

    private lateinit var btn31: Button
    private lateinit var btn32: Button
    private lateinit var btn33: Button
    private lateinit var btn34: Button
    private lateinit var btn35: Button
    private lateinit var btn36: Button
    private lateinit var btn37: Button
    private lateinit var btn38: Button

    // referral

    // Referral Title TextView
    private lateinit var tvNoReferralTitle: TextView
    private lateinit var tvHealthPostTitle: TextView
    private lateinit var tvHygienistTitle: TextView
    private lateinit var tvDentistTitle: TextView
    private lateinit var tvGeneralPhysicianTitle: TextView
    private lateinit var tvOtherDetailsTitle: TextView

    // Referral Data TextView
    private lateinit var tvNoReferral: TextView
    private lateinit var tvHealthPost: TextView
    private lateinit var tvHygienist: TextView
    private lateinit var tvDentist: TextView
    private lateinit var tvGeneralPhysician: TextView
    private lateinit var tvOtherDetails: TextView

    // recall
//    private lateinit var tvRecallDate: TextView
//    private lateinit var tvRecallTime: TextView
//    private lateinit var tvRecallGeography: TextView
//    private lateinit var tvRecallActivity: TextView

    private lateinit var context: Context

    var encounterId: Long = 0
    var patientId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_encounter)
        context = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        encounterId = intent.getLongExtra("ENCOUNTER_ID", 0)
        patientId = intent.getLongExtra("PATIENT_ID", 0)

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)
        wardBox = ObjectBox.boxStore.boxFor(Ward::class.java)
        activityBox = ObjectBox.boxStore.boxFor(Activity::class.java)
        //recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!
        patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!

        title = patient.fullName()

        history = historyBox.query().equal(History_.encounterId, encounter.id).build().findFirst()!!
        screening =
            screeningBox.query().equal(Screening_.encounterId, encounter.id).build().findFirst()!!
        treatment =
            treatmentBox.query().equal(Treatment_.encounterId, encounter.id).build().findFirst()!!
        referral =
            referralBox.query().equal(Referral_.encounterId, encounter.id).build().findFirst()!!
        //recall = recallBox.query().equal(Recall_.encounterId, encounter.id).build().findFirst()!!


        initUI()
    }

    private fun initUI() {

        // Geography and activity

        tvWardName = findViewById(R.id.tvWardName)
        tvActivity = findViewById(R.id.tvActivity)

        var wardID: Ward
        var activityID: Activity

        if (encounter.ward_id != 0){
            wardID = wardBox.query().equal(Ward_.remote_id, encounter.ward_id.toLong()).build().findFirst()!!
            tvWardName.text = wardID.name
        } else {
            tvWardName.text = DentalApp.ward_name
        }

        if (!(encounter.activityarea_id).isNullOrEmpty()){
            activityID = activityBox.query().equal(Activity_.remote_id, encounter.activityarea_id).build().findFirst()!!
            tvActivity.text = activityID.name
        } else {
            tvActivity.text = DentalApp.activity_name
        }

        // history

        // History Title TextView
        tvBloodDisorderOrBleedingProblemTitle =
            findViewById(R.id.tvBloodDisorderOrBleedingProblemTitle)
        tvDiabetesTitle = findViewById(R.id.tvDiabetesTitle)
        tvLiverProblemTitle = findViewById(R.id.tvLiverProblemTitle)
        tvRheumaticFeverTitle = findViewById(R.id.tvRheumaticFeverTitle)
        tvSeizuresOrEpilepsyTitle = findViewById(R.id.tvSeizuresOrEpilepsyTitle)
        tvHepatitisBOrCTitle = findViewById(R.id.tvHepatitisBOrCTitle)
        tvHIVTitle = findViewById(R.id.tvHIVTitle)
        tvOtherTitle = findViewById(R.id.tvOtherTitle)
        tvHighBPTitle = findViewById(R.id.tvHighBPTitle)
        tvLowBPTitle = findViewById(R.id.tvLowBPTitle)
        tvThyroidDisorderTitle = findViewById(R.id.tvThyroidDisorderTitle)
        tvNoUnderlyingMedicalConditionTitle = findViewById(R.id.tvNoUnderlyingMedicalConditionTitle)
        tvNotTakingAnyMedicationsTitle = findViewById(R.id.tvNotTakingAnyMedicationsTitle)
        tvAllergiesTitle = findViewById(R.id.tvAllergiesTitle)

        // History Data TextView
        tvBloodDisorderOrBleedingProblem = findViewById(R.id.tvBloodDisorderOrBleedingProblem)
        tvDiabetes = findViewById(R.id.tvDiabetes)
        tvLiverProblem = findViewById(R.id.tvLiverProblem)
        tvRheumaticFever = findViewById(R.id.tvRheumaticFever)
        tvSeizuresOrEpilepsy = findViewById(R.id.tvSeizuresOrEpilepsy)
        tvHepatitisBOrC = findViewById(R.id.tvHepatitisBOrC)
        tvHIV = findViewById(R.id.tvHIV)
        tvOther = findViewById(R.id.tvOther)
        tvHighBP = findViewById(R.id.tvHighBP)
        tvLowBP = findViewById(R.id.tvLowBP)
        tvThyroidDisorder = findViewById(R.id.tvThyroidDisorder)
        tvNoUnderlyingMedicalCondition = findViewById(R.id.tvNoUnderlyingMedicalCondition)
        tvNotTakingAnyMedications = findViewById(R.id.tvNotTakingAnyMedications)
        tvAllergies = findViewById(R.id.tvAllergies)

        hideBoolean(
            history.blood_disorder,
            tvBloodDisorderOrBleedingProblemTitle,
            tvBloodDisorderOrBleedingProblem
        )
        hideBoolean(history.diabetes, tvDiabetesTitle, tvDiabetes)
        hideBoolean(history.liver_problem, tvLiverProblemTitle, tvLiverProblem)
        hideBoolean(history.rheumatic_fever, tvRheumaticFeverTitle, tvRheumaticFever)
        hideBoolean(history.seizuers_or_epilepsy, tvSeizuresOrEpilepsyTitle, tvSeizuresOrEpilepsy)
        hideBoolean(history.hepatitis_b_or_c, tvHepatitisBOrCTitle, tvHepatitisBOrC)
        hideBoolean(history.hiv, tvHIVTitle, tvHIV)
        hideString(history.other, tvOtherTitle, tvOther)
        hideBoolean(history.high_blood_pressure, tvHighBPTitle, tvHighBP)
        hideBoolean(history.low_blood_pressure, tvLowBPTitle, tvLowBP)
        hideBoolean(history.thyroid_disorder, tvThyroidDisorderTitle, tvThyroidDisorder)
        hideBoolean(
            history.no_underlying_medical_condition,
            tvNoUnderlyingMedicalConditionTitle,
            tvNoUnderlyingMedicalCondition
        )

        // left to history.medicine Edit Text ------------

        if (history.not_taking_any_medications) {
            tvNotTakingAnyMedicationsTitle.text = "Not Taking Any Medications"
            tvNotTakingAnyMedications.text = "Yes"
        } else {
            tvNotTakingAnyMedications.text = history.medications
        }
        // hideBoolean(history.not_taking_any_medications, tvNotTakingAnyMedicationsTitle, tvNotTakingAnyMedications)
        // since allergies is little different i.e. if no_allergies is True show allergies(String) else don't show both no_allergies(Int) and allergies(String)
        if (history.no_allergies) {
            tvAllergiesTitle.text = "No Allergies"
            tvAllergies.text = "Yes"
        } else {
            tvAllergies.text = history.allergies
        }

        // screening

        // Screening Title TextView
        tvCarriesRiskTitle = findViewById(R.id.tvCarriesRiskTitle)
        tvNoOfDecayedPrimaryTeethTitle = findViewById(R.id.tvNoOfDecayedPrimaryTeethTitle)
        tvNoOfDecayedPermanentTeethTitle = findViewById(R.id.tvNoOfDecayedPermanentTeethTitle)
        tvCavityPermanentToothTitle = findViewById(R.id.tvCavityPermanentToothTitle)
        tvCavityPermanentAnteriorTitle = findViewById(R.id.tvCavityPermanentAnteriorTitle)
        tvReversiblePulpitisTitle = findViewById(R.id.tvReversiblePulpitisTitle)
        tvNeedARTFillingTitle = findViewById(R.id.tvNeedARTFillingTitle)
        tvNeedSealantTitle = findViewById(R.id.tvNeedSealantTitle)
        tvNeedSDFTitle = findViewById(R.id.tvNeedSDFTitle)
        tvNeedExtractionTitle = findViewById(R.id.tvNeedExtractionTitle)
        tvActiveInfectionTitle = findViewById(R.id.tvActiveInfectionTitle)

        // Screening Data TextView
        tvCarriesRisk = findViewById(R.id.tvCarriesRisk)
        tvNoOfDecayedPrimaryTeeth = findViewById(R.id.tvNoOfDecayedPrimaryTeeth)
        tvNoOfDecayedPermanentTeeth = findViewById(R.id.tvNoOfDecayedPermanentTeeth)
        tvCavityPermanentTooth = findViewById(R.id.tvCavityPermanentTooth)
        tvCavityPermanentAnterior = findViewById(R.id.tvCavityPermanentAnterior)
        tvReversiblePulpitis = findViewById(R.id.tvReversiblePulpitis)
        tvNeedARTFilling = findViewById(R.id.tvNeedARTFilling)
        tvNeedSealant = findViewById(R.id.tvNeedSealant)
        tvNeedSDF = findViewById(R.id.tvNeedSDF)
        tvNeedExtraction = findViewById(R.id.tvNeedExtraction)
        tvActiveInfection = findViewById(R.id.tvActiveInfection)

        // to hide if screening items are unchecked while adding encounter
        hideString(screening.carries_risk, tvCarriesRiskTitle, tvCarriesRisk)
        hideInt(
            screening.decayed_primary_teeth,
            tvNoOfDecayedPrimaryTeethTitle,
            tvNoOfDecayedPrimaryTeeth
        )
        hideInt(
            screening.decayed_permanent_teeth,
            tvNoOfDecayedPermanentTeethTitle,
            tvNoOfDecayedPermanentTeeth
        )
        hideBoolean(
            screening.cavity_permanent_posterior_teeth,
            tvCavityPermanentToothTitle,
            tvCavityPermanentTooth
        )
        hideBoolean(
            screening.cavity_permanent_anterior_teeth,
            tvCavityPermanentAnteriorTitle,
            tvCavityPermanentAnterior
        )
        hideBoolean(screening.reversible_pulpitis, tvReversiblePulpitisTitle, tvReversiblePulpitis)
        hideBoolean(screening.need_art_filling, tvNeedARTFillingTitle, tvNeedARTFilling)
        hideBoolean(screening.need_sealant, tvNeedSealantTitle, tvNeedSealant)
        hideBoolean(screening.need_sdf, tvNeedSDFTitle, tvNeedSDF)
        hideBoolean(screening.need_extraction, tvNeedExtractionTitle, tvNeedExtraction)
        hideBoolean(screening.active_infection, tvActiveInfectionTitle, tvActiveInfection)


        // treatment

        // Treatment Title TextView
        tvSDFWholeMouthTitle = findViewById(R.id.tvSDFWholeMouthTitle)
        tvFVAppliedTitle = findViewById(R.id.tvFVAppliedTitle)
        tvNotesTitle = findViewById(R.id.tvNotesTitle)
        tvTreatmentPlanCompleteTitle = findViewById(R.id.tvTreatmentPlanCompleteTitle)

        // Treatment Data TextView
        tvSDFWholeMouth = findViewById(R.id.tvSDFWholeMouth)
        tvFVApplied = findViewById(R.id.tvFVApplied)
        tvNotes = findViewById(R.id.tvNotes)
        tvTreatmentPlanComplete = findViewById(R.id.tvTreatmentPlanComplete)

        buttonInit()
        hideBoolean(treatment.sdf_whole_mouth, tvSDFWholeMouthTitle, tvSDFWholeMouth)
        hideBoolean(treatment.fv_applied, tvFVAppliedTitle, tvFVApplied)
        hideBoolean(
            treatment.treatment_plan_complete,
            tvTreatmentPlanCompleteTitle,
            tvTreatmentPlanComplete
        )
        hideString(treatment.notes, tvNotesTitle, tvNotes)

        // referral

        // Referral Title TextView
        tvNoReferralTitle = findViewById(R.id.tvNoReferralTitle)
        tvHealthPostTitle = findViewById(R.id.tvHealthPostTitle)
        tvHygienistTitle = findViewById(R.id.tvHygienistTitle)
        tvDentistTitle = findViewById(R.id.tvDentistTitle)
        tvGeneralPhysicianTitle = findViewById(R.id.tvGeneralPhysicianTitle)
        tvOtherDetailsTitle = findViewById(R.id.tvOtherDetailsTitle)

        // Referral Data TextView
        tvNoReferral = findViewById(R.id.tvNoReferral)
        tvHealthPost = findViewById(R.id.tvHealthPost)
        tvHygienist = findViewById(R.id.tvHygienist)
        tvDentist = findViewById(R.id.tvDentist)
        tvGeneralPhysician = findViewById(R.id.tvGeneralPhysician)
        tvOtherDetails = findViewById(R.id.tvOtherDetails)

        hideBoolean(referral.no_referral, tvNoReferralTitle, tvNoReferral)
        hideBoolean(referral.health_post, tvHealthPostTitle, tvHealthPost)
        hideBoolean(referral.hygienist, tvHygienistTitle, tvHygienist)
        hideBoolean(referral.dentist, tvDentistTitle, tvDentist)
        hideBoolean(referral.general_physician, tvGeneralPhysicianTitle, tvGeneralPhysician)
        hideString(referral.other_details, tvOtherDetailsTitle, tvOtherDetails)

        // schedule

        // Schedule Title TextView
//        tvRecallDate = findViewById(R.id.tvRecallDate)
//        tvRecallTime = findViewById(R.id.tvRecallTime)
//        tvRecallActivity = findViewById(R.id.tvRecallActivity)
//        tvRecallGeography = findViewById(R.id.tvRecallGeography)

        // Schedule Data TextView
//        tvRecallDate.text = recall.date
//        tvRecallTime.text = recall.time
//        tvRecallActivity.text = recall.activity
//        tvRecallGeography.text = recall.geography

    }

    private fun buttonInit() {

        // Treatment Buttons

        // Treatment Buttons Row 1
        btn11 = findViewById(R.id.btnId11)
        btn12 = findViewById(R.id.btnId12)
        btn13 = findViewById(R.id.btnId13)
        btn14 = findViewById(R.id.btnId14)
        btn15 = findViewById(R.id.btnId15)
        btn16 = findViewById(R.id.btnId16)
        btn17 = findViewById(R.id.btnId17)
        btn18 = findViewById(R.id.btnId18)

        btn21 = findViewById(R.id.btnId21)
        btn22 = findViewById(R.id.btnId22)
        btn23 = findViewById(R.id.btnId23)
        btn24 = findViewById(R.id.btnId24)
        btn25 = findViewById(R.id.btnId25)
        btn26 = findViewById(R.id.btnId26)
        btn27 = findViewById(R.id.btnId27)
        btn28 = findViewById(R.id.btnId28)


        // Treatment Buttons Row 2
        btn51 = findViewById(R.id.btnId51)
        btn52 = findViewById(R.id.btnId52)
        btn53 = findViewById(R.id.btnId53)
        btn54 = findViewById(R.id.btnId54)
        btn55 = findViewById(R.id.btnId55)

        btn61 = findViewById(R.id.btnId61)
        btn62 = findViewById(R.id.btnId62)
        btn63 = findViewById(R.id.btnId63)
        btn64 = findViewById(R.id.btnId64)
        btn65 = findViewById(R.id.btnId65)

        // Treatment Buttons Row 3
        btn71 = findViewById(R.id.btnId71)
        btn72 = findViewById(R.id.btnId72)
        btn73 = findViewById(R.id.btnId73)
        btn74 = findViewById(R.id.btnId74)
        btn75 = findViewById(R.id.btnId75)

        btn81 = findViewById(R.id.btnId81)
        btn82 = findViewById(R.id.btnId82)
        btn83 = findViewById(R.id.btnId83)
        btn84 = findViewById(R.id.btnId84)
        btn85 = findViewById(R.id.btnId85)

        // Treatment Buttons Row 4
        btn41 = findViewById(R.id.btnId41)
        btn42 = findViewById(R.id.btnId42)
        btn43 = findViewById(R.id.btnId43)
        btn44 = findViewById(R.id.btnId44)
        btn45 = findViewById(R.id.btnId45)
        btn46 = findViewById(R.id.btnId46)
        btn47 = findViewById(R.id.btnId47)
        btn48 = findViewById(R.id.btnId48)

        btn31 = findViewById(R.id.btnId31)
        btn32 = findViewById(R.id.btnId32)
        btn33 = findViewById(R.id.btnId33)
        btn34 = findViewById(R.id.btnId34)
        btn35 = findViewById(R.id.btnId35)
        btn36 = findViewById(R.id.btnId36)
        btn37 = findViewById(R.id.btnId37)
        btn38 = findViewById(R.id.btnId38)

        // Fill the color of each button
        // Treatment Buttons Row 1
        setButtonColor(btn11, treatment.tooth11)
        setButtonColor(btn12, treatment.tooth12)
        setButtonColor(btn13, treatment.tooth13)
        setButtonColor(btn14, treatment.tooth14)
        setButtonColor(btn15, treatment.tooth15)
        setButtonColor(btn16, treatment.tooth16)
        setButtonColor(btn17, treatment.tooth17)
        setButtonColor(btn18, treatment.tooth18)

        setButtonColor(btn21, treatment.tooth21)
        setButtonColor(btn22, treatment.tooth22)
        setButtonColor(btn23, treatment.tooth23)
        setButtonColor(btn24, treatment.tooth24)
        setButtonColor(btn25, treatment.tooth25)
        setButtonColor(btn26, treatment.tooth26)
        setButtonColor(btn27, treatment.tooth27)
        setButtonColor(btn28, treatment.tooth28)

        // Treatment Buttons Row 2
        setButtonColor(btn51, treatment.tooth51)
        setButtonColor(btn52, treatment.tooth52)
        setButtonColor(btn53, treatment.tooth53)
        setButtonColor(btn54, treatment.tooth54)
        setButtonColor(btn55, treatment.tooth55)

        setButtonColor(btn61, treatment.tooth61)
        setButtonColor(btn62, treatment.tooth62)
        setButtonColor(btn63, treatment.tooth63)
        setButtonColor(btn64, treatment.tooth64)
        setButtonColor(btn65, treatment.tooth65)

        // Treatment Buttons Row 3
        setButtonColor(btn71, treatment.tooth71)
        setButtonColor(btn72, treatment.tooth72)
        setButtonColor(btn73, treatment.tooth73)
        setButtonColor(btn74, treatment.tooth74)
        setButtonColor(btn75, treatment.tooth75)

        setButtonColor(btn81, treatment.tooth81)
        setButtonColor(btn82, treatment.tooth82)
        setButtonColor(btn83, treatment.tooth83)
        setButtonColor(btn84, treatment.tooth84)
        setButtonColor(btn85, treatment.tooth85)

        // Treatment Buttons Row 4
        setButtonColor(btn31, treatment.tooth31)
        setButtonColor(btn32, treatment.tooth32)
        setButtonColor(btn33, treatment.tooth33)
        setButtonColor(btn34, treatment.tooth34)
        setButtonColor(btn35, treatment.tooth35)
        setButtonColor(btn36, treatment.tooth36)
        setButtonColor(btn37, treatment.tooth37)
        setButtonColor(btn38, treatment.tooth38)

        setButtonColor(btn41, treatment.tooth41)
        setButtonColor(btn42, treatment.tooth42)
        setButtonColor(btn43, treatment.tooth43)
        setButtonColor(btn44, treatment.tooth44)
        setButtonColor(btn45, treatment.tooth45)
        setButtonColor(btn46, treatment.tooth46)
        setButtonColor(btn47, treatment.tooth47)
        setButtonColor(btn48, treatment.tooth48)

    }

    private fun setButtonColor(button: Button, treatmentType: String) {
        when (treatmentType) {
            "SDF" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf_applied, null)
                button.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.treatment_button_onselect_text,
                        null
                    )
                )
            }
            "SEAL" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal_applied, null)
                button.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.treatment_button_onselect_text,
                        null
                    )
                )
            }
            "ART" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_art_applied, null)
                button.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.treatment_button_onselect_text,
                        null
                    )
                )
            }
            "EXO" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo_applied, null)
                button.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.treatment_button_onselect_text,
                        null
                    )
                )
            }
            "UNTR" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr_applied, null)
                button.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.treatment_button_onselect_text,
                        null
                    )
                )
            }
            "SMART" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_smart_applied, null)
                button.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.treatment_button_onselect_text,
                        null
                    )
                )
            }
        }
    }

    /* To show or hide the fields with Boolean data in the
    Fragements -> History, Screening, Treatment, Referral and Recall */
    private fun hideBoolean(disease: Boolean, viewTitle: View, view: TextView) {
        if (disease) {
            view.text = "Yes"
        } else {
            viewTitle.visibility = View.GONE
            view.visibility = View.GONE
        }
    }

    /*To show or hide the fields with String data in the
    Fragements -> History, Screening, Treatment, Referral and Recall*/
    private fun hideString(disease: String, viewTitle: View, view: TextView) {
        if (!disease.isNullOrBlank()) {
            view.text = disease
        } else {
            viewTitle.visibility = View.GONE
            view.visibility = View.GONE
        }
    }

    /*
        To show or hide the fields with Integer data in the
        Fragments -> History, Screening, Treatment, Referral and Recall
    */
    private fun hideInt(disease: Int, viewTitle: View, view: TextView) {
        if (disease != 0) {
            view.text = disease.toString()
        } else {
            viewTitle.visibility = View.GONE
            view.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_patient_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewPatient -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                val inflate: LayoutInflater = layoutInflater
                val view: View = inflate.inflate(R.layout.popup_view_patient, null)

                // to get all id of the textView
                val tvFirstNameView = view.findViewById<TextView>(R.id.tvFirstNameView)
                val tvMiddleNameView = view.findViewById<TextView>(R.id.tvMiddleNameView)
                val tvLastNameView = view.findViewById<TextView>(R.id.tvLastNameView)
                val tvGenderpopupView = view.findViewById<TextView>(R.id.tvGenderpopupView)
                val tvDateofBirthView = view.findViewById<TextView>(R.id.tvDateofBirthView)
                val tvPhonepopupView = view.findViewById<TextView>(R.id.tvPhonepopupView)
                val tvWardView = view.findViewById<TextView>(R.id.tvWardView)
                val tvMunicipalityView = view.findViewById<TextView>(R.id.tvMunicipalityView)
                val tvDistrictView = view.findViewById<TextView>(R.id.tvDistrictView)
                val tvEducationLevelView = view.findViewById<TextView>(R.id.tvEducationLevelView)
                val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnCloseDialog)

                // to set the details of the patient on Alert Dialog i.e. View Patient
                tvFirstNameView.text = patient.first_name
                tvMiddleNameView.text = patient.middle_name
                tvLastNameView.text = patient.last_name
                tvGenderpopupView.text = patient.gender.capitalize()
                tvDateofBirthView.text = DateHelper.formatNepaliDate(context, patient.dob)
                tvPhonepopupView.text = patient.phone
                tvWardView.text = patient.wardNumber()
                tvMunicipalityView.text = patient.municipalityName()
                tvDistrictView.text = patient.districtName()
                tvEducationLevelView.text = patient.education.capitalize()


                builder.setView(view)
                val dialog: Dialog = builder.create()
                dialog.show()

                btnCloseDialog.setOnClickListener {
                    dialog.dismiss()
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

}