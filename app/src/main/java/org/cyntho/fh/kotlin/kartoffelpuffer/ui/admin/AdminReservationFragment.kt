package org.cyntho.fh.kotlin.kartoffelpuffer.ui.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentAdminReservationBinding
import java.util.Calendar

class AdminReservationFragment : Fragment() {


    private var _binding: FragmentAdminReservationBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private var tYear = calendar.get(Calendar.YEAR)
    private var tMonth = calendar.get(Calendar.MONTH)
    private var tDay = calendar.get(Calendar.DAY_OF_MONTH)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentAdminReservationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val btnDatePicker = binding.btnDate
        btnDatePicker.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    run {

                        btnDatePicker.text = String.format("%d / %d / %d", dayOfMonth, month + 1, year)

                        tYear = year
                        tMonth = month
                        tDay = dayOfMonth
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - (1000 * 60)
            calendar.add(Calendar.MONTH, 1)
            datePicker.datePicker.maxDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, -1)
        }




        return root
    }

    private fun requestUpdate(){

    }
}