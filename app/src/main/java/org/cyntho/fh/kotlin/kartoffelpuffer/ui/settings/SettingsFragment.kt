package org.cyntho.fh.kotlin.kartoffelpuffer.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.MainActivity
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.ActivityMainBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentSettingsBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import org.cyntho.fh.kotlin.kartoffelpuffer.ui.setup.SetupFragment
import org.w3c.dom.Text

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        /* --------------- Assign bindings and handler --------------------------- */

        val root: View = binding.root
        val app = requireActivity().application as KartoffelApp

        // Text field: Username
        val txtUsername: EditText = binding.txtUsername
        txtUsername.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (txtUsername.text.isNotEmpty())
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    updateUsername(txtUsername, app)
                    requireActivity().hideSoftKeyboard()
                    return@OnKeyListener true
                }
            false
        })

        // DarkMode Switch handler
        val swDarkMode: Switch = binding.switchDarkMode
        swDarkMode.setOnClickListener {
            updateUsername(txtUsername, app)
            onDarkModeToggledHandler(swDarkMode.isChecked)
            findNavController().navigate(R.id.navigation_settings)

        }

        // Notifications Switch handler
        val swNotification: Switch = binding.switchNotifications
        swNotification.setOnClickListener {
            updateUsername(txtUsername, app)
            onNotificationsToggleHandler(swNotification.isChecked)
            findNavController().navigate(R.id.navigation_settings)
        }

        // Imprint button handler
        val btnImprint: Button = binding.btnImprint
        btnImprint.setOnClickListener {
            it.findNavController().navigate(R.id.navigation_imprint)
        }

        /* --------------- Initialize data from Settings --------------------------- */
        // See also: https://developer.android.com/training/data-storage/shared-preferences

        // Load saved settings
        val cfg = activity?.getSharedPreferences("config", Context.MODE_PRIVATE) ?: return root

        swDarkMode.isChecked = cfg.getBoolean(getString(R.string.cfgDarkMode), false)
        swNotification.isChecked = cfg.getBoolean(getString(R.string.cfgNotifications), false)

        txtUsername.setText(app.getUserName())

        // ------------ Admin VIEW ------------

        val guestViewContainer = binding.settingsLoginContainer
        val adminViewContainer = binding.settingsLogoutContainer

        if (app.isAdmin() && app.displayAdminView()) {
            guestViewContainer.isInvisible = true
            adminViewContainer.isInvisible = false
        } else {
            guestViewContainer.isInvisible = false
            adminViewContainer.isInvisible = true
        }

        // Button: Auth as Admin
        val btnAuth: Button = binding.btnAuthAsAdmin
        val txtCode = binding.txtAdminCode

        btnAuth.setOnClickListener {
            val mgr = NetManager()
            val token = app.getUserToken()

            println("Attempting admin authorization using token[${app.getUserToken()}] and code [${txtCode.text.toString()}]")

            val isAdmin = runBlocking {
                mgr.login(token, txtCode.text.toString())
            }

            app.setAdmin(isAdmin)
            app.setAdminView(isAdmin)
            app.save()

            findNavController().navigate(R.id.navigation_settings)
        }


        val btnLogout = binding.btnSettingsLogout
        btnLogout.text = getString(R.string.btn_logout)
        btnLogout.setOnClickListener {

            app.setAdminView(false)
            app.save()

            findNavController().navigate(R.id.navigation_settings)
        }

        val btnEditLayout = binding.btnEditLayout
        btnEditLayout.setOnClickListener {
            if (app.isAdmin() && app.displayAdminView()) {
                findNavController().navigate(R.id.navigation_layoutEditor)
            }
        }



        return root
    }

    private fun updateUsername(
        txtUsername: EditText,
        app: KartoffelApp
    ) {
        println("Sending Username " + txtUsername.text.toString())
        var netpacket: NetPacket? = null
        runBlocking {
            netpacket = NetManager().send(
                "/setUsername",
                NetPacket(
                    System.currentTimeMillis(),
                    app.getUserToken(),
                    0,
                    txtUsername.text.toString()
                )
            )
            if (netpacket == null) {
                println("Server Response Empty")
            } else if (netpacket!!.type == 0) {
                app.setUserName(txtUsername.text.toString())
                app.save()
                println("Name change success")
            }
        }
    }


    override fun onDestroyView() {

        val userField = binding.txtUsername
        if (!userField.text.equals("") && userField.text.length > 3) {
            val app = (requireActivity().application as KartoffelApp)
            app.setUserName(userField.text.toString())
            app.save()
        }

        super.onDestroyView()
        _binding = null
    }


    private fun onDarkModeToggledHandler(mode: Boolean) {
        val cfg = activity?.getSharedPreferences("config", Context.MODE_PRIVATE) ?: return
        with(cfg.edit())
        {
            putBoolean(getString(R.string.cfgDarkMode), mode)
            apply()
        }
        if (mode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        println("DarkMode is now: ${if (mode) "on" else "off"}")
    }

    private fun onNotificationsToggleHandler(mode: Boolean) {
        val cfg = activity?.getSharedPreferences("config", Context.MODE_PRIVATE) ?: return
        with(cfg.edit())
        {
            putBoolean(getString(R.string.cfgNotifications), mode)
            apply()
        }
        println("Notifications are now: ${if (mode) "on" else "off"}")
    }

    fun Activity.hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}