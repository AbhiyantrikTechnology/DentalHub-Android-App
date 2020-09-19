package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Screening : Parcelable {
    var id: String = ""
    var carries_risk: String = ""
    var decayed_primary_teeth: Int = 0
    var decayed_permanent_teeth: Int = 0
    var cavity_permanent_anterior_teeth: Boolean = false
    var cavity_permanent_posterior_teeth: Boolean = false
    var reversible_pulpitis: Boolean = false
    var need_art_filling: Boolean = false
    var need_sealant: Boolean = false
    var need_sdf: Boolean = false
    var need_extraction: Boolean = false
    var active_infection: Boolean = false
}
