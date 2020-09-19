package com.abhiyantrik.dentalhub.models

data class Geography(
    var id: Int,
    var location: String,
    var district: String,
    var municipality_name: String,
    var ward: Int,
    var name: String,
    var status: Boolean
)
