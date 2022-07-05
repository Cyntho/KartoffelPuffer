package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationDetailBinding

class ReservationDetail : Fragment() {

    private var _binding: FragmentReservationDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentReservationDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }
}