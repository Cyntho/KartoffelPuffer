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
        val txtDate = binding.txtDate
        val txtTime = binding.txtTime
        val btnSetAmount = binding.btnChangeAmount

        txtDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(currentAttempt.time)
        txtTime.text = SimpleDateFormat("HH:mm", Locale.GERMAN).format(currentAttempt.time)
        btnSetAmount.text = String.format("%s / %s", currentAttempt.pplCurrent, currentAttempt.pplMax)


        // dbg
        val allergies = app.getAllergyList()
        println("Allergies:")
        for (allergy in allergies){
            println("\t$allergy")
        }

        println("Dishes:")
        for (dish in app.getDishList()){
            println("\t$dish")
        }








        return root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }
}