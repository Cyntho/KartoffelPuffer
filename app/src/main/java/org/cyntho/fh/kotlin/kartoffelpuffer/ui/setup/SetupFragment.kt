package org.cyntho.fh.kotlin.kartoffelpuffer.ui.setup

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentSetupBinding

class SetupFragment : Fragment() {

    private lateinit var setupViewModel: SetupViewModel
    private var _binding: FragmentSetupBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {

        setupViewModel =
            ViewModelProvider(this).get(SetupViewModel::class.java)
        _binding = FragmentSetupBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val btnConfirm = binding.btnSetup
        val txtUsername = binding.editTextSetupUsername
        btnConfirm.setOnClickListener {
            if (txtUsername.text.equals("")){

                val builder = android.app.AlertDialog.Builder(context)
                builder.setTitle(R.string.title_setup)
                builder.setMessage("Benutzername darf nicht leer sein")
                builder.setPositiveButton(android.R.string.ok) {
                        _, _ -> println("Pressed ok")
                }

                builder.show()
            }
        }





        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}