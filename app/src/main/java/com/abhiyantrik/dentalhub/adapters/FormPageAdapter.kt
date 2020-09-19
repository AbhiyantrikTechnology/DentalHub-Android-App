package com.abhiyantrik.dentalhub.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.abhiyantrik.dentalhub.fragments.HistoryFragment
import com.abhiyantrik.dentalhub.fragments.ReferralFragment
import com.abhiyantrik.dentalhub.fragments.ScreeningFragment
import com.abhiyantrik.dentalhub.fragments.TreatmentFragment

class FormPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                HistoryFragment()
            }
            1 -> {
                ScreeningFragment()
            }
            2 -> {
                TreatmentFragment()
            }
            3 -> {
                ReferralFragment()
            }
            else -> {
                HistoryFragment()
            }
        }
    }

    override fun getCount() = 4
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "History"
            }
            1 -> {
                "Screening"
            }
            2 -> {
                "Treatment"
            }
            3 -> {
                "Referral"
            }
            else -> {
                "History"
            }
        }
    }
}