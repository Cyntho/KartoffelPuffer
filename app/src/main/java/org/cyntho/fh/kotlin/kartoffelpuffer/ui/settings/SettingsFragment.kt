package org.cyntho.fh.kotlin.kartoffelpuffer.ui.settings

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.MainActivity
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.ActivityMainBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentSettingsBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.ui.setup.SetupFragment

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

        // Imprint button handler
        val btnImprint: Button = binding.btnImprint
        btnImprint.setOnClickListener {
            it.findNavController().navigate(R.id.navigation_layoutEditor)
        }

        // Text field: Username
        val txtUsername: EditText = binding.txtUsername


        txtUsername.addTextChangedListener {
            object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    println("New text: $s")
                }

                // useless, but must-have..
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        }

        // Button: Auth as Admin
        val btnAuth: Button = binding.btnAuthAsAdmin
        val txtCode = binding.txtAdminCode
        val app: KartoffelApp = activity!!.application as KartoffelApp

        btnAuth.setOnClickListener {
            val mgr = NetManager()
            val token = app.getUserToken()

            println("Attempting admin authorization using token[${app.getUserToken()}] and code [${txtCode.text.toString()}]")

            val isAdmin = runBlocking {
                mgr.login(token, txtCode.text.toString())
            }

            app.setAdmin(isAdmin)
            app.setAdminView(isAdmin)

            // ToDo: Handle displaying..
        }



        /* --------------- Initialize data from Settings --------------------------- */
        // See also: https://developer.android.com/training/data-storage/shared-preferences

        // Load saved settings
        val cfg = activity?.getSharedPreferences("config", Context.MODE_PRIVATE) ?: return root

        swDarkMode.isChecked = cfg.getBoolean(getString(R.string.cfgDarkMode), false)
        swNotification.isChecked = cfg.getBoolean(getString(R.string.cfgNotifications), false)

        txtUsername.setText(app.getUserName())

        return root
    }



    override fun onDestroyView() {

        val userField = binding.txtUsername
        if (!userField.text.equals("") && userField.text.length > 3){
            val app = (activity!!.application as KartoffelApp)
            app.setUserName(userField.text.toString())
            app.save()
        }

        super.onDestroyView()
        _binding = null
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