package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class User: Parcelable{
    @Id var id: Long = 0
    var remote_id: String = ""
    var first_name: String = ""
    var middle_name: String =""
    var last_name: String = ""

    fun full_name():String{
        return "$first_name $middle_name $last_name"
    }
}