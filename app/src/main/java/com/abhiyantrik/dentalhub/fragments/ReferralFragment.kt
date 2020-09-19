package com.abhiyantrik.dentalhub.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.abhiyantrik.dentalhub.*
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.fragments.interfaces.ReferralFormCommunicator
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.hornet.dateconverter.DateConverter
import io.objectbox.Box
import kotlinx.android.synthetic.main.fragment_referral.*
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*


class ReferralFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var referralFormCommunicator: ReferralFormCommunicator

    private lateinit var encounterBox: Box<Encounter>
    private var encounter = Encounter()
    private lateinit var referralBox: Box<Referral>
    private var referral = Referral()

    private lateinit var checkboxNoReferral : CheckBox
    private lateinit var checkboxHealthPost : CheckBox
    private lateinit var checkboxHygienist : CheckBox
    private lateinit var checkboxDentist : CheckBox
    private lateinit var checkboxGeneralPhysician : CheckBox
    private lateinit var checkboxOther : CheckBox

    private lateinit var tvRecallDateReferral: TextView
    private lateinit var rgRecalls: RadioGroup
    private lateinit var etOtherDetails: EditText

    private lateinit var etRecallDate: EditText
    private lateinit var etRecallTime: EditText

    private var recallDateOriginal = DentalApp.lastRecallDate

    private lateinit var activitiesBox: Box<Activity>
    private lateinit var patientBox: Box<Patient>
