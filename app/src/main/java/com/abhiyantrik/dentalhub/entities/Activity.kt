package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Activity : Parcelable {
    @Id
    var id: Long = 0
    var remote_id: String = ""
    var name: String = ""
}