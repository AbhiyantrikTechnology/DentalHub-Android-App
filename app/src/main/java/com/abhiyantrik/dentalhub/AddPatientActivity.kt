package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.utils.AdapterHelper
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.DateValidator
import com.abhiyantrik.dentalhub.workers.UpdatePatientWorker
import com.abhiyantrik.dentalhub.workers.UploadPatientWorker
import com.google.firebase.perf.metrics.AddTrace
import com.hornet.dateconverter.DateConverter
import io.objectbox.Box
import java.lang.Exception
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class AddPatientActivity : AppCompatActivity() {

    private lateinit var btnAddPatient: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText
    private lateinit var spinnerWard: Spinner
    private lateinit var spinnerMunicipality: Spinner
    private lateinit var spinnerDistrict: Spinner

    private lateinit var spinnerDobDay: Spinner
    private lateinit var spinnerDobMonth: Spinner
    private lateinit var spinnerDobYear: Spinner


    private lateinit var loading: ProgressBar
    private lateinit var tvErrorMessage: TextView

    private lateinit var context: Context
    private var patient: Patient? = null
    private val TAG = "AddPatientActivity"
    private var action = "new"
    private var patientId: Long = 0

    private var allWards = mutableListOf<Ward>()
    private var allMunicipalities = mutableListOf<Municipality>()
    private var allDistricts = mutableListOf<District>()

    private lateinit var patientsBox: Box<Patient>
    private lateinit var districtsBox: Box<District>
    private lateinit var municipalitiesBox: Box<Municipality>
    private lateinit var wardsBox: Box<Ward>
    private lateinit var patientBox: Box<Patient>

    @AddTrace(name = "onCreateAddPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        patientId = intent.getLongExtra("PATIENT_ID", 0)
        if (patientId != 0.toLong()) {
            patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()
        }
        action = intent.getStringExtra("ACTION")
        context = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()
    }

    @AddTrace(name = "initUIAddPatientActivity", enabled = true /* optional */)
    private fun initUI() {
        districtsBox =
            ObjectBox.boxStore.boxFor(District::class.java)
        municipalitiesBox = ObjectBox.boxStore.boxFor(Municipality::class.java)
        wardsBox = ObjectBox.boxStore.boxFor(Ward::class.java)

        loading = findViewById(R.id.loading)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etLastName = findViewById(R.id.etLastName)

        spinnerWard = findViewById(R.id.spinnerWard)
        spinnerMunicipality = findViewById(R.id.spinnerMunicipality)
        spinnerDistrict = findViewById(R.id.spinnerDistrict)

        spinnerDobDay = findViewById(R.id.spinnerDobDay)
        spinnerDobMonth = findViewById(R.id.spinnerDobMonth)
        spinnerDobYear = findViewById(R.id.spinnerDobYear)


        etPhone = findViewById(R.id.etPhone)

        btnAddPatient = findViewById(R.id.btnAddPatient)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerEducationLevel = findViewById(R.id.spinnerEducationLevel)

        spinnerGender.adapter =
            AdapterHelper.createAdapter(
                context,
                resources.getStringArray(R.array.gender_list).toList()
            )

        spinnerEducationLevel.adapter =
            AdapterHelper.createAdapter(
                context,
                resources.getStringArray(R.array.education_level_list).toList()
            )

        val nepaliDateToday = DateConverter().todayNepaliDate
        val currentYear = nepaliDateToday.year
        val startYear = currentYear - 120

        spinnerDobDay.adapter = AdapterHelper.createAdapterWithInts(context, (1..32).toList())
        spinnerDobMonth.adapter =
            AdapterHelper.createAdapter(context, resources.getStringArray(R.array.months).toList())
        spinnerDobYear.adapter = AdapterHelper.createAdapterWithInts(
            context,
            (startYear..currentYear).reversed().toList()
        )

        updateUI()
        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)

        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * If nothing is selected we dont have to do any other operation
                 */
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (patient != null) {
                    setupMunicipalities(patient!!.municipality)
                } else {
                    setupMunicipalities(0)
                }

            }
        }
        spinnerMunicipality.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no thing to do yet
                Log.d("TAG", "nothing selected")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (patient != null) {
                    setupWards(patient!!.ward)
                } else {
                    setupWards(0)
                }

            }

        }

        btnAddPatient.setOnClickListener {
            if (isFormValid()) {
                DentalApp.lastDistrictIndex = spinnerDistrict.selectedItemPosition+1
                DentalApp.lastMunicipalityIndex = spinnerMunicipality.selectedItemPosition
                DentalApp.lastWardIndex = spinnerWard.selectedItemPosition
                DentalApp.lastEducationLevel = spinnerEducationLevel.selectedItemPosition
                savePatient()
            }
        }
    }

    private fun setupWards(selectedWard: Int) {
        if (allMunicipalities.size > 0) {
            val dbMunicipality = allMunicipalities[spinnerMunicipality.selectedItemPosition]
            Log.d("Selected Municipality: ", spinnerMunicipality.selectedItem.toString())
            Log.d("Municipality Position: ", spinnerMunicipality.selectedItemPosition.toString())
            var selectedWardIndex = 0

            val dbWards =
                wardsBox.query().equal(Ward_.municipalityId, dbMunicipality.id).build().find()
            val wards = mutableListOf<String>()
            allWards = dbWards

            if(selectedWard == 0){
                for ((_, ward) in dbWards.withIndex()) {
                    wards.add(ward.ward.toString())
                }
                selectedWardIndex = DentalApp.lastWardIndex
            }else{
                for ((count, ward) in dbWards.withIndex()) {
                    if (selectedWard == ward.remote_id) {
                        selectedWardIndex = count
                    }
                    wards.add(ward.ward.toString())
                }
            }
            spinnerWard.adapter = AdapterHelper.createAdapter(context, wards.toList())
            try {
                spinnerWard.setSelection(selectedWardIndex)
            }catch (e: IndexOutOfBoundsException){
                spinnerWard.setSelection(0)
            }


        } else {
            Toast.makeText(context, "Municipality not found.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupMunicipalities(selectedMunicipality: Int) {
        Log.d("Selected District", spinnerDistrict.selectedItem.toString())
        Log.d("District Position", spinnerDistrict.selectedItemPosition.toString())

        var selectedMunicipalityIndex = 0

        val dbDistrict = allDistricts[spinnerDistrict.selectedItemPosition]
        allMunicipalities =
            municipalitiesBox.query().equal(Municipality_.districtId, dbDistrict.id).build().find()
        val municipalitiesList = mutableListOf<String>()

        if(selectedMunicipality==0){
            for ((_, municipality) in allMunicipalities.withIndex()) {
                municipalitiesList.add(municipality.name.capitalize())
            }
            selectedMunicipalityIndex = DentalApp.lastMunicipalityIndex
        }else{
            for ((count, municipality) in allMunicipalities.withIndex()) {
                if (selectedMunicipality == municipality.remote_id) {
                    selectedMunicipalityIndex = count
                }
                municipalitiesList.add(municipality.name.capitalize())
            }
        }
        spinnerMunicipality.adapter =
            AdapterHelper.createAdapter(context, municipalitiesList.toList())
        try {
            spinnerMunicipality.setSelection(selectedMunicipalityIndex)
        }catch (e: IndexOutOfBoundsException){
            spinnerMunicipality.setSelection(0)
        }

        if (patient != null) {
            setupWards(patient!!.ward)
        } else {
            setupWards(0)
        }

    }

    private fun setupDistricts(selectedDistrict: Int) {

        allDistricts = districtsBox.query().build().find()
        val districtsList = mutableListOf<String>()
        var selectedDistrictIndex = 0
        for ((count, district) in allDistricts.withIndex()) {
            if (selectedDistrict == district.remote_id) {
                selectedDistrictIndex = count
            }
            districtsList.add(district.name.capitalize())
        }
        spinnerDistrict.adapter = AdapterHelper.createAdapter(context, districtsList.toList())
        try {
            spinnerDistrict.setSelection(selectedDistrictIndex)
        }catch (e: IndexOutOfBoundsException){
            spinnerDistrict.setSelection(0)
        }

        if (patient != null) {
            setupMunicipalities(patient!!.municipality)
        } else {
            setupMunicipalities(0)
        }

    }

    private fun updateUI() {
        if (patient != null) {
            title = resources.getString(R.string.edit) + " : " + patient!!.fullName()
            etFirstName.setText(patient!!.first_name)
            etMiddleName.setText(patient!!.middle_name)
            etLastName.setText(patient!!.last_name)
            etPhone.setText(patient!!.phone)
            setupDistricts(patient!!.district)
            spinnerGender.setSelection(resources.getStringArray(R.array.gender_list).indexOf(patient!!.gender))
            spinnerEducationLevel.setSelection(
                resources.getStringArray(R.array.education_level_list).indexOf(
                    patient!!.education
                )
            )
            //spinnerDobDay.setSelection()
            val nepaliDateToday = DateConverter().todayNepaliDate
            val currentYear = nepaliDateToday.year
            val startYear = currentYear - 120

            val dobDay = patient!!.dob.substring(8, 10)
            val dobMonth = patient!!.dob.substring(5, 7)
            val dobYear = patient!!.dob.substring(0, 4)
            spinnerDobDay.setSelection(dobDay.toInt() - 1)
            spinnerDobMonth.setSelection(dobMonth.toInt() - 1)
            spinnerDobYear.setSelection((startYear..currentYear).reversed().toList().indexOf(dobYear.toInt()))
        } else {
            title = resources.getString(R.string.add_new_patient)
            spinnerDobDay.setSelection(DentalApp.lastDobDayIndex)
            spinnerDobMonth.setSelection(DentalApp.lastDobMonthIndex)
            spinnerDobYear.setSelection(DentalApp.lastDobYearIndex)
            setupDistricts(DentalApp.lastDistrictIndex) // set default as kaski 35 is remote_id of Kaski District
//            spinnerEducationLevel.setSelection(())
            spinnerEducationLevel.setSelection(DentalApp.lastEducationLevel)
        }
    }

    @AddTrace(name = "savePatientAddPatientActivity", enabled = true /* optional */)
    private fun savePatient() {
        Log.d(TAG, "savePatient()")
        loading.visibility = View.VISIBLE
        tvErrorMessage.visibility = View.GONE
        val patient = createPatient()
        saveToLocalDB(patient)
        loading.visibility = View.GONE
    }

    @AddTrace(name = "createPatientAddPatientActivity", enabled = true /* optional */)
    private fun createPatient(): Patient {
        Log.d(TAG, "createPatient()")

        val dbDistrict = allDistricts[spinnerDistrict.selectedItemPosition]
        val dbMunicipality = allMunicipalities[spinnerMunicipality.selectedItemPosition]
        val dbWard = allWards[spinnerWard.selectedItemPosition]

        val id: Long = 0
        val firstName = etFirstName.text.toString()
        val middleName = etMiddleName.text.toString()
        val lastName = etLastName.text.toString()
        val gender = spinnerGender.selectedItem.toString()
        val dob = getFormattedDob()
        val phone = etPhone.text.toString()
        val education = spinnerEducationLevel.selectedItem.toString()
        val ward = dbWard.remote_id
        val municipality = dbMunicipality.remote_id
        val district = dbDistrict.remote_id
        val geography = DentalApp.geography_id
        val activity = DentalApp.activity_id
        val latitude = DentalApp.location.latitude
        val longitude = DentalApp.location.longitude
        val date = DateHelper.getCurrentNepaliDate()
        if (action == "edit") {
            patient!!.id = patientId
            patient = patientsBox.get(patientId)
            patient!!.first_name = firstName
            patient!!.middle_name = middleName
            patient!!.last_name = lastName
            patient!!.gender = gender
            patient!!.dob = dob
            patient!!.phone = phone
            patient!!.education = education
            patient!!.ward = ward
            patient!!.municipality = municipality
            patient!!.district = district
            patient!!.latitude = latitude
            patient!!.longitude = longitude
            patient!!.geography_id = DentalApp.geography_id
            patient!!.activityarea_id = DentalApp.activity_id
            patient!!.created_at = date
            patient!!.updated_at = date
            patient!!.updated = true
            patient!!.author = DentalApp.readFromPreference(context, Constants.PREF_PROFILE_ID, "")
            patient!!.updated_by =
                DentalApp.readFromPreference(context, Constants.PREF_PROFILE_ID, "")
            return patient!!
        } else {
            val tempPatient = Patient()
            tempPatient.id = id
            tempPatient.remote_id = ""
            tempPatient.first_name = firstName
            tempPatient.middle_name = middleName
            tempPatient.last_name = lastName
            tempPatient.gender = gender
            tempPatient.dob = dob
            tempPatient.phone = phone
            tempPatient.education = education
            tempPatient.ward = ward
            tempPatient.municipality = municipality
            tempPatient.district = district
            tempPatient.latitude = latitude
            tempPatient.longitude = longitude
            tempPatient.geography_id = geography
            tempPatient.activityarea_id = activity
            tempPatient.created_at = date
            tempPatient.updated_at = date
            tempPatient.uploaded = false
            tempPatient.updated = false
            tempPatient.recall = null
            tempPatient.author =
                DentalApp.readFromPreference(context, Constants.PREF_PROFILE_ID, "")
            tempPatient.updated_by =
                DentalApp.readFromPreference(context, Constants.PREF_PROFILE_ID, "")
            return tempPatient

        }
    }

    private fun getFormattedDob(): String {
        DentalApp.lastDobDayIndex = spinnerDobDay.selectedItemPosition
        DentalApp.lastDobMonthIndex = spinnerDobMonth.selectedItemPosition
        DentalApp.lastDobYearIndex = spinnerDobYear.selectedItemPosition

        val dobYear = spinnerDobYear.selectedItem.toString()
        val dobMonth = DecimalFormat("00").format(spinnerDobMonth.selectedItemPosition + 1)
        val dobDay = DecimalFormat("00").format(spinnerDobDay.selectedItem)
        return "$dobYear-$dobMonth-$dobDay"
    }

    @AddTrace(name = "saveToLocalDBAddPatientActivity", enabled = true /* optional */)
    private fun saveToLocalDB(patient: Patient) {
        Log.d(TAG, "saveToLocalDB")
        patientsBox.put(patient)
        val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
        if (action == "new") {
            val dbPatientEntity = patientBox.query().orderDesc(Patient_.id).build().findFirst()!!

            val data = Data.Builder().putLong("PATIENT_ID", dbPatientEntity.id)
            val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<UploadPatientWorker>()
                .setInputData(data.build())
                .setConstraints(DentalApp.uploadConstraints)
                .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(applicationContext).enqueue(uploadPatientWorkRequest)


            viewPatientIntent.putExtra("PATIENT_ID", dbPatientEntity.id)
            startActivity(viewPatientIntent)
            finish()
        } else {
            val data = Data.Builder().putLong("PATIENT_ID", patient.id)
            val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<UpdatePatientWorker>()
                .setInputData(data.build())
                .setConstraints(DentalApp.uploadConstraints)
                .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
            WorkManager.getInstance(applicationContext).enqueue(uploadPatientWorkRequest)
            finish()
        }
    }

    @AddTrace(name = "isFormValidAddPatientActivity", enabled = true /* optional */)
    private fun isFormValid(): Boolean {
        tvErrorMessage.visibility = View.GONE

        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val phone = etPhone.text.toString()
        val dob = getFormattedDob()

        if (spinnerDistrict.selectedItem == null) {
            tvErrorMessage.text = "District is not selected."
            tvErrorMessage.visibility = View.VISIBLE
        }

        if (spinnerMunicipality.selectedItem == null) {
            tvErrorMessage.text = "Municipality is not selected."
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }

        if (spinnerWard.selectedItem == null) {
            tvErrorMessage.text = "Ward is not selected."
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }

        if (firstName.isBlank() || firstName.isEmpty() || firstName.length < 2) {
            tvErrorMessage.text = resources.getString(R.string.first_name_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (lastName.isBlank() || lastName.isEmpty() || lastName.length < 2) {
            tvErrorMessage.text = resources.getString(R.string.last_name_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (phone.isBlank() || phone.isEmpty()) {
            tvErrorMessage.text = resources.getString(R.string.phone_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (phone.length < 5) {
            tvErrorMessage.text = resources.getString(R.string.valid_phone_number_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        val dobYear = spinnerDobYear.selectedItem.toString()
        val dobMonth = (spinnerDobMonth.selectedItemPosition).toString()
        val dobDay = (spinnerDobDay.selectedItem).toString()
        if (!DateValidator.isValid(dobYear.toInt(), dobMonth.toInt(), dobDay.toInt())) {
            tvErrorMessage.text = resources.getString(R.string.valid_date_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}