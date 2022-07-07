package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationsBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import org.cyntho.fh.kotlin.kartoffelpuffer.net.ReservationListEntry
import org.cyntho.fh.kotlin.kartoffelpuffer.net.ReservationWrapper
import java.sql.Timestamp
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


        // Forward to admin view, if authorized
        val app: KartoffelApp = activity!!.application as KartoffelApp ?: return root
        if (app.isAdmin() && app.displayAdminView()){
            println("Forwarding..")
            findNavController().navigate(R.id.navigation_admin_reservation)
        }

        // Not an admin, display own reservations
        val btnPrefab = binding.btnMyReservationsPrefab
        val containerBox = binding.reservationsLayoutContainer

        var response: NetPacket? = null

        runBlocking {
            response = NetManager().send("/listReservations", NetPacket(System.currentTimeMillis(), app.getUserToken(), 0,
                GsonBuilder().create().toJson(Timestamp(System.currentTimeMillis()))))
        }
        if (response != null && response!!.type == 0){
            println("Response: $response")
            val raw = GsonBuilder().create().fromJson(response!!.data, Array<ReservationListEntry>::class.java)
            val list = raw.asList().toMutableList()

            for (entry in list){
                println("entry. $entry")

                val button = AppCompatButton(requireContext())
                button.layoutParams = btnPrefab.layoutParams
                button.background = btnPrefab.background

                val startTimer = SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.GERMAN).format(Timestamp(entry.start))
                val label = "$startTimer (${entry.people} Personen)\n ${entry.username}"

                button.setOnClickListener {
                    val bundle = bundleOf("reservation_id" to entry.id)
                    findNavController().navigate(R.id.navigation_reservation_details, bundle)
                }

                button.text = label
                containerBox.addView(button)
            }
            containerBox.removeView(btnPrefab)
        }
        return root
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }
}