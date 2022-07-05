package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationsBinding
import java.text.SimpleDateFormat
import java.util.*

class ReservationsFragment : Fragment() {

    private lateinit var reservationsViewModel: ReservationsViewModel
    private var _binding: FragmentReservationsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        reservationsViewModel =
            ViewModelProvider(this).get(ReservationsViewModel::class.java)

        _binding = FragmentReservationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Load app and current reservation. Return on failure
        val app: KartoffelApp = activity!!.application as KartoffelApp ?: return root
        val currentAttempt = app.getCurrentReservation()?: return root

        // Set references





        return root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }
}