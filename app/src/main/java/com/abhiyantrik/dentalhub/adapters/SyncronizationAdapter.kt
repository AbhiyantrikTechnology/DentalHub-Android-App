package com.abhiyantrik.dentalhub.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.models.Sync
import kotlinx.android.synthetic.main.single_syncronization.view.*

class SyncronizationAdapter(
    val data: List<Sync>
) : RecyclerView.Adapter<SyncronizationAdapter.SyncViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_syncronization, parent, false)
        return SyncViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SyncViewHolder, position: Int) {
        val syncItem = data[position]
        holder.itemView.apply {
            tvPatientName.text = syncItem.patient_name
            tvEncounterType.text = syncItem.encounter_type
            tvCreatedDate.text = syncItem.created_date.substring(0, 10)
            if (!syncItem.uploaded) {
                ivSyncStatus.setColorFilter(resources.getColor(R.color.colorSDF))
            } else {
                ivSyncStatus.setColorFilter(resources.getColor(R.color.green_700))
            }
        }
    }

    inner class SyncViewHolder(viewHolder: View) : RecyclerView.ViewHolder(viewHolder)
}