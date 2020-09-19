package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    var id: String,
    var first_name: String,
    var middle_name: String,
    var last_name: String
): Parcelable