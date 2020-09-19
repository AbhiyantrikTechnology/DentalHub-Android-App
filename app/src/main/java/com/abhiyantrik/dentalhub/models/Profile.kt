package com.abhiyantrik.dentalhub.models

data class Profile(
    var id: String,
    var first_name: String,
    var middle_name: String,
    var last_name: String,
    var image: String
) {
    fun fullName(): String {
        return "$first_name $middle_name $last_name"
    }
}