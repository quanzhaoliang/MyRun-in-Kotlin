package com.example.quanzhao_liang_myrun2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var listPreference: ListPreference
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)


        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val defaultValue = "-1" // Assign some meaningful default value
        val unitSelected = sharedPref.getString("unit", defaultValue)

//        val bundle = Bundle()
//        val fragment = HistoryFragment()
//        bundle.putString("unitSelected",unitSelected)
//        fragment.arguments = bundle
//
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.history_fragment, fragment)
//            .commit()


    }
}