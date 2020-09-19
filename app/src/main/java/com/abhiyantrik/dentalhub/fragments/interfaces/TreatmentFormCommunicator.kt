package com.abhiyantrik.dentalhub.fragments.interfaces

interface TreatmentFormCommunicator {
    fun updateTreatment(
        notes: String,
        sdfWholeMouth: Boolean,
        fvApplied: Boolean,
        treatmentPlanComplete: Boolean,
        teeth: Array<String>
    )
}