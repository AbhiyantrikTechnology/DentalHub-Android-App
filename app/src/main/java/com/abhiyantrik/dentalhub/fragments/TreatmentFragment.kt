package com.abhiyantrik.dentalhub.fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.TreatmentFragmentCommunicator
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Treatment
import com.abhiyantrik.dentalhub.entities.Treatment_
import com.abhiyantrik.dentalhub.fragments.interfaces.TreatmentFormCommunicator
import io.objectbox.Box
import kotlinx.android.synthetic.main.single_patient.view.*

class TreatmentFragment : Fragment(), View.OnClickListener {


    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var treatmentFormCommunicator: TreatmentFormCommunicator

    private lateinit var encounterBox: Box<Encounter>
    private var encounter = Encounter()
    private lateinit var treatmentBox: Box<Treatment>
    private var treatment = Treatment()

    // Treatment Type buttons initialization
    private lateinit var btnSDF: Button
    private lateinit var btnSEAL: Button
    private lateinit var btnART: Button
    private lateinit var btnEXO: Button
    private lateinit var btnUNTR: Button
    private lateinit var btnSMART: Button


    private lateinit var checkBoxSDFWholeMouth: CheckBox
    private lateinit var tvSDFWholeMouth: TextView
    private lateinit var checkBoxFVApplied: CheckBox
    private lateinit var checkBoxTreatmentPlanComplete: CheckBox
    private lateinit var etNotes: EditText
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button


    private lateinit var btnId18: Button
    private lateinit var btnId17: Button
    private lateinit var btnId16: Button
    private lateinit var btnId15: Button
    private lateinit var btnId14: Button
    private lateinit var btnId13: Button
    private lateinit var btnId12: Button
    private lateinit var btnId11: Button
    private lateinit var btnId21: Button
    private lateinit var btnId22: Button
    private lateinit var btnId23: Button
    private lateinit var btnId24: Button
    private lateinit var btnId25: Button
    private lateinit var btnId26: Button
    private lateinit var btnId27: Button
    private lateinit var btnId28: Button


    private lateinit var btnId48: Button
    private lateinit var btnId47: Button
    private lateinit var btnId46: Button
    private lateinit var btnId45: Button
    private lateinit var btnId44: Button
    private lateinit var btnId43: Button
    private lateinit var btnId42: Button
    private lateinit var btnId41: Button
    private lateinit var btnId31: Button
    private lateinit var btnId32: Button
    private lateinit var btnId33: Button
    private lateinit var btnId34: Button
    private lateinit var btnId35: Button
    private lateinit var btnId36: Button
    private lateinit var btnId37: Button
    private lateinit var btnId38: Button

    private lateinit var btnId55: Button
    private lateinit var btnId54: Button
    private lateinit var btnId53: Button
    private lateinit var btnId52: Button
    private lateinit var btnId51: Button
    private lateinit var btnId61: Button
    private lateinit var btnId62: Button
    private lateinit var btnId63: Button
    private lateinit var btnId64: Button
    private lateinit var btnId65: Button

    private lateinit var btnId85: Button
    private lateinit var btnId84: Button
    private lateinit var btnId83: Button
    private lateinit var btnId82: Button
    private lateinit var btnId81: Button
    private lateinit var btnId71: Button
    private lateinit var btnId72: Button
    private lateinit var btnId73: Button
    private lateinit var btnId74: Button
    private lateinit var btnId75: Button

