package com.abhiyantrik.dentalhub.models

data class Location(
    var latitude: String,
    var longitude: String
) {
    override fun toString(): String {
        return "Location {$latitude}, {$longitude}"
    }
}