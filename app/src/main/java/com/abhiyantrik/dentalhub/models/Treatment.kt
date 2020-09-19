package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Treatment : Parcelable {
    var id: String = ""
    var sdf_whole_mouth: Boolean = false
    var fv_applied: Boolean = false
    var treatment_plan_complete: Boolean = false
    var notes: String = ""

    var tooth18: String = "NONE"
    var tooth17: String = "NONE"
    var tooth16: String = "NONE"
    var tooth15: String = "NONE"
    var tooth14: String = "NONE"
    var tooth13: String = "NONE"
    var tooth12: String = "NONE"
    var tooth11: String = "NONE"

    var tooth21: String = "NONE"
    var tooth22: String = "NONE"
    var tooth23: String = "NONE"
    var tooth24: String = "NONE"
    var tooth25: String = "NONE"
    var tooth26: String = "NONE"
    var tooth27: String = "NONE"
    var tooth28: String = "NONE"

    var tooth48: String = "NONE"
    var tooth47: String = "NONE"
    var tooth46: String = "NONE"
    var tooth45: String = "NONE"
    var tooth44: String = "NONE"
    var tooth43: String = "NONE"
    var tooth42: String = "NONE"
    var tooth41: String = "NONE"

    var tooth31: String = "NONE"
    var tooth32: String = "NONE"
    var tooth33: String = "NONE"
    var tooth34: String = "NONE"
    var tooth35: String = "NONE"
    var tooth36: String = "NONE"
    var tooth37: String = "NONE"
    var tooth38: String = "NONE"


    var tooth51: String = "NONE"
    var tooth52: String = "NONE"
    var tooth53: String = "NONE"
    var tooth54: String = "NONE"
    var tooth55: String = "NONE"

    var tooth61: String = "NONE"
    var tooth62: String = "NONE"
    var tooth63: String = "NONE"
    var tooth64: String = "NONE"
    var tooth65: String = "NONE"

    var tooth81: String = "NONE"
    var tooth82: String = "NONE"
    var tooth83: String = "NONE"
    var tooth84: String = "NONE"
    var tooth85: String = "NONE"

    var tooth71: String = "NONE"
    var tooth72: String = "NONE"
    var tooth73: String = "NONE"
    var tooth74: String = "NONE"
    var tooth75: String = "NONE"
}