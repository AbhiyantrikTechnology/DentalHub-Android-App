package com.abhiyantrik.dentalhub.utils

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter

class AdapterHelper {
    companion object {
        fun createAdapter(context: Context, values: List<String>): SpinnerAdapter? {
            val dataAdapter =
                ArrayAdapter<String>(context, R.layout.simple_spinner_item, values.toMutableList())
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return dataAdapter
        }

        fun createAdapterWithInts(context: Context, values: List<Int>): SpinnerAdapter? {
            val dataAdapter =
                ArrayAdapter<Int>(context, R.layout.simple_spinner_item, values.toMutableList())
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            return dataAdapter
        }
    }
}