    private var selectedTreatment = ""
    private var defaultTreatment = "NONE"
    private var btnBackground: Drawable? = null
    private var defaultBackground: Drawable? = null
    private var btnDefaultBackground: Drawable? = null
    private var btnOnSelectTextColor: Int = 0
    private var teeth = Array(52) { "NONE" }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_treatment, container, false)

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)

        btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
        btnDefaultBackground =
            ResourcesCompat.getDrawable(resources, R.drawable.treatment_button_default_color, null)
        btnOnSelectTextColor =
            ResourcesCompat.getColor(resources, R.color.treatment_button_onselect_text, null)
        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)
        checkBoxSDFWholeMouth = view.findViewById(R.id.checkBoxSDFWholeMouth)
        tvSDFWholeMouth = view.findViewById(R.id.tvSDFWholeMouthTreatment)
        checkBoxFVApplied = view.findViewById(R.id.checkBoxFVApplied)
        checkBoxTreatmentPlanComplete = view.findViewById(R.id.checkBoxTreatmentPlanComplete)
        etNotes = view.findViewById(R.id.etNotes)

        // finding Treatment button from View
        btnSDF = view.findViewById(R.id.btnSDF)
        btnSEAL = view.findViewById(R.id.btnSEAL)
        btnART = view.findViewById(R.id.btnART)
        btnEXO = view.findViewById(R.id.btnEXO)
        btnUNTR = view.findViewById(R.id.btnUNTR)
        btnSMART = view.findViewById(R.id.btnSMART)

        btnId18 = view.findViewById(R.id.btnId18)
        btnId17 = view.findViewById(R.id.btnId17)
        btnId16 = view.findViewById(R.id.btnId16)
        btnId15 = view.findViewById(R.id.btnId15)
        btnId14 = view.findViewById(R.id.btnId14)
        btnId13 = view.findViewById(R.id.btnId13)
        btnId12 = view.findViewById(R.id.btnId12)
        btnId11 = view.findViewById(R.id.btnId11)

        btnId21 = view.findViewById(R.id.btnId21)
        btnId22 = view.findViewById(R.id.btnId22)
        btnId23 = view.findViewById(R.id.btnId23)
        btnId24 = view.findViewById(R.id.btnId24)
        btnId25 = view.findViewById(R.id.btnId25)
        btnId26 = view.findViewById(R.id.btnId26)
        btnId27 = view.findViewById(R.id.btnId27)
        btnId28 = view.findViewById(R.id.btnId28)

        btnId48 = view.findViewById(R.id.btnId48)
        btnId47 = view.findViewById(R.id.btnId47)
        btnId46 = view.findViewById(R.id.btnId46)
        btnId45 = view.findViewById(R.id.btnId45)
        btnId44 = view.findViewById(R.id.btnId44)
        btnId43 = view.findViewById(R.id.btnId43)
        btnId42 = view.findViewById(R.id.btnId42)
        btnId41 = view.findViewById(R.id.btnId41)

        btnId31 = view.findViewById(R.id.btnId31)
        btnId32 = view.findViewById(R.id.btnId32)
        btnId33 = view.findViewById(R.id.btnId33)
        btnId34 = view.findViewById(R.id.btnId34)
        btnId35 = view.findViewById(R.id.btnId35)
        btnId36 = view.findViewById(R.id.btnId36)
        btnId37 = view.findViewById(R.id.btnId37)
        btnId38 = view.findViewById(R.id.btnId38)

        // primary teeth
        btnId51 = view.findViewById(R.id.btnId51)
        btnId52 = view.findViewById(R.id.btnId52)
        btnId53 = view.findViewById(R.id.btnId53)
        btnId54 = view.findViewById(R.id.btnId54)
        btnId55 = view.findViewById(R.id.btnId55)

        btnId61 = view.findViewById(R.id.btnId61)
        btnId62 = view.findViewById(R.id.btnId62)
        btnId63 = view.findViewById(R.id.btnId63)
        btnId64 = view.findViewById(R.id.btnId64)
        btnId65 = view.findViewById(R.id.btnId65)

        btnId81 = view.findViewById(R.id.btnId81)
        btnId82 = view.findViewById(R.id.btnId82)
        btnId83 = view.findViewById(R.id.btnId83)
        btnId84 = view.findViewById(R.id.btnId84)
        btnId85 = view.findViewById(R.id.btnId85)

        btnId71 = view.findViewById(R.id.btnId71)
        btnId72 = view.findViewById(R.id.btnId72)
        btnId73 = view.findViewById(R.id.btnId73)
        btnId74 = view.findViewById(R.id.btnId74)
        btnId75 = view.findViewById(R.id.btnId75)

        // for Treatment Type button onClick event listener
        btnSDF.setOnClickListener(this)
        btnSEAL.setOnClickListener(this)
        btnART.setOnClickListener(this)
        btnEXO.setOnClickListener(this)
        btnUNTR.setOnClickListener(this)
        btnSMART.setOnClickListener(this)

        btnId11.setOnClickListener(this)
        btnId12.setOnClickListener(this)
        btnId13.setOnClickListener(this)
        btnId14.setOnClickListener(this)
        btnId15.setOnClickListener(this)
        btnId16.setOnClickListener(this)
        btnId17.setOnClickListener(this)
        btnId18.setOnClickListener(this)

        btnId21.setOnClickListener(this)
        btnId22.setOnClickListener(this)
        btnId23.setOnClickListener(this)
        btnId24.setOnClickListener(this)
        btnId25.setOnClickListener(this)
        btnId26.setOnClickListener(this)
        btnId27.setOnClickListener(this)
        btnId28.setOnClickListener(this)

        btnId31.setOnClickListener(this)
        btnId32.setOnClickListener(this)
        btnId33.setOnClickListener(this)
        btnId34.setOnClickListener(this)
        btnId35.setOnClickListener(this)
        btnId36.setOnClickListener(this)
        btnId37.setOnClickListener(this)
        btnId38.setOnClickListener(this)

        btnId41.setOnClickListener(this)
        btnId42.setOnClickListener(this)
        btnId43.setOnClickListener(this)
        btnId44.setOnClickListener(this)
        btnId45.setOnClickListener(this)
        btnId46.setOnClickListener(this)
        btnId47.setOnClickListener(this)
        btnId48.setOnClickListener(this)

        // primary teeth
        btnId51.setOnClickListener(this)
        btnId52.setOnClickListener(this)
        btnId53.setOnClickListener(this)
        btnId54.setOnClickListener(this)
        btnId55.setOnClickListener(this)

        btnId61.setOnClickListener(this)
        btnId62.setOnClickListener(this)
        btnId63.setOnClickListener(this)
        btnId64.setOnClickListener(this)
        btnId65.setOnClickListener(this)

        btnId81.setOnClickListener(this)
        btnId82.setOnClickListener(this)
        btnId83.setOnClickListener(this)
        btnId84.setOnClickListener(this)
        btnId85.setOnClickListener(this)

        btnId71.setOnClickListener(this)
        btnId72.setOnClickListener(this)
        btnId73.setOnClickListener(this)
        btnId74.setOnClickListener(this)
        btnId75.setOnClickListener(this)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        defaultBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        treatmentFormCommunicator = activity as TreatmentFormCommunicator

        setupUI(activity as Context)

        btnNext.setOnClickListener {
            val view = this.view!!.rootView
            if (view != null) {
                val imm = (activity as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            val notes = etNotes.text.toString()
            val sdfWholeMouth = checkBoxSDFWholeMouth.isChecked
            val fvApplied = checkBoxFVApplied.isChecked
            val treatmentPlanComplete = checkBoxTreatmentPlanComplete.isChecked

            Log.d("TPC", treatmentPlanComplete.toString())
            Log.d("notes", notes)
            Log.d("fvApplied", fvApplied.toString())
            Log.d("teeth", teeth.toString())

            treatmentFormCommunicator.updateTreatment(
                notes,
                sdfWholeMouth,
                fvApplied,
                treatmentPlanComplete,
                teeth
            )
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            val view = this.view!!.rootView
            if (view != null) {
                val imm = (activity as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            val notes = etNotes.text.toString()
            val sdfWholeMouth = checkBoxSDFWholeMouth.isChecked
            val fvApplied = checkBoxFVApplied.isChecked
            val treatmentPlanComplete = checkBoxTreatmentPlanComplete.isChecked

            Log.d("TPC", treatmentPlanComplete.toString())
            Log.d("notes", notes)
            Log.d("fvApplied", fvApplied.toString())
            Log.d("teeth", teeth.toString())

            treatmentFormCommunicator.updateTreatment(
                notes,
                sdfWholeMouth,
                fvApplied,
                treatmentPlanComplete,
                teeth
            )
            fragmentCommunicator.goBack()
        }
    }

    private fun setupUI(applicationContext: Context) {

        treatmentTypeBackgroundColor()

        val encounterId =
            DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()

        if (encounterId != 0.toLong()) {
            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

            // query to retrive the data of the encounter by id
            treatment = treatmentBox.query()
                .equal(Treatment_.encounterId, encounter.id)
                .orderDesc(Treatment_.id).build().findFirst()!!

            if (treatment.tooth11 != "NONE") setColorForButton(btnId11, treatment.tooth11, 7)
            if (treatment.tooth12 != "NONE") setColorForButton(btnId12, treatment.tooth12, 6)
            if (treatment.tooth13 != "NONE") setColorForButton(btnId13, treatment.tooth13, 5)
            if (treatment.tooth14 != "NONE") setColorForButton(btnId14, treatment.tooth14, 4)
            if (treatment.tooth15 != "NONE") setColorForButton(btnId15, treatment.tooth15, 3)
            if (treatment.tooth16 != "NONE") setColorForButton(btnId16, treatment.tooth16, 2)
            if (treatment.tooth17 != "NONE") setColorForButton(btnId17, treatment.tooth17, 1)
            if (treatment.tooth18 != "NONE") setColorForButton(btnId18, treatment.tooth18, 0)

            if (treatment.tooth21 != "NONE") setColorForButton(btnId21, treatment.tooth21, 8)
            if (treatment.tooth22 != "NONE") setColorForButton(btnId22, treatment.tooth22, 9)
            if (treatment.tooth23 != "NONE") setColorForButton(btnId23, treatment.tooth23, 10)
            if (treatment.tooth24 != "NONE") setColorForButton(btnId24, treatment.tooth24, 11)
            if (treatment.tooth25 != "NONE") setColorForButton(btnId25, treatment.tooth25, 12)
            if (treatment.tooth26 != "NONE") setColorForButton(btnId26, treatment.tooth26, 13)
            if (treatment.tooth27 != "NONE") setColorForButton(btnId27, treatment.tooth27, 14)
            if (treatment.tooth28 != "NONE") setColorForButton(btnId28, treatment.tooth28, 15)

            if (treatment.tooth31 != "NONE") setColorForButton(btnId31, treatment.tooth31, 24)
            if (treatment.tooth32 != "NONE") setColorForButton(btnId32, treatment.tooth32, 25)
            if (treatment.tooth33 != "NONE") setColorForButton(btnId33, treatment.tooth33, 26)
            if (treatment.tooth34 != "NONE") setColorForButton(btnId34, treatment.tooth34, 27)
            if (treatment.tooth35 != "NONE") setColorForButton(btnId35, treatment.tooth35, 28)
            if (treatment.tooth36 != "NONE") setColorForButton(btnId36, treatment.tooth36, 29)
            if (treatment.tooth37 != "NONE") setColorForButton(btnId37, treatment.tooth37, 30)
            if (treatment.tooth38 != "NONE") setColorForButton(btnId38, treatment.tooth38, 31)

            if (treatment.tooth41 != "NONE") setColorForButton(btnId41, treatment.tooth41, 23)
            if (treatment.tooth42 != "NONE") setColorForButton(btnId42, treatment.tooth42, 22)
            if (treatment.tooth43 != "NONE") setColorForButton(btnId43, treatment.tooth43, 21)
            if (treatment.tooth44 != "NONE") setColorForButton(btnId44, treatment.tooth44, 20)
            if (treatment.tooth45 != "NONE") setColorForButton(btnId45, treatment.tooth45, 19)
            if (treatment.tooth46 != "NONE") setColorForButton(btnId46, treatment.tooth46, 18)
            if (treatment.tooth47 != "NONE") setColorForButton(btnId47, treatment.tooth47, 17)
            if (treatment.tooth48 != "NONE") setColorForButton(btnId48, treatment.tooth48, 16)

            if (treatment.tooth51 != "NONE") setColorForButton(btnId51, treatment.tooth51, 32)
            if (treatment.tooth52 != "NONE") setColorForButton(btnId52, treatment.tooth52, 33)
            if (treatment.tooth53 != "NONE") setColorForButton(btnId53, treatment.tooth53, 34)
            if (treatment.tooth54 != "NONE") setColorForButton(btnId54, treatment.tooth54, 35)
            if (treatment.tooth55 != "NONE") setColorForButton(btnId55, treatment.tooth55, 36)

            if (treatment.tooth61 != "NONE") setColorForButton(btnId61, treatment.tooth61, 37)
            if (treatment.tooth62 != "NONE") setColorForButton(btnId62, treatment.tooth62, 38)
            if (treatment.tooth63 != "NONE") setColorForButton(btnId63, treatment.tooth63, 39)
            if (treatment.tooth64 != "NONE") setColorForButton(btnId64, treatment.tooth64, 40)
            if (treatment.tooth65 != "NONE") setColorForButton(btnId65, treatment.tooth65, 41)

            if (treatment.tooth71 != "NONE") setColorForButton(btnId71, treatment.tooth71, 47)
            if (treatment.tooth72 != "NONE") setColorForButton(btnId72, treatment.tooth72, 48)
            if (treatment.tooth73 != "NONE") setColorForButton(btnId73, treatment.tooth73, 49)
            if (treatment.tooth74 != "NONE") setColorForButton(btnId74, treatment.tooth74, 50)
            if (treatment.tooth75 != "NONE") setColorForButton(btnId75, treatment.tooth75, 51)

            if (treatment.tooth81 != "NONE") setColorForButton(btnId81, treatment.tooth81, 42)
            if (treatment.tooth82 != "NONE") setColorForButton(btnId82, treatment.tooth82, 43)
            if (treatment.tooth83 != "NONE") setColorForButton(btnId83, treatment.tooth83, 44)
            if (treatment.tooth84 != "NONE") setColorForButton(btnId84, treatment.tooth84, 45)
            if (treatment.tooth85 != "NONE") setColorForButton(btnId85, treatment.tooth85, 46)


            if (treatment.sdf_whole_mouth) checkBoxSDFWholeMouth.isChecked = true
            if (treatment.fv_applied) checkBoxFVApplied.isChecked = true
            if (treatment.treatment_plan_complete) checkBoxTreatmentPlanComplete.isChecked = true
            if (!treatment.notes.isNullOrEmpty()) etNotes.setText(treatment.notes)
        }
    }

    private fun setColorForButton(button: Button, buttonValue: String, buttonNumber: Int) {

        when (buttonValue) {
            "SDF" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf_applied, null)
                teeth[buttonNumber] = "SDF"
                button.setTextColor(btnOnSelectTextColor)
            }
            "SEAL" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal_applied, null)
                teeth[buttonNumber] = "SEAL"
                button.setTextColor(btnOnSelectTextColor)
            }
            "ART" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_art_applied, null)
                teeth[buttonNumber] = "ART"
                button.setTextColor(btnOnSelectTextColor)
            }
            "EXO" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo_applied, null)
                teeth[buttonNumber] = "EXO"
                button.setTextColor(btnOnSelectTextColor)
            }
            "UNTR" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr_applied, null)
                teeth[buttonNumber] = "UNTR"
                button.setTextColor(btnOnSelectTextColor)
            }
            "SMART" -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_smart_applied, null)
                teeth[buttonNumber] = "SMART"
                button.setTextColor(btnOnSelectTextColor)
            }
            else -> {
                button.background =
                    ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
                teeth[buttonNumber] = "NONE"
                button.setTextColor(btnOnSelectTextColor)
            }
        }

    }

    override fun onClick(v: View) {
        val buttons = arrayOf(
            R.id.btnId11,
            R.id.btnId12,
            R.id.btnId13,
            R.id.btnId14,
            R.id.btnId15,
            R.id.btnId16,
            R.id.btnId17,
            R.id.btnId18,
            R.id.btnId21,
            R.id.btnId22,
            R.id.btnId23,
            R.id.btnId24,
            R.id.btnId25,
            R.id.btnId26,
            R.id.btnId27,
            R.id.btnId28,
            R.id.btnId31,
            R.id.btnId32,
            R.id.btnId33,
            R.id.btnId34,
            R.id.btnId35,
            R.id.btnId36,
            R.id.btnId37,
            R.id.btnId38,
            R.id.btnId41,
            R.id.btnId42,
            R.id.btnId43,
            R.id.btnId44,
            R.id.btnId45,
            R.id.btnId46,
            R.id.btnId47,
            R.id.btnId48,

            R.id.btnId55,
            R.id.btnId54,
            R.id.btnId53,
            R.id.btnId52,
            R.id.btnId51,
            R.id.btnId65,
            R.id.btnId64,
            R.id.btnId63,
            R.id.btnId62,
            R.id.btnId61,
            R.id.btnId85,
            R.id.btnId84,
            R.id.btnId83,
            R.id.btnId82,
            R.id.btnId81,
            R.id.btnId75,
            R.id.btnId74,
            R.id.btnId73,
            R.id.btnId72,
            R.id.btnId71
        )
        // for Treatment Type OnClick event array
        val buttonsTreatmentType = arrayOf(
            R.id.btnSDF, R.id.btnSEAL, R.id.btnART, R.id.btnEXO, R.id.btnUNTR, R.id.btnSMART
        )

        // for Treatment Type color change according to its name
        if (buttonsTreatmentType.contains(v.id)) {

            when (v.id) {
                R.id.btnSDF -> {
                    selectedTreatment = "SDF"
                    btnBackground = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.treatment_sdf_applied,
                        null
                    )
                    treatmentTypeBackgroundColor()
                    btnSDF.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.treatment_button_onselect_text,
                            null
                        )
                    )
                    btnSDF.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf, null)
                    checkBoxSDFWholeMouth.visibility = View.VISIBLE
                    tvSDFWholeMouth.visibility = View.VISIBLE
                }

                R.id.btnSEAL -> {
                    selectedTreatment = "SEAL"
                    btnBackground = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.treatment_seal_applied,
                        null
                    )
                    treatmentTypeBackgroundColor()
                    btnSEAL.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.treatment_button_onselect_text,
                            null
                        )
                    )
                    btnSEAL.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal, null)
                    checkBoxSDFWholeMouth.visibility = View.GONE
                    tvSDFWholeMouth.visibility = View.GONE
                }

                R.id.btnART -> {
                    selectedTreatment = "ART"
                    btnBackground = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.treatment_art_applied,
                        null
                    )
                    treatmentTypeBackgroundColor()
                    btnART.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.treatment_button_onselect_text,
                            null
                        )
                    )
                    btnART.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.treatment_art, null)
                    checkBoxSDFWholeMouth.visibility = View.GONE
                    tvSDFWholeMouth.visibility = View.GONE
                }

                R.id.btnEXO -> {
                    selectedTreatment = "EXO"
                    btnBackground = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.treatment_exo_applied,
                        null
                    )
                    treatmentTypeBackgroundColor()
                    btnEXO.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.treatment_button_onselect_text,
                            null
                        )
                    )
                    btnEXO.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo, null)
                    checkBoxSDFWholeMouth.visibility = View.GONE
                    tvSDFWholeMouth.visibility = View.GONE
                }

                R.id.btnUNTR -> {
                    selectedTreatment = "UNTR"
                    btnBackground = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.treatment_untr_applied,
                        null
                    )
                    treatmentTypeBackgroundColor()
                    btnUNTR.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.treatment_button_onselect_text,
                            null
                        )
                    )
                    btnUNTR.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr, null)
                    checkBoxSDFWholeMouth.visibility = View.GONE
                    tvSDFWholeMouth.visibility = View.GONE
                }

                R.id.btnSMART -> {
                    selectedTreatment = "SMART"
                    btnBackground = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.treatment_smart_applied,
                        null
                    )
                    treatmentTypeBackgroundColor()
                    btnSMART.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.treatment_button_onselect_text,
                            null
                        )
                    )
                    btnSMART.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.treatment_smart_without_border, null)
                    checkBoxSDFWholeMouth.visibility = View.GONE
                    tvSDFWholeMouth.visibility = View.GONE
                }
            }
        }

        if (buttons.contains(v.id)) {
            if (v.background == btnBackground) {
                v.background = defaultBackground
            } else {
                v.background = btnBackground
            }
        }
        when (v.id) {
            //secondary teeth
            R.id.btnId18 -> toggleTreatment(0, btnId18)
            R.id.btnId17 -> toggleTreatment(1, btnId17)
            R.id.btnId16 -> toggleTreatment(2, btnId16)
            R.id.btnId15 -> toggleTreatment(3, btnId15)
            R.id.btnId14 -> toggleTreatment(4, btnId14)
            R.id.btnId13 -> toggleTreatment(5, btnId13)
            R.id.btnId12 -> toggleTreatment(6, btnId12)
            R.id.btnId11 -> toggleTreatment(7, btnId11)

            R.id.btnId21 -> toggleTreatment(8, btnId21)
            R.id.btnId22 -> toggleTreatment(9, btnId22)
            R.id.btnId23 -> toggleTreatment(10, btnId23)
            R.id.btnId24 -> toggleTreatment(11, btnId24)
            R.id.btnId25 -> toggleTreatment(12, btnId25)
            R.id.btnId26 -> toggleTreatment(13, btnId26)
            R.id.btnId27 -> toggleTreatment(14, btnId27)
            R.id.btnId28 -> toggleTreatment(15, btnId28)

            R.id.btnId48 -> toggleTreatment(16, btnId48)
            R.id.btnId47 -> toggleTreatment(17, btnId47)
            R.id.btnId46 -> toggleTreatment(18, btnId46)
            R.id.btnId45 -> toggleTreatment(19, btnId45)
            R.id.btnId44 -> toggleTreatment(20, btnId44)
            R.id.btnId43 -> toggleTreatment(21, btnId43)
            R.id.btnId42 -> toggleTreatment(22, btnId42)
            R.id.btnId41 -> toggleTreatment(23, btnId41)

            R.id.btnId31 -> toggleTreatment(24, btnId31)
            R.id.btnId32 -> toggleTreatment(25, btnId32)
            R.id.btnId33 -> toggleTreatment(26, btnId33)
            R.id.btnId34 -> toggleTreatment(27, btnId34)
            R.id.btnId35 -> toggleTreatment(28, btnId35)
            R.id.btnId36 -> toggleTreatment(29, btnId36)
            R.id.btnId37 -> toggleTreatment(30, btnId37)
            R.id.btnId38 -> toggleTreatment(31, btnId38)

            // primary teeth
            R.id.btnId51 -> toggleTreatment(32, btnId51)
            R.id.btnId52 -> toggleTreatment(33, btnId52)
            R.id.btnId53 -> toggleTreatment(34, btnId53)
            R.id.btnId54 -> toggleTreatment(35, btnId54)
            R.id.btnId55 -> toggleTreatment(36, btnId55)

            R.id.btnId61 -> toggleTreatment(37, btnId61)
            R.id.btnId62 -> toggleTreatment(38, btnId62)
            R.id.btnId63 -> toggleTreatment(39, btnId63)
            R.id.btnId64 -> toggleTreatment(40, btnId64)
            R.id.btnId65 -> toggleTreatment(41, btnId65)

            R.id.btnId81 -> toggleTreatment(42, btnId81)
            R.id.btnId82 -> toggleTreatment(43, btnId82)
            R.id.btnId83 -> toggleTreatment(44, btnId83)
            R.id.btnId84 -> toggleTreatment(45, btnId84)
            R.id.btnId85 -> toggleTreatment(46, btnId85)

            R.id.btnId71 -> toggleTreatment(47, btnId71)
            R.id.btnId72 -> toggleTreatment(48, btnId72)
            R.id.btnId73 -> toggleTreatment(49, btnId73)
            R.id.btnId74 -> toggleTreatment(50, btnId74)
            R.id.btnId75 -> toggleTreatment(51, btnId75)
        }
    }

    private fun toggleTreatment(teethNumber: Int, button: Button) {

        if (selectedTreatment != "") {
            if (teeth[teethNumber] == selectedTreatment) {
                teeth[teethNumber] = defaultTreatment
                button.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.treatment_button_default_text,
                        null
                    )
                )
                button.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.treatment_button_default,
                    null
                )
            } else {
                teeth[teethNumber] = selectedTreatment
                button.setTextColor(btnOnSelectTextColor)
            }
        } else {
            button.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.treatment_button_default_text,
                    null
                )
            )
        }
    }

    private fun treatmentTypeBackgroundColor() {
        btnSDF.background =
            ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf_outline, null)
        btnSEAL.background =
            ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal_outline, null)
        btnART.background =
            ResourcesCompat.getDrawable(resources, R.drawable.treatment_art_outline, null)
        btnEXO.background =
            ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo_outline, null)
        btnUNTR.background =
            ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr_outline, null)
        btnSMART.background =
            ResourcesCompat.getDrawable(resources, R.drawable.treatment_smart_outline, null)

        btnSDF.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.treatment_button_default_text,
                null
            )
        )
        btnSEAL.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.treatment_button_default_text,
                null
            )
        )
        btnART.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.treatment_button_default_text,
                null
            )
        )
        btnEXO.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.treatment_button_default_text,
                null
            )
        )
        btnUNTR.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.treatment_button_default_text,
                null
            )
        )
        btnSMART.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.treatment_button_default_text,
                null
            )
        )
    }
}
