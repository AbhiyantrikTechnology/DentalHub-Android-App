package com.abhiyantrik.dentalhub.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.models.Geography

class GeographyAdapter(
    var context: Context,
    private var data: List<Geography>,
    listener: GeographyClickListener
) :
    RecyclerView.Adapter<GeographyAdapter.GeographyViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var geographyClickListener: GeographyClickListener = listener

    interface GeographyClickListener {
        fun onGeographyClick(geography: Geography)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeographyViewHolder {
        val view = inflater.inflate(R.layout.single_geography, parent, false)
        return GeographyViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeographyViewHolder, position: Int) {
        val selectedGeography: Geography = data[position]
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true

        holder.itemView.setOnClickListener {
            Log.d("EncounterAdapter", "itemView clicked")
            DentalApp.geography_id = selectedGeography.id
            geographyClickListener.onGeographyClick(selectedGeography)
        }
        holder.bindEncounter(selectedGeography)
    }

    override fun getItemCount() = data.size

    inner class GeographyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private var tvLocation: TextView = itemView.findViewById(R.id.tvLocation)


        fun bindEncounter(geography: Geography) {
            tvLocation.text = geography.name
        }


    }
}