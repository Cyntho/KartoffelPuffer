package org.cyntho.fh.kotlin.kartoffelpuffer.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        val root: View = binding.root

        val textView: TextView = binding.txSettingsTitle
        val btnTest: Button = binding.btnImprint


        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })


        // Assign button?
        btnTest.setOnClickListener {
            onButtonClickedHandler()

            val p = Intent(context, SettingsPopupWindow::class.java)

            p.putExtra("popup_title", "Der Titel!")
            p.putExtra("popup_text", "Der Inhalt der Meldung")
            p.putExtra("popup_button_text", "Okay?")
            startActivity(p)

        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onButtonClickedHandler(){
        println("Das ist ein Test!")
    }
}