package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Encounter : Parcelable {
    var id: String = ""
    var encounter_type: String = ""
    var other_problem: String? = ""
    var created_at: String = ""
    var updated_at: String? = ""
    var uploaded: Boolean = false

    var ward_id: Int = 0
    var activity_area: String = ""

    var author: String = ""
    var updated_by: String = ""

    var history: History? = null
    var screening: Screening? = null
    var treatment: Treatment? = null
    var referral: Referral? = null
}
