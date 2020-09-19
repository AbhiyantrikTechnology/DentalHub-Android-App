package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Recall : Parcelable {
    @Id
    var id: Long = 0
    var date: String = ""
    var time: String = ""
    var geography: String = ""
    var activity: String = ""
    var patient: ToOne<Patient>? = null
    var encounter: ToOne<Encounter>? = null
}