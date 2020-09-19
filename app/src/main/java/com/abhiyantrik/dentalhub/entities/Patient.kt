package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import android.util.Log
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.hornet.dateconverter.DateConverter
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*


@Entity
@Parcelize
class Patient : Parcelable {
    @Id
    var id: Long = 0
    var remote_id: String = ""
    var first_name: String = ""
    var middle_name: String = ""
    var last_name: String = ""
    var gender: String = ""
    var dob: String = ""
    var phone: String = ""
    var education: String = ""
    var ward: Int = 0
    var municipality: Int = 0
    var district: Int = 0
    var latitude: String = ""
    var longitude: String = ""
    var geography_id: Int = 0
    var activityarea_id: String = ""
    var recall_date: String? = ""
    var recall_time: String? = "00:01:00"
    var recall_geography: Int = 0
    var called: Boolean = false
    var created_at: String? = ""
    var updated_at: String? = ""
    var author: String = ""
    var updated_by: String? = ""
    var uploaded: Boolean = false
    var updated: Boolean = false
    var content: String = "patient"
    @Backlink(to = "patient")
    var recall: ToMany<Recall>? = null


    @IgnoredOnParcel
    @Backlink(to = "patient")
    var encounters: ToMany<Encounter>? = null


    fun referall(): String {
        return if (recall_date!!.isEmpty()) {
            DateHelper.getCurrentDate()
        } else {
            "$recall_date $recall_time"
        }

    }

    fun address(): String {
        val municipalityName = municipalityName()
        val districtName = districtName()
        val wardNumber = wardNumber()

        return "$municipalityName-$wardNumber, $districtName"
    }

    fun fullName(): String {
        return "$first_name $middle_name $last_name"
    }

    fun wardNumber(): String {
        var wardNumberString = "-"
        try {
            val wardBox = ObjectBox.boxStore.boxFor(Ward::class.java)
            val ward = wardBox.query().equal(Ward_.remote_id, ward.toLong()).build().findFirst()
            wardNumberString = "${ward?.ward}"
        } catch (e: KotlinNullPointerException) {
            Log.d("Patient", e.printStackTrace().toString())
        }
        return wardNumberString

    }

    fun municipalityName(): String {
        var municipalityNameString = "-"
        try {
            val municipalityBox = ObjectBox.boxStore.boxFor(Municipality::class.java)
            val municipalityName = municipalityBox.query().equal(
                Municipality_.remote_id,
                municipality.toLong()
            ).build().findFirst()!!

            municipalityNameString = municipalityName.name
        } catch (e: KotlinNullPointerException) {
            Log.d("Patient", e.printStackTrace().toString())
        }
        return municipalityNameString

    }

    fun districtName(): String {
        var districtNameString = "-"
        try {
            val districtBox = ObjectBox.boxStore.boxFor(District::class.java)
            val districtName = districtBox.query().equal(
                District_.remote_id,
                district.toLong()
            ).build().findFirst()!!
            districtNameString = districtName.name
        } catch (e: KotlinNullPointerException) {
            Log.d("Patient", e.printStackTrace().toString())
        }
        return districtNameString
    }

    fun age(): String {
        Log.d("dob", dob)
        try {
            val year: Int = dob.substring(0, 4).toInt()
            val month: Int = dob.substring(5, 7).toInt()
            val day: Int = dob.substring(8, 10).toInt()
            val dob = Calendar.getInstance()
            val today = Calendar.getInstance()

            val nepaliCalender = DateConverter()

            val todayNepali = nepaliCalender.todayNepaliDate

            val yearToday = todayNepali.year
            val monthToday = todayNepali.month + 1
            val dayToday = todayNepali.day

            dob.set(year, month, day)
            today.set(yearToday, monthToday, dayToday)

            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            Log.d("AGE ", age.toString())
            Log.d(
                "Month",
                ((today.get(Calendar.DAY_OF_YEAR) - dob.get(Calendar.DAY_OF_YEAR)) / 30).toString()
            )
            return if (age <= 0) {
                val months = (today.get(Calendar.DAY_OF_YEAR) - dob.get(Calendar.DAY_OF_YEAR)) / 30
                "$months months"
            } else {
                "$age years"
            }
        } catch (e: StringIndexOutOfBoundsException) {
            Log.d("Patient", "Could not calculate date")
            return "-"
        }
    }
}