package org.cyntho.fh.kotlin.kartoffelpuffer.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.data.ReservationHolder
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentAdminReservationDetailBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket

class AdminReservationDetailsFragment : Fragment() {

    private var _binding: FragmentAdminReservationDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminReservationDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val reservationID = arguments?.getInt("reservation_id") ?: return root
        val app = requireActivity().application as KartoffelApp

        var response: NetPacket? = null
        runBlocking {
            response = NetManager().send("/getReservationDetails", NetPacket(System.currentTimeMillis(), app.getUserToken(), reservationID, ""))
        }

        if (response == null){
            val diag = MaterialAlertDialogBuilder(requireContext())
            diag.background = ResourcesCompat.getDrawable(resources,R.drawable.roundedbutton,null)
            diag.setTitle(getString(R.string.alert_attention))
            diag.setMessage("Verbindung zu Server fehlgeschlagen")

            diag.setPositiveButton(android.R.string.ok) {_,_ ->
                findNavController().navigate(R.id.navigation_home)
            }
            return root
        }

        val wrapper = GsonBuilder().create().fromJson(response!!.data, ReservationHolder::class.java)
        println("Received wrapper: $wrapper")


        return root
    }

/*

        val reservationID = arguments?.getInt("reservation_id") ?: return root
        val app = requireActivity().application as KartoffelApp

        var response: NetPacket? = null
        runBlocking {
            response = NetManager().send("/getReservationDetails", NetPacket(System.currentTimeMillis(), app.getUserToken(), reservationID, ""))
        }

        if (response == null){
            val diag = AlertDialog.Builder(requireContext())
            diag.setTitle(getString(R.string.alert_attention))
            diag.setMessage("Verbindung zu Server fehlgeschlagen")

            diag.setPositiveButton(android.R.string.ok) {_,_ ->
                findNavController().navigate(R.id.navigation_home)
            }
            return root
        }

        val wrapper = GsonBuilder().create().fromJson(response!!.data, ReservationHolder::class.java)
        println("Received wrapper: $wrapper")

 */
}