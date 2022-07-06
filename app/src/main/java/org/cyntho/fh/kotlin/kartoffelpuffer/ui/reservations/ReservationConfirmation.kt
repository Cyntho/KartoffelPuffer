package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationConfirmationBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import java.text.SimpleDateFormat
import java.util.*

class ReservationConfirmation : Fragment() {

    private var _binding: FragmentReservationConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentReservationConfirmationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val txtDate      = binding.reservationInfoDate
        val txtTime      = binding.reservationInfoTime
        val txtPplAmount = binding.reservationInfoSize
        val txtDetail    = binding.reservationInfoDisc

        val btnSaved     = binding.reservationInfoDelete
        val btnCancel    = binding.reservationInfoBack

        val app: KartoffelApp = (requireActivity().application as KartoffelApp)
        val content = app.getCurrentReservation()
        if (content == null){
            println("ReservationHolder shouldn't be null here!!")
            return root
        }

        // Setup UI from ReservationHolder
        txtDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(content.time)
        txtTime.text = SimpleDateFormat("HH:mm", Locale.GERMAN).format(content.time)
        txtPplAmount.text = content.pplCurrent.toString()

        // List dishes
        if (content.dishes != null){
            txtDetail.text = ""
            for (entry in content.dishes!!){
                val name = app.getDishById(entry.key)!!.name
                val line = "${entry.value} Mal - ${name}\n${txtDetail.text}"
                txtDetail.text = line
            }
        }

        // Assign buttons
        btnSaved.setOnClickListener {
            // Save data to server
            runBlocking {
                val response = NetManager().send("/attemptReservation", NetPacket(
                    System.currentTimeMillis(),
                    app.getUserToken(),
                    1,
                    GsonBuilder().create().toJson(content)
                ))

                if (response == null){
                    println("Communication error")
                } else {
                    val diag = AlertDialog.Builder(requireContext())

                    if (response.type == 0){
                        diag.setTitle(R.string.alert_success)
                        diag.setMessage("Die Reservierung wurde erfolgreich gespeichert!")
                        diag.setPositiveButton("Wunderbar!") {
                                _, _ ->
                            findNavController().navigate(R.id.navigation_home)
                        }
                    } else {
                        diag.setTitle(R.string.alert_failure)
                        diag.setMessage("Bei der Reservierung ist ein Fehlerauftetreten!")
                        // ToDo
                    }
                    diag.show()
                }
            }
        }

        btnCancel.setOnClickListener {
            // ToDo forward Wrapper!
            findNavController().navigate(R.id.navigation_reservation_details)
        }

        return root
    }
}