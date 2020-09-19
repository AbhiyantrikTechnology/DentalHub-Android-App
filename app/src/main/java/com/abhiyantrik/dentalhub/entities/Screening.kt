package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Screening : Parcelable {
    @Id
    var id: Long = 0
    var remote_id: String = ""
    var encounter: ToOne<Encounter>? = null
    var carries_risk: String = "Low"
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
    var uploaded: Boolean = false
    var updated: Boolean = false
}
