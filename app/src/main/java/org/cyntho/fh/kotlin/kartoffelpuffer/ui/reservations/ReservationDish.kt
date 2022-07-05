package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationDishBinding

class ReservationDish : Fragment() {

    private var _binding: FragmentReservationDishBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentReservationDishBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
}