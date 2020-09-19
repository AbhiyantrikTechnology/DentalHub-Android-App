package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class District : Parcelable {
    @Id
    var id: Long = 0
    var remote_id: Int = 0
    var name: String = ""
    var municipalities: ToMany<Municipality>? = null
}
