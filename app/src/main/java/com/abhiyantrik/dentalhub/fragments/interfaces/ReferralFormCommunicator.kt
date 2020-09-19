package com.abhiyantrik.dentalhub.fragments.interfaces

interface ReferralFormCommunicator {
    fun updateReferral(
        noReferral: Boolean,
        healthPost: Boolean,
        hygienist: Boolean,
        dentist: Boolean,
        generalPhysician: Boolean,
        other: Boolean,
        otherDetails: String
    )

    fun updateRecall(
        recallDate: String,
        recallTime: String,
        selectedGeography: String,
        selectedActivity: String
    )

    fun updateRecallDate(
        recallDate: String,
        recallTime: String
    )
}