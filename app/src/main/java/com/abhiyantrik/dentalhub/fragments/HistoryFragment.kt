package com.abhiyantrik.dentalhub.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.TreatmentFragmentCommunicator
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.History
import com.abhiyantrik.dentalhub.entities.History_
import com.abhiyantrik.dentalhub.fragments.interfaces.HistoryFormCommunicator
import io.objectbox.Box
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var historyFormCommunicator: HistoryFormCommunicator

    private lateinit var encounterBox: Box<Encounter>
    private var encounter = Encounter()
    private lateinit var historyBox: Box<History>
    private var history = History()

    private lateinit var checkBoxBloodDisorderOrBleedingProblem: CheckBox
    private lateinit var checkBoxDiabetes: CheckBox
    private lateinit var checkBoxLiverProblem: CheckBox
    private lateinit var checkBoxRheumaticFever: CheckBox
    private lateinit var checkBoxSeizuresOrEpilepsy: CheckBox
    private lateinit var checkBoxHepatitisBOrC: CheckBox
    private lateinit var checkBoxHIV: CheckBox
    private lateinit var checkBoxNoUnderlyingMedicalCondition: CheckBox
    private lateinit var checkBoxNotTakingAnyMedications: CheckBox
    private lateinit var checkBoxNoAllergies: CheckBox

    private lateinit var checkBoxLowBP: CheckBox
    private lateinit var checkBoxHighBP: CheckBox
    private lateinit var checkBoxThyroidDisorder: CheckBox

    private lateinit var etOther: EditText
    private lateinit var etMedications: EditText
    private lateinit var etAllergies: EditText

    private lateinit var btnNext: Button
    private lateinit var btnSave: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)

        checkBoxBloodDisorderOrBleedingProblem =
            view.findViewById(R.id.checkBoxBloodDisorderOrBleedingProblem)
        checkBoxDiabetes = view.findViewById(R.id.checkBoxDiabetes)
        checkBoxLiverProblem = view.findViewById(R.id.checkBoxLiverProblem)
        checkBoxRheumaticFever = view.findViewById(R.id.checkBoxRheumaticFever)
        checkBoxSeizuresOrEpilepsy = view.findViewById(R.id.checkBoxSeizuresOrEpilepsy)
        checkBoxHepatitisBOrC = view.findViewById(R.id.checkBoxHepatitisBOrC)
        checkBoxHIV = view.findViewById(R.id.checkBoxHIV)
        checkBoxNoUnderlyingMedicalCondition =
            view.findViewById(R.id.checkBoxNoUnderlyingMedicalCondition)
        checkBoxNotTakingAnyMedications = view.findViewById(R.id.checkBoxNotTakingAnyMedications)
        checkBoxNoAllergies = view.findViewById(R.id.checkBoxNoAllergies)

        checkBoxLowBP = view.findViewById(R.id.checkBoxLowBP)
        checkBoxHighBP = view.findViewById(R.id.checkBoxHighBP)
        checkBoxThyroidDisorder = view.findViewById(R.id.checkBoxThyroidDisorder)

        etOther = view.findViewById(R.id.etOther)
        etMedications = view.findViewById(R.id.etMedications)
        etAllergies = view.findViewById(R.id.etAllergies)

        btnSave = view.findViewById(R.id.btnSave)
        btnNext = view.findViewById(R.id.btnNext)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        historyFormCommunicator = activity as HistoryFormCommunicator

        setupUI(activity as Context)

        uncheckNoUnderlyingMedicalCon(checkBoxBloodDisorderOrBleedingProblem)
        uncheckNoUnderlyingMedicalCon(checkBoxDiabetes)
        uncheckNoUnderlyingMedicalCon(checkBoxLiverProblem)
        uncheckNoUnderlyingMedicalCon(checkBoxRheumaticFever)
        uncheckNoUnderlyingMedicalCon(checkBoxSeizuresOrEpilepsy)
        uncheckNoUnderlyingMedicalCon(checkBoxHepatitisBOrC)
        uncheckNoUnderlyingMedicalCon(checkBoxHIV)
        uncheckNoUnderlyingMedicalCon(checkBoxHighBP)
        uncheckNoUnderlyingMedicalCon(checkBoxLowBP)
        uncheckNoUnderlyingMedicalCon(checkBoxThyroidDisorder)

        checkBoxNoUnderlyingMedicalCondition.setOnCheckedChangeListener { compoundButton, _ ->
            etOther.setText("")
            if (compoundButton.isChecked) {
                checkBoxBloodDisorderOrBleedingProblem.isChecked = false
                checkBoxDiabetes.isChecked = false
                checkBoxLiverProblem.isChecked = false
                checkBoxRheumaticFever.isChecked = false
                checkBoxSeizuresOrEpilepsy.isChecked = false
                checkBoxHepatitisBOrC.isChecked = false
                checkBoxHIV.isChecked = false
                checkBoxHighBP.isChecked = false
                checkBoxLowBP.isChecked = false
                checkBoxThyroidDisorder.isChecked = false
                etOther.visibility = View.GONE
                tvOther.visibility = View.GONE
            } else {
                etOther.visibility = View.VISIBLE
                tvOther.visibility = View.VISIBLE
            }
        }

        checkBoxNotTakingAnyMedications.setOnCheckedChangeListener { compoundButton, _ ->
            etMedications.setText("")
            if (!compoundButton.isChecked) {
                etMedications.visibility = View.VISIBLE
                tvMedications.visibility = View.VISIBLE
            } else {
                etMedications.visibility = View.GONE
                tvMedications.visibility = View.GONE
            }
        }

        checkBoxNoAllergies.setOnCheckedChangeListener { compoundButton, _ ->
            etAllergies.setText("")
            if (!compoundButton.isChecked) {
                etAllergies.visibility = View.VISIBLE
                tvAllergies.visibility = View.VISIBLE
            } else {
                etAllergies.visibility = View.GONE
                tvAllergies.visibility = View.GONE
            }
        }

        checkBoxHighBP.setOnCheckedChangeListener { compoundButton, b ->
            if (checkBoxNoUnderlyingMedicalCondition.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    activity,
                    "Please uncheck the Not underlying medical condition.",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(b && checkBoxLowBP.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    context,
                    resources.getString(R.string.low_bp_is_checked),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        checkBoxLowBP.setOnCheckedChangeListener { compoundButton, b ->
            if (checkBoxNoUnderlyingMedicalCondition.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    activity,
                    "Please uncheck the Not underlying medical condition.",
                    Toast.LENGTH_SHORT
                ).show()
            }else if(b && checkBoxHighBP.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    context,
                    resources.getString(R.string.high_bp_is_checked),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnNext.setOnClickListener {
            val view = this.view!!.rootView
            if (view != null) {
                val imm = (activity as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
//          to check the validation conditions
            if (historyValidate() == false) {
                Log.d("HistoryFragment", "History fragment is invalid.")
            } else {
                saveHistoryData()
                fragmentCommunicator.goForward()
            }
        }
        btnSave.setOnClickListener {
            val view = this.view!!.rootView
            if (view != null) {
                val imm = (activity as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
//          to check the validation conditions
            if (historyValidate() == false) {
                Log.d("HistoryFragment", "History fragment is invalid.")
            } else {
                Log.d("HistoryFragment", "History fragment is valid.")
                saveHistoryData()
                fragmentCommunicator.goBack()
            }
        }
    }

    private fun saveHistoryData() {

        val bloodDisorders = checkBoxBloodDisorderOrBleedingProblem.isChecked
        val diabetes = checkBoxDiabetes.isChecked
        val liverProblem = checkBoxLiverProblem.isChecked
        val rheumaticFever = checkBoxRheumaticFever.isChecked
        val seizuresOrEpilepsy = checkBoxSeizuresOrEpilepsy.isChecked
        val hepatitisBOrC = checkBoxHepatitisBOrC.isChecked
        val hiv = checkBoxHIV.isChecked
        val other = etOther.text.toString()
        val noUnderlyingMedicalCondition = checkBoxNoUnderlyingMedicalCondition.isChecked
        val medications = etMedications.text.toString()
        val notTakingAnyMedications = checkBoxNotTakingAnyMedications.isChecked
        val noAllergies = checkBoxNoAllergies.isChecked
        val allergies = etAllergies.text.toString()

        val highBP = checkBoxHighBP.isChecked
        val lowBP = checkBoxLowBP.isChecked
        val thyroidDisorder = checkBoxThyroidDisorder.isChecked

        historyFormCommunicator.updateHistory(
            bloodDisorders,
            diabetes,
            liverProblem,
            rheumaticFever,
            seizuresOrEpilepsy,
            hepatitisBOrC,
            hiv,
            other,
            highBP,
            lowBP,
            thyroidDisorder,
            noUnderlyingMedicalCondition,
            medications,
            notTakingAnyMedications,
            noAllergies,
            allergies
        )
    }

    private fun historyValidate(): Boolean {
//        for no underlying medical condition
        if (checkBoxNoUnderlyingMedicalCondition.isChecked == false && (checkBoxDiabetes.isChecked == false &&
                    checkBoxLiverProblem.isChecked == false && checkBoxRheumaticFever.isChecked == false &&
                    checkBoxSeizuresOrEpilepsy.isChecked == false && checkBoxHepatitisBOrC.isChecked == false &&
                    checkBoxHIV.isChecked == false && checkBoxHighBP.isChecked == false &&
                    checkBoxLowBP.isChecked == false && checkBoxThyroidDisorder.isChecked == false) &&
            etOther.text.toString().trim().length < 1) {
            Log.d("HistoryFragment()", "Checkbox of not underlying medical condition is not checked.")
            Toast.makeText(context, "Fill underlying medical condition details.", Toast.LENGTH_SHORT).show()
            return false
        }
//        for not taking any medications
        if (checkBoxNotTakingAnyMedications.isChecked == false && etMedications.text.toString().trim().length < 1) {
            Log.d("HistoryFragment()", "Checkbox of medicine is not checked.")
            Toast.makeText(context, "Fill medication details.", Toast.LENGTH_SHORT).show()
            return false
        }
//        for no allergies
        if (checkBoxNoAllergies.isChecked == false && etAllergies.text.toString().trim().length < 1) {
            Log.d("HistoryFragment()", "Checkbox of allergies is not checked.")
            Toast.makeText(context, "Fill allergies details.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun uncheckNoUnderlyingMedicalCon(checkbox: CheckBox) {
        checkbox.setOnCheckedChangeListener { compoundButton, _ ->
            if (checkBoxNoUnderlyingMedicalCondition.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    activity,
                    "Please uncheck the Not underlying medical condition.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupUI(applicationContext: Context) {
        val encounterId =
            DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()

        if (encounterId != 0.toLong()) {
            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

            history =
                historyBox.query().equal(
                    History_.encounterId,
                    encounter.id
                ).orderDesc(History_.id).build().findFirst()!!

            if (history.blood_disorder) checkBoxBloodDisorderOrBleedingProblem.isChecked = true
            if (history.diabetes) checkBoxDiabetes.isChecked = true
            if (history.liver_problem) checkBoxLiverProblem.isChecked = true
            if (history.rheumatic_fever) checkBoxRheumaticFever.isChecked = true
            if (history.seizuers_or_epilepsy) checkBoxSeizuresOrEpilepsy.isChecked = true
            if (history.hepatitis_b_or_c) checkBoxHepatitisBOrC.isChecked = true
            if (history.hiv) checkBoxHIV.isChecked = true
            etOther.setText(history.other)

            if (history.low_blood_pressure) checkBoxLowBP.isChecked = true
            if (history.high_blood_pressure) checkBoxHighBP.isChecked = true
            if (history.thyroid_disorder) checkBoxThyroidDisorder.isChecked = true

            if (history.no_underlying_medical_condition){
                checkBoxNoUnderlyingMedicalCondition.isChecked = true
                etOther.visibility = View.GONE
                tvOther.visibility = View.GONE
            }else{
                etOther.setText(history.other)
            }
            etMedications.setText(history.medications)
            if (history.not_taking_any_medications) checkBoxNotTakingAnyMedications.isChecked = true

            if (history.not_taking_any_medications) {
                checkBoxNotTakingAnyMedications.isChecked = true
                etMedications.visibility = View.GONE
                tvMedications.visibility = View.GONE
            } else {
                etMedications.setText(history.medications)
            }
            if (history.no_allergies) {
                checkBoxNoAllergies.isChecked = true
                etAllergies.visibility = View.GONE
                tvAllergies.visibility = View.GONE
            } else {
                etAllergies.setText(history.allergies)
            }
        } else {
            history = historyBox.query().orderDesc(History_.id).build().findFirst()!!
        }
    }
}
