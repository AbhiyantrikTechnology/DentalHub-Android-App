package com.abhiyantrik.dentalhub.models

class Municipality(
    var id: Int,
    var name: String,
    var category: String,
    var wards: List<Ward>
)