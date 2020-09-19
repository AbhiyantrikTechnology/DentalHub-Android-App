package com.abhiyantrik.dentalhub.models

class District(
    var id: Int,
    var name: String,
    var municipalities: List<Municipality>
)