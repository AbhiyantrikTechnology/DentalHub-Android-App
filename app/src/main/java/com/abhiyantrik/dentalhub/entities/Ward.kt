package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Ward : Parcelable {

    @Id
    var id: Long = 0
    var remote_id: Int = 0
    var ward: Int = 0
    var name: String = ""
    var municipality: ToOne<Municipality>? = null
}