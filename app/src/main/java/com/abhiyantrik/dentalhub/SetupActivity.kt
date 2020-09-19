package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.District
import com.abhiyantrik.dentalhub.models.Profile
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.workers.DownloadPatientWorker
import com.abhiyantrik.dentalhub.workers.DownloadUsersWorker
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.concurrent.TimeUnit
import com.abhiyantrik.dentalhub.models.Patient as PatientModel

class SetupActivity : AppCompatActivity() {

    private val TAG = "SetupActivity"
    private lateinit var tvMessage: TextView

    private lateinit var districtsBox: Box<com.abhiyantrik.dentalhub.entities.District>
    private lateinit var municipalitiesBox: Box<Municipality>
    private lateinit var patientsBox: Box<Patient>
    private lateinit var wardsBox: Box<Ward>
    var allDistricts = listOf<District>()

    private lateinit var context: Context
    var profileLoadComplete = false
    var dataLoadComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        context = this
        initUI()
        loadProfile()

    }

    private fun logout() {
        DentalApp.clearAuthDetails(context)
        Toast.makeText(context, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(context, LoginActivity::class.java))
        finish()
    }

    private fun loadProfile() {
        Log.d(TAG, "startSync")

        val downloadUsersWorkRequest = OneTimeWorkRequestBuilder<DownloadUsersWorker>()
            .setConstraints(DentalApp.downloadConstraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(downloadUsersWorkRequest)

        val downloadPatientWorkRequest = OneTimeWorkRequestBuilder<DownloadPatientWorker>()
            .setInitialDelay(100, TimeUnit.MILLISECONDS)
            .setConstraints(DentalApp.downloadConstraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(downloadPatientWorkRequest)

        tvMessage.append("Loading profile...\n")
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.fetchProfile("JWT $token")
        call.enqueue(object : Callback<Profile> {
            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                if (BuildConfig.DEBUG) {
                    tvMessage.append(t.message.toString())
                } else {
                    tvMessage.append("Failed to load profile\n")
                }
                logout()
            }

            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                Log.d("SetupActivity", response.code().toString())
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val p = response.body() as Profile
                            DentalApp.fullName = p.fullName()
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_PROFILE_FULL_NAME,
                                p.fullName()
                            )
                            DentalApp.saveToPreference(context, Constants.PREF_PROFILE_ID, p.id)
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_PROFILE_IMAGE,
                                p.image
                            )
                            profileLoadComplete = true
                            tvMessage.append("Loading profile complete\n")
                            loadData()
                        } else -> {
                            logout()
                        }
                    }
                } else {
                    Log.d("SetupActivity", "response failed")
                    logout()
                }
            }

        })
    }

    private fun loadData() {

        Log.d(TAG, "listAddressess()")
        tvMessage.append("Loading addresses...\n")
        val panelService = DjangoInterface.create(this)
        val call = panelService.listAddresses()
        call.enqueue(object : Callback<List<District>> {
            override fun onFailure(call: Call<List<District>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                tvMessage.append("Failed to load addresses \n")
                Log.d(TAG, t.toString())
                logout()
            }

            override fun onResponse(
                call: Call<List<District>>,
                response: Response<List<District>>
            ) {
                Log.d(TAG, "onResponse()")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            allDistricts = response.body() as List<District>
                            storeDistricts(allDistricts)
                            tvMessage.append("Loading address complete\n")
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_SETUP_COMPLETE,
                                "true"
                            )
                            dataLoadComplete = true
                            startActivity(Intent(context, LocationSelectorActivity::class.java))
                            finish()
                        } else -> {
                            logout()
                        }
                    }
                } else {
                    Log.d(TAG, response.code().toString())
                    logout()
                }
            }
        })
    }

    private fun storeDistricts(allDistricts: List<District>) {
        for (district in allDistricts) {
            if (districtsBox.query().equal(
                    District_.name,
                    district.name
                ).build().count() == 0.toLong()
            ) {
                val newDistrict = District()
                newDistrict.remote_id = district.id
                newDistrict.name = district.name
                districtsBox.put(newDistrict)
            }


            for (municipality in district.municipalities) {
                val dbDistrict =
                    districtsBox.query().orderDesc(District_.id).build()
                        .findFirst()
                if (municipalitiesBox.query().equal(
                        Municipality_.name,
                        municipality.name
                    ).build().count() == 0.toLong()
                ) {
                    val newMunicipality = Municipality()
                    newMunicipality.remote_id = municipality.id
                    newMunicipality.name = municipality.name
                    newMunicipality.district?.target = dbDistrict
                    municipalitiesBox.put(newMunicipality)
                    for (ward in municipality.wards) {
                        val dbMunicipality = municipalitiesBox.query()
                            .orderDesc(Municipality_.id).build().findFirst()
                        val newWard = Ward()
                        newWard.remote_id = ward.id
                        newWard.ward = ward.ward
                        newWard.name = ward.name
                        newWard.municipality?.target = dbMunicipality
                        wardsBox.put(newWard)
                    }
                }
            }
        }
    }

    private fun initUI() {
        tvMessage = findViewById(R.id.tvMessage)

        districtsBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.District::class.java)
        municipalitiesBox = ObjectBox.boxStore.boxFor(Municipality::class.java)
        wardsBox = ObjectBox.boxStore.boxFor(Ward::class.java)
        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)

    }
}