//    private lateinit var activitiesQuery: Query<Activity>
//    private lateinit var geographiesQuery: Query<Geography>


    private lateinit var patient: Patient
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_referral, container, false)

        activitiesBox = ObjectBox.boxStore.boxFor(Activity::class.java)
        patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)


        checkboxNoReferral = view.findViewById(R.id.checkBoxNoReferral)
        checkboxHealthPost = view.findViewById(R.id.checkBoxHealthPost)
        checkboxHygienist = view.findViewById(R.id.checkBoxHygienist)
        checkboxDentist = view.findViewById(R.id.checkBoxDentist)
        checkboxGeneralPhysician = view.findViewById(R.id.checkBoxGeneralPhysician)
        checkboxOther = view.findViewById(R.id.checkBoxOther)

        tvRecallDateReferral = view.findViewById(R.id.tvRecallDateReferral)
        rgRecalls = view.findViewById(R.id.rgRecalls)
        etOtherDetails = view.findViewById(R.id.etOtherDetails)

        etRecallDate = view.findViewById(R.id.etRecallDate)
        etRecallTime = view.findViewById(R.id.etRecallTime)

        if(DentalApp.lastRecallDate.isNotEmpty()){
            etRecallDate.setText(DateHelper.getReadableNepaliDate(DentalApp.lastRecallDate))
        }

        etRecallTime.setText(DentalApp.lastRecallTime)

        checkboxNoReferral.isChecked = false
        checkboxHealthPost.isChecked = false
        checkboxHygienist.isChecked = false
        checkboxDentist.isChecked = false
        checkboxGeneralPhysician.isChecked = false
        checkboxOther.isChecked = false

        checkboxNoReferral.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                checkBoxHealthPost.isChecked = false
                checkBoxHygienist.isChecked = false
                checkBoxDentist.isChecked = false
                checkBoxGeneralPhysician.isChecked = false
                checkBoxOther.isChecked = false
                etOtherDetails.setText("")
                etOtherDetails.visibility = View.GONE

                tvRecallDateReferral.visibility = View.GONE
                rgRecalls.visibility = View.GONE
                etRecallDate.visibility = View.GONE
                etRecallTime.visibility = View.GONE
            }
        }

        uncheckNoReferral(checkboxHealthPost)
        uncheckNoReferral(checkboxHygienist)
        uncheckNoReferral(checkboxDentist)
        uncheckNoReferral(checkboxGeneralPhysician)
        uncheckNoReferral(checkboxOther)

        checkboxHealthPost.setOnCheckedChangeListener { compoundButton, b ->

            if (checkboxNoReferral.isChecked) {
                Toast.makeText(
                    activity,
                    "Please uncheck the No Referral.",
                    Toast.LENGTH_SHORT
                ).show()
                checkboxHealthPost.isChecked = false
            }

            if (compoundButton.isChecked) {
                tvRecallDateReferral.visibility = View.VISIBLE
                etRecallDate.visibility = View.VISIBLE
                rgRecalls.visibility = View.VISIBLE
                etRecallTime.visibility = View.VISIBLE

                tvRecallDateReferral.visibility = View.VISIBLE
                etRecallDate.visibility = View.VISIBLE
                etRecallTime.visibility = View.VISIBLE
            } else {
                tvRecallDateReferral.visibility = View.GONE
                rgRecalls.visibility = View.GONE
                etRecallDate.visibility = View.GONE
                etRecallTime.visibility = View.GONE

                tvRecallDateReferral.visibility = View.GONE
                etRecallDate.visibility = View.GONE
                etRecallTime.visibility = View.GONE
            }
        }

        checkboxOther.setOnCheckedChangeListener { compoundButton, b ->

            if (checkboxNoReferral.isChecked) {
                Toast.makeText(
                    activity,
                    "Please uncheck the No Referral.",
                    Toast.LENGTH_SHORT
                ).show()
                checkboxOther.isChecked = false
            } else {
                if (compoundButton.isChecked) {
                    etOtherDetails.visibility = View.VISIBLE
                } else {
                    etOtherDetails.visibility = View.GONE
                }
            }
        }

        rgRecalls.setOnCheckedChangeListener { radioGroup, i ->
            var recallDate = ""
            val nepaliCalender = DateConverter()

            val todayNepali = nepaliCalender.todayNepaliDate

            val yearToday = todayNepali.year
            val monthToday = todayNepali.month + 1
            val dayToday = todayNepali.day

            when (i) {
                R.id.radioOneWeek -> {
                    recallDate = if (dayToday + 7 > 30 && monthToday+1 > 12) {
                        "${yearToday+1}-" + DecimalFormat("00").format("01") + "-" + DecimalFormat(
                            "00"
                        ).format((dayToday + 7) % 30)
                    } else if(dayToday+7 > 30){
                            "$yearToday-" + DecimalFormat("00").format(monthToday + 1) + "-" + DecimalFormat(
                                "00"
                            ).format((dayToday + 7) % 30)

                    }
                    else{
                          "$yearToday-" + DecimalFormat("00").format(monthToday) + "-" + DecimalFormat(
                                "00"
                            ).format((dayToday + 7) % 30)

                    }

                }
                R.id.radioOneMonth -> {
                    recallDate = if (monthToday == 12) {
                        "${yearToday + 1}-" + DecimalFormat("00").format("01") + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    } else {
                        "${yearToday}-" + DecimalFormat("00").format(monthToday + 1) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    }
                }
                R.id.radioSixMonths -> {
                    recallDate = if (monthToday + 6 > 12) {
                        "${yearToday + 1}-" + DecimalFormat("00").format(((monthToday + 6) % 12)) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    } else {
                        "$yearToday-" + DecimalFormat("00").format(monthToday + 6) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    }
                }
                R.id.radioOneYear -> {
                    recallDate =
                        "${yearToday + 1}-" + DecimalFormat("00").format(monthToday) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                }
            }
            recallDateOriginal = recallDate
            if(recallDate.isNotEmpty()){
                etRecallDate.setText(DateHelper.getReadableNepaliDate(recallDate))
            }
        }

        etRecallDate.setOnFocusChangeListener { _, b ->
            if (b) {
                val nepaliDateConverter = DateConverter()
                val dpd =
                    com.hornet.dateconverter.DatePicker.DatePickerDialog.newInstance { view, year, monthOfYear, dayOfMonth ->
                        val month = DecimalFormat("00").format(monthOfYear + 1).toString()
                        val day = DecimalFormat("00").format(dayOfMonth).toString()
                        val recallDate = "$year-$month-$day"
                        recallDateOriginal = recallDate
                        if(recallDate.isNotEmpty()){
                            etRecallDate.setText(DateHelper.getReadableNepaliDate(recallDate))
                        }
                    }
                dpd.setMinDate(nepaliDateConverter.todayNepaliDate)
                fragmentManager?.let { dpd.show(it, "RecallDate") }

            }
        }
        etRecallTime.setOnFocusChangeListener { _, b ->
            if (b) {
                // Get Current Time
                val c = Calendar.getInstance()
                val mHour = c.get(Calendar.HOUR_OF_DAY)
                val mMinute = c.get(Calendar.MINUTE)

                // Launch Time Picker Dialog
                val timePickerDialog = TimePickerDialog(
                    activity,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        etRecallTime.setText(
                            DecimalFormat("00").format(
                                hourOfDay
                            ) + ":" + DecimalFormat("00").format(minute)
                        )
                    },
                    mHour,
                    mMinute,
                    false
                )
                timePickerDialog.show()
            }
        }

        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)
        return view
    }

    private fun uncheckNoReferral(checkbox : CheckBox) {
        checkbox.setOnCheckedChangeListener { compoundButton, _ ->
            if (checkboxNoReferral.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    activity,
                    "Please uncheck the No Referral.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        referralFormCommunicator = activity as ReferralFormCommunicator

        setupUI(activity as Context)

        btnNext.setOnClickListener {
            val view = this.view!!.rootView
            if (view != null) {
                val imm = (activity as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            if (isFormValid()) {
                val noReferral = checkboxNoReferral.isChecked
                val healthPost = checkboxHealthPost.isChecked
                val hygienist = checkboxHygienist.isChecked
                val dentist = checkboxDentist.isChecked
                val generalPhysician = checkboxGeneralPhysician.isChecked
                val other = checkboxOther.isChecked
                val otherDetails = etOtherDetails.text.toString()

                referralFormCommunicator.updateReferral(
                    noReferral,
                    healthPost,
                    hygienist,
                    dentist,
                    generalPhysician,
                    other,
                    otherDetails
                )

                var recallDate = ""
                var recallTime = ""
                if (healthPost) {
                    recallDate = recallDateOriginal
                    recallTime = etRecallTime.text.toString()
                    DentalApp.lastRecallDate = recallDate
                    DentalApp.lastRecallTime = recallTime
                } else {
                    try {
                        if (patient != null) {
                            patient.called = false
                            patientBox.put(patient)
                        }
                    } catch (e: Exception) {
                        Log.d("Referral Fragment", "patient not found i.e. not initialized.")
                    }
                }
                //referralFormCommunicator.updateRecall(recallDate, recallTime, selectedGeography, selectedActivity)
                referralFormCommunicator.updateRecallDate(recallDate, recallTime)

                fragmentCommunicator.goForward()

            } else {
                // form is not valid
            }
        }
        btnBack.setOnClickListener {
            val view = this.view!!.rootView
            if (view != null) {
                val imm = (activity as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            fragmentCommunicator.goBack()
        }
    }

    private fun setupUI(applicationContext: Context) {

        val encounterId =
            DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()

        if (encounterId != 0.toLong()) {

            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

            patient = patientBox.query().equal(
                Patient_.id,
                DentalApp.readIntFromPreference(
                    applicationContext,
                    Constants.PREF_SELECTED_PATIENT
                ).toLong()
            ).build().findFirst()!!
            referral = referralBox.query()
                .equal(Referral_.encounterId, encounter.id)
                .orderDesc(Referral_.id).build().findFirst()!!

            val checkboxMap = mapOf(
                checkboxNoReferral to referral.no_referral,
                checkboxHealthPost to referral.health_post,
                checkboxHygienist to referral.hygienist,
                checkboxDentist to referral.dentist,
                checkboxGeneralPhysician to referral.general_physician,
                checkboxOther to referral.other
            )

            for (checkbox in checkboxMap) {
                if (checkbox.value) {
                    checkbox.key.isChecked = true
                }
            }
        if (!referral.other_details.isNullOrEmpty()) etOtherDetails.setText(referral.other_details)
            recallDateOriginal = patient.recall_date!!
            if(patient.recall_date!!.isNotEmpty()){
                etRecallDate.setText(DateHelper.getReadableNepaliDate(patient.recall_date!!))
            }
            etRecallTime.setText(patient.recall_time)
        }
    }



    private fun isFormValid(): Boolean {
        var status = true

        // For checking if any checkbox button is clicked or not
        if (!(checkboxNoReferral.isChecked) && !(checkboxHealthPost.isChecked) && !(checkboxHygienist.isChecked) &&
            !(checkboxDentist.isChecked) && !(checkboxGeneralPhysician.isChecked) && !(checkboxOther.isChecked)) {
            Toast.makeText(activity, "Checkbox is not selected", Toast.LENGTH_SHORT).show()
            status = false
        }

        if (checkboxHealthPost.isChecked) {
            if (etRecallDate.text.isNullOrBlank() || etRecallTime.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    "Recall Date and Time should be specified.",
                    Toast.LENGTH_SHORT
                ).show()
                status = false
            }
        }
        return status
    }
}
