package org.cyntho.fh.kotlin.kartoffelpuffer.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {

        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        /* --------------- Assign bindings and handler --------------------------- */

        val root: View = binding.root
        val textView: TextView = binding.txSettingsTitle
        val btnTest: Button = binding.btnImprint

        // DarkMode Switch handler
        val swDarkMode: Switch = binding.switchDarkMode
        swDarkMode.setOnClickListener {
            onDarkModeToggledHandler(swDarkMode.isChecked)
        }

        // Notifications Switch handler
        val swNotification: Switch = binding.switchNotifications
        swNotification.setOnClickListener {
            onNotificationsToggleHandler(swNotification.isChecked)
        }

        btnTest.setOnClickListener {onButtonClickedHandler()}



        /* --------------- Initialize data from Settings --------------------------- */
        // See also: https://developer.android.com/training/data-storage/shared-preferences

        // Load saved settings
        val cfg = activity?.getPreferences(Context.MODE_PRIVATE) ?: return root

        swDarkMode.isChecked = cfg.getBoolean(getString(R.string.cfgDarkMode), false)
        swNotification.isChecked = cfg.getBoolean(getString(R.string.cfgNotifications), false)


        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onButtonClickedHandler(){
        println("Das ist ein Test!")
    }

    private fun onDarkModeToggledHandler(mode: Boolean){
        val cfg = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (cfg.edit())
        {
            putBoolean(getString(R.string.cfgDarkMode), mode)
            apply()
        }
        println("DarkMode is now: ${if (mode) "on" else "off"}")
    }

    private fun onNotificationsToggleHandler(mode: Boolean) {
        val cfg = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (cfg.edit())
        {
            putBoolean(getString(R.string.cfgNotifications), mode)
            apply()
        }
        println("Notifications are now: ${if (mode) "on" else "off"}")
    }
}