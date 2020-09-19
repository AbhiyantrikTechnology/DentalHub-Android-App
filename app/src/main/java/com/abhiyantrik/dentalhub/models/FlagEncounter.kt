package com.abhiyantrik.dentalhub.models

data class FlagEncounter(
    val id: String,
    val encounter_remote_id: String,
    val patient_name: String,
    val encounter_type: String,
    val flag_type: String,
    val flag_status: String,
    val description: String
)