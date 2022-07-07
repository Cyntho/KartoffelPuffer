package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationInfoBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationsBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager

class ReservationInfo : Fragment() {

    private var _binding: FragmentReservationInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReservationInfoBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val dishID = arguments?.getInt("reservation_id") ?: return root

        //val response = NetManager().send("/reservation")


        return root
    }
}