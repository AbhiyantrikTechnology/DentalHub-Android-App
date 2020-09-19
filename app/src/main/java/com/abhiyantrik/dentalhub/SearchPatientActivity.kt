package com.abhiyantrik.dentalhub

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.adapters.PatientAdapter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.query.Query
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SearchPatientActivity : AppCompatActivity() {

    private lateinit var context: Context

    private lateinit var recyclerView: RecyclerView

    private lateinit var patientsearchlist: List<Patient>

    private lateinit var patientsBox: Box<Patient>
    private lateinit var patientQuery: Query<Patient>
    private lateinit var recyclerAdapter: PatientAdapter
    private lateinit var manager: SearchManager
    private lateinit var searchView: SearchView

    private val TAG = "SearchPatientActivity"

    @AddTrace(name = "onCreateSearchPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_search_patient)

        context = this
        title = getString(R.string.search_label)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        DentalApp.saveIntToPreference(context, Constants.PREF_LAST_SELECTED_PATIENT_POSITION, 0)
        setupUI()

    }

    private fun setupUI() {
//        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
//        patientsQuery = patientsBox.query().build()

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        patientQuery = patientsBox.query().build()


        recyclerView = findViewById(R.id.recyclerViewSearchPatient)

        val layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager

//        val recyclerAdapter = PatientAdapter()


    }

    override fun onResume() {
        super.onResume()
        listPatients()
        val lastSelectedPosition = DentalApp.readIntFromPreference(context, Constants.PREF_LAST_SELECTED_PATIENT_POSITION)
        recyclerView.scrollToPosition(lastSelectedPosition)
        //manager.startSearch(null, false, componentName, null, false)
//        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
    }

    private fun listPatients() {
        try {
            patientsearchlist =
                patientsBox.query().equal(Patient_.geography_id, DentalApp.geography_id.toLong())
                    .orderDesc(Patient_.created_at).orderDesc(Patient_.id).build()
                    .find()
            setupAdapter()
        } catch (e: DbException) {
            Log.d("DBException", e.printStackTrace().toString())
        }

    }

    private fun setupAdapter() {
        recyclerAdapter = PatientAdapter(
            context,
            patientsearchlist,
            false,
            object : PatientAdapter.PatientClickListener {
                override fun onRemovePatientClick(patient: Patient) {
                    val tempPatient =
                        patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()!!
                    tempPatient.created_at = ""
                    patientsBox.put(tempPatient)
                    listPatients()
                }

                override fun onViewPatientDetailClick(position: Int, patient: Patient) {
                    DentalApp.saveIntToPreference(context, Constants.PREF_LAST_SELECTED_PATIENT_POSITION, position)
                    val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
                    viewPatientIntent.putExtra("PATIENT_ID", patient.id)
                    startActivity(viewPatientIntent)
                }

                override fun onCallPatientClick(patient: Patient) {
                    if(DentalApp.canMakeCall(context)){
                        val call = Intent(Intent.ACTION_DIAL)
                        call.data = Uri.parse("tel:" + patient.phone)
                        startActivity(call)
                    }else{
                        Toast.makeText(context, getString(R.string.telephony_serivce_unavailable), Toast.LENGTH_LONG).show()
                    }
                }

                override fun onDelayPatientClick(patient: Patient) {
                    displayDelayDialog(patient)
                }

            })
        recyclerView.adapter = recyclerAdapter
    }

    private fun displayDelayDialog(patient: Patient) {
        // delay recall of patient
        val grpName = arrayOf(
            "1 week",
            "2 weeks",
            "3 weeks",
            "1 month",
            "2 months",
            "3 months"
        )
        val delayChooser = androidx.appcompat.app.AlertDialog.Builder(this)
        delayChooser.setTitle(getString(R.string.delay))
        delayChooser.setSingleChoiceItems(
            grpName,
            -1,
            DialogInterface.OnClickListener { dialog, item ->
                Log.d("DELAYED: ", patient.fullName() + " by " + grpName[item])
                Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show()

                val tempPatient =
                    patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()!!
                val calendar = Calendar.getInstance()
                try {
                    calendar.time = SimpleDateFormat("yyyy/MM/dd").parse(tempPatient.recall_date)
                } catch (e: ParseException) {
                    Log.e("ParseException", e.printStackTrace().toString())
                }
                when (item) {
                    0 -> {
                        calendar.add(Calendar.DAY_OF_MONTH, 7)
                    }
                    1 -> {
                        calendar.add(Calendar.DAY_OF_MONTH, 14)
                    }
                    2 -> {
                        calendar.add(Calendar.DAY_OF_MONTH, 21)
                    }
                    3 -> {
                        calendar.add(Calendar.DAY_OF_MONTH, 28)

                    }
                    4 -> {
                        calendar.add(Calendar.DAY_OF_MONTH, 60)
                    }
                    5 -> {
                        calendar.add(Calendar.DAY_OF_MONTH, 90)
                    }
                }

                val newDate = SimpleDateFormat("yyyy-mm-dd").format(calendar.time)
                tempPatient.recall_date = newDate
                patientsBox.put(tempPatient)
                listPatients()
                dialog.dismiss()// dismiss the alert box after chose option
            })
        val alert = delayChooser.create()
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_patient_menu, menu)


        val searchItem = menu?.findItem(R.id.searchPatient)
        searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()

                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, "Looking for the $query", Toast.LENGTH_SHORT).show()
                }

                try {
                    patientsearchlist = patientsBox.query()
                        .contains(Patient_.first_name, query)
                        .or()
                        .contains(Patient_.last_name, query)
                        .or()
                        .contains(Patient_.last_name, query)
                        .orderDesc(Patient_.created_at)
                        .equal(Patient_.geography_id, DentalApp.geography_id.toLong())
                        .build().find()
                } catch (e: DbException) {
                    Log.d("DBException", e.printStackTrace().toString())
                }
                println("Query result is $patientsearchlist")

                setupAdapter()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
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
