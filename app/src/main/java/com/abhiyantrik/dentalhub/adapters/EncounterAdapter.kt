package com.abhiyantrik.dentalhub.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.AddEncounterActivity
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.User
import com.abhiyantrik.dentalhub.entities.User_
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.workers.DownloadUsersWorker
import io.objectbox.Box
import kotlinx.android.synthetic.main.single_encounter.view.*


class EncounterAdapter(
    var context: Context,
    var patient: Patient,
    private var data: List<Encounter>,
    listener: EncounterClickListener
) :
    RecyclerView.Adapter<EncounterAdapter.EncounterViewHolder>() {

    private lateinit var userBox: Box<User>
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var encounterClickListener: EncounterClickListener = listener

    interface EncounterClickListener {
        fun onEncounterClick(encounter: Encounter)
        fun onModificationFlagClick(encounterId: String, isModifyable: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncounterViewHolder {
        val view = inflater.inflate(R.layout.single_encounter, parent, false)
        return EncounterViewHolder(view)
    }

    override fun onBindViewHolder(holder: EncounterViewHolder, position: Int) {
        val encounterItem: Encounter = data[position]
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
        holder.itemView.ibEdit.setOnClickListener {
            Log.d("EncounterAdapter", "do the edit operation")
            val addEncounterActivityIntent = Intent(context, AddEncounterActivity::class.java)
            addEncounterActivityIntent.putExtra("ENCOUNTER_ID", encounterItem.id)
            addEncounterActivityIntent.putExtra("PATIENT_ID", patient.id)
            addEncounterActivityIntent.putExtra("MODIFY_DELETE", "0".toLong())
            context.startActivity(addEncounterActivityIntent)
        }
        holder.itemView.setOnClickListener {
            Log.d("EncounterAdapter", "itemView clicked")
            encounterClickListener.onEncounterClick(encounterItem)
        }
        holder.itemView.ibModificationFlag.setOnClickListener {
            encounterClickListener.onModificationFlagClick(encounterItem.remote_id, encounterItem.isEditable())
        }
        holder.bindEncounter(encounterItem)
    }

    override fun getItemCount() = data.size

    inner class EncounterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private var tvEncounterName: TextView = itemView.findViewById(R.id.tvEncounterName)
        private var tvEncounterDate: TextView = itemView.findViewById(R.id.tvEncounterDate)
        private var tvAuthorName: TextView = itemView.findViewById(R.id.tvAuthorName)
        private var ivEncounterSyncStatus: ImageView = itemView.findViewById(R.id.ivEncounterSyncStatus)
        private var ibEdit: ImageButton = itemView.findViewById(R.id.ibEdit)

        fun bindEncounter(encounter: Encounter) {
            userBox = ObjectBox.boxStore.boxFor(User::class.java)
            try {
                val author = userBox.query().equal(User_.remote_id, encounter.author).build().findFirst()!!
                tvAuthorName.text = author.full_name()
            } catch (e: NullPointerException) {
                Log.d("EncounterAdapater", "Author not found.")
                val downloadUsersWorkRequest = OneTimeWorkRequestBuilder<DownloadUsersWorker>()
                    .setConstraints(DentalApp.downloadConstraints)
                    .build()
                WorkManager.getInstance(context).enqueue(downloadUsersWorkRequest)
                tvAuthorName.text = "--"
            }
            val encounterType: String = context.getString(R.string.other_problem)
            if (encounter.encounter_type == encounterType) {
                tvEncounterName.text = encounter.encounter_type + " - " + encounter.other_problem
            } else {
                tvEncounterName.text = encounter.encounter_type
            }
            tvEncounterDate.text = DateHelper.formatNepaliDate(context, encounter.created_at)

            if (!encounter.uploaded) {
                ivEncounterSyncStatus.setColorFilter(context.resources.getColor(R.color.colorSDF))
            } else {
                ivEncounterSyncStatus.setColorFilter(context.resources.getColor(R.color.green_700))
            }

            if (encounter.isEditable()) {
                ibEdit.visibility = View.VISIBLE
            } else {
                ibEdit.visibility = View.INVISIBLE
            }
        }
    }
}
