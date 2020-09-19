package com.abhiyantrik.dentalhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.models.FlagEncounter
import kotlinx.android.synthetic.main.single_flaged_encounter.view.*

class FlagAdapter(
    val context: Context,
    val data: List<FlagEncounter>,
    listner: FlagClickListner
    ) : RecyclerView.Adapter<FlagAdapter.FlagAdapterViewHolder>() {

    private var flagClickListner: FlagClickListner = listner

    interface FlagClickListner {
        fun onEditButtonClick(flagEncounter: FlagEncounter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.single_flaged_encounter, parent, false)
        return FlagAdapterViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FlagAdapterViewHolder, position: Int) {
        val flagEncounter: FlagEncounter = data[position]
        holder.itemView.apply {
            tvPatientName.text = flagEncounter.patient_name
            tvEncounterType.text = flagEncounter.encounter_type
            val input = flagEncounter.flag_type
            val capitalizedWord = input.substring(0, 1).toUpperCase() + input.substring(1);
            tvFlagType.text = capitalizedWord
            tvFlagEncounterStatus.text = flagEncounter.flag_status
            tvFlagReasonDescription.text = flagEncounter.description
            ibEdit.visibility = View.INVISIBLE
            when (flagEncounter.flag_status) {
                "approved" -> {
                    ibEdit.visibility = View.VISIBLE
                    tvFlagEncounterStatus.setTextColor(resources.getColor(R.color.flag_approved_green))
                }
                "pending" -> {
                    tvFlagEncounterStatus.setTextColor(resources.getColor(R.color.flag_pending_yellow))
                }
                "deleted" -> {
                    tvFlagEncounterStatus.setTextColor(resources.getColor(R.color.flag_expired_red))
                }
            }

            ibEdit.setOnClickListener {
                flagClickListner.onEditButtonClick(flagEncounter)
            }
        }
    }

    inner class FlagAdapterViewHolder(viewHolder: View) : RecyclerView.ViewHolder(viewHolder)
}
