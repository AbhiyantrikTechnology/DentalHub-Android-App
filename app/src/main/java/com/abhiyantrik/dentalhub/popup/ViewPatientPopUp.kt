package com.abhiyantrik.dentalhub.popup

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.DisplayMetrics
import com.abhiyantrik.dentalhub.R

class ViewPatientPopUp : Activity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.popup_view_patient)

        val dm = DisplayMetrics()
    }
}