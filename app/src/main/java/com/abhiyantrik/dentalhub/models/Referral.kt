package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Referral : Parcelable {
    var id: String = ""
    var no_referral: Boolean = false
    var health_post: Boolean = false
    var hygienist: Boolean = false
    var dentist: Boolean = false
    var general_physician: Boolean = false
    var other_details: String = ""
    var other: Boolean = false
//    var date: String = ""
//    var time: String = ""
}
