package org.cyntho.fh.kotlin.kartoffelpuffer.ui.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentAdminReservationBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import org.cyntho.fh.kotlin.kartoffelpuffer.net.ReservationListEntry
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class AdminReservationFragment : Fragment() {


    private var _binding: FragmentAdminReservationBinding? = null
    private val binding get() = _binding!!

    private val calendar = Calendar.getInstance()
    private var tYear = calendar.get(Calendar.YEAR)
    private var tMonth = calendar.get(Calendar.MONTH)
    private var tDay = calendar.get(Calendar.DAY_OF_MONTH)

    private val gregorian = GregorianCalendar(TimeZone.getTimeZone("GMT"))


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

                        gregorian.set(year, month + 1, dayOfMonth)
                        requestUpdate()
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - (1000 * 60)
            calendar.add(Calendar.MONTH, 1)
            datePicker.datePicker.maxDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, -1)
            datePicker.show()
        }

        btnDatePicker.text = String.format("%d / %d / %d", gregorian.get(GregorianCalendar.DAY_OF_MONTH), gregorian.get(GregorianCalendar.MONTH) + 1, gregorian.get(GregorianCalendar.YEAR))

        requestUpdate()

        return root
    }

    private fun requestUpdate(){

        var response: NetPacket? = null
        runBlocking {
            response = NetManager().send("/listReservations",
                NetPacket(
                    System.currentTimeMillis(),
                    (requireActivity().application as KartoffelApp).getUserToken(),
                    1, GsonBuilder().create().toJson(Timestamp(gregorian.timeInMillis))
                )
            )
        }

        if (response != null){
            val raw = GsonBuilder().create().fromJson(response!!.data, Array<ReservationListEntry>::class.java)
            val list = raw.asList().toMutableList()

            val btnPrefab = binding.btnAdminListPrefab
            val currentContainer = binding.reservationAtDate

            for (entry in list){

                val startTimer = SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.GERMAN).format(Timestamp(entry.start))
                val label = "$startTimer (${entry.people} Personen)\n ${entry.username}"
                val button = AppCompatButton(requireContext())

                button.layoutParams = btnPrefab.layoutParams
                button.background = btnPrefab.background
                button.text = label

                button.setOnClickListener {
                    val bundle = bundleOf("reservation_id" to entry.id)
                    findNavController().navigate(R.id.navigation_admin_reservation_details, bundle)
                }

                currentContainer.addView(button)
            }

            currentContainer.removeView(btnPrefab)
        }

    }
}