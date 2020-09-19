package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Treatment : Parcelable {
    @Id
    var id: Long = 0
    var remote_id: String = ""
    var sdf_whole_mouth: Boolean = false
    var fv_applied: Boolean = false
    var treatment_plan_complete: Boolean = false
    var notes: String = ""

    var tooth18 = "NONE"
    var tooth17 = "NONE"
    var tooth16 = "NONE"
    var tooth15 = "NONE"
    var tooth14 = "NONE"
    var tooth13 = "NONE"
    var tooth12 = "NONE"
    var tooth11 = "NONE"

    var tooth21 = "NONE"
    var tooth22 = "NONE"
    var tooth23 = "NONE"
    var tooth24 = "NONE"
    var tooth25 = "NONE"
    var tooth26 = "NONE"
    var tooth27 = "NONE"
    var tooth28 = "NONE"

    var tooth48 = "NONE"
    var tooth47 = "NONE"
    var tooth46 = "NONE"
    var tooth45 = "NONE"
    var tooth44 = "NONE"
    var tooth43 = "NONE"
    var tooth42 = "NONE"
    var tooth41 = "NONE"

    var tooth31 = "NONE"
    var tooth32 = "NONE"
    var tooth33 = "NONE"
    var tooth34 = "NONE"
    var tooth35 = "NONE"
    var tooth36 = "NONE"
    var tooth37 = "NONE"
    var tooth38 = "NONE"


    var tooth51 = "NONE"
    var tooth52 = "NONE"
    var tooth53 = "NONE"
    var tooth54 = "NONE"
    var tooth55 = "NONE"

    var tooth61 = "NONE"
    var tooth62 = "NONE"
    var tooth63 = "NONE"
    var tooth64 = "NONE"
    var tooth65 = "NONE"

    var tooth81 = "NONE"
    var tooth82 = "NONE"
    var tooth83 = "NONE"
    var tooth84 = "NONE"
    var tooth85 = "NONE"

    var tooth71 = "NONE"
    var tooth72 = "NONE"
    var tooth73 = "NONE"
    var tooth74 = "NONE"
    var tooth75 = "NONE"

    var encounter: ToOne<Encounter>? = null
    var uploaded: Boolean = false
    var updated: Boolean = false
}
