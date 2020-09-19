package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class History : Parcelable {
    var id: String = ""
    var blood_disorder: Boolean = false
    var diabetes: Boolean = false
    var liver_problem: Boolean = false
    var rheumatic_fever: Boolean = false
    var seizuers_or_epilepsy: Boolean = false
    var hepatitis_b_or_c: Boolean = false
    var hiv: Boolean = false
    var other: String = ""
    var high_blood_pressure: Boolean = false
    var low_blood_pressure: Boolean = false
    var thyroid_disorder: Boolean = false
    var no_underlying_medical_condition: Boolean = false
    var medications: String = ""
    var not_taking_any_medications: Boolean = false
    var no_allergies: Boolean = false
    var allergies: String = ""
}
