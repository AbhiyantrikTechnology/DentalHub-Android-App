package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Activity : Parcelable {
    var id: String = ""
    var name: String = ""
}