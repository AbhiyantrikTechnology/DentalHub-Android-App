package com.abhiyantrik.dentalhub.fragments.interfaces

interface ScreeningFormCommunicator {
    fun updateScreening(
        carriesRisk: String,
        decayedPrimaryTeeth: String,
        decayedPermanentTeeth: String,
        cavityPermanentTooth: Boolean,
        cavityPermanentAnterior: Boolean,
        reversiblePulpitis: Boolean,
        needARTFilling: Boolean,
        needSealant: Boolean,
        needSDF: Boolean,
        needExtraction: Boolean,
        activeInfection: Boolean
    )
}