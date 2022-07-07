package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

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
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationInfoBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ReservationInfo : Fragment() {

    private var _binding: FragmentReservationInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReservationInfoBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val reservationID = arguments?.getInt("reservation_id")
        val app = requireActivity().application as KartoffelApp

        if (reservationID == null){
            println("Unhandled reservation id")
            return root
        }

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
            diag.show()
            return root
        }

        val wrapper = GsonBuilder().create().fromJson(response!!.data, ReservationHolder::class.java)
        println("Received wrapper: $wrapper")

        val txtDate = binding.reservationInfoDate
        val txtTime = binding.reservationInfoTime
        val txtPeople = binding.reservationInfoSize
        val txtListe = binding.reservationInfoDisc

        val btnDelete = binding.reservationInfoDelete
        val btnBack = binding.reservationInfoBack

        txtDate.text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN).format(Timestamp(wrapper.time))
        txtTime.text = SimpleDateFormat("HH:mm", Locale.GERMAN).format(Timestamp(wrapper.time))
        txtPeople.text = String.format("%d / %d", wrapper.pplCurrent, wrapper.pplMax)

        var tmp = ""
        for (entry in wrapper.dishes!!){
            tmp = "${entry.value}x ${app.getDishById(entry.key)} \n$tmp"
        }

        txtListe.text = tmp

        btnBack.setOnClickListener {
            if (app.isAdmin() && app.displayAdminView()){
                findNavController().navigate(R.id.navigation_admin_reservation)
            } else {
                findNavController().navigate(R.id.navigation_reservations)
            }
        }



        btnDelete.setOnClickListener {
            val diag = MaterialAlertDialogBuilder(requireContext())
            diag.background = ResourcesCompat.getDrawable(resources,R.drawable.roundedbutton,null)
            diag.setTitle("Wirklich löschen?")
            diag.setMessage("Wollen Sie diese Reservierung wirklich löschen?")
            diag.setPositiveButton("Ja, wirklich löschen") {_, _ ->
                var deleteRequest: NetPacket? = null
                runBlocking {
                    deleteRequest = NetManager().send("/deleteReservation", NetPacket(
                        System.currentTimeMillis(),
                        app.getUserToken(),
                        reservationID,
                        ""
                    ))
                }

                val feedbackDialog = MaterialAlertDialogBuilder(requireContext())
                feedbackDialog.background = ResourcesCompat.getDrawable(resources,R.drawable.roundedbutton,null)

                if (deleteRequest != null && deleteRequest!!.type == 0){
                    feedbackDialog.setTitle(getString(R.string.alert_success))
                    feedbackDialog.setMessage("Die Reservierung wurde gelöscht!")
                    feedbackDialog.setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                        findNavController().navigate(R.id.navigation_reservations)
                    }
                } else if (deleteRequest != null) {
                    feedbackDialog.setTitle(getString(R.string.alert_failure))
                    feedbackDialog.setMessage("Sie haben nicht die Berechtigung, diese Reservierung zu löschen!")
                    feedbackDialog.setPositiveButton(getString(android.R.string.ok)) { _, _, ->
                        findNavController().navigate(R.id.navigation_home)
                    }
                } else {
                    feedbackDialog.setTitle(getString(R.string.connection_refused))
                    feedbackDialog.setMessage("Die Verbindung zum Server wurde unterbrochen.\nBitte versuchen Sie es später erneut.")
                    feedbackDialog.setPositiveButton(getString(android.R.string.ok)) { _, _, -> }
                }
                feedbackDialog.show()
            }
            diag.setNegativeButton("Abbrechen") {_, _ -> }
            diag.show()
        }





















        return root
    }
}