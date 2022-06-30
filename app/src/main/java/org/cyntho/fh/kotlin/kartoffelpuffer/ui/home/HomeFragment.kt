package org.cyntho.fh.kotlin.kartoffelpuffer.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.cyntho.fh.kotlin.kartoffelpuffer.MainActivity
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentHomeBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.Greeting
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val list: MutableList<AppCompatButton> = mutableListOf()
    private val map: HashMap<Int, Int> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Handling DatePicker
        val txtDate = binding.btnDate
        val currentTime = Calendar.getInstance()
        val year = currentTime.get(Calendar.YEAR)
        val month = currentTime.get(Calendar.MONTH)
        val day = currentTime.get(Calendar.DAY_OF_MONTH)

        txtDate.setText(String.format("%d / %d / %d", day, month + 1, year))
        if (context == null) return root

        val datePicker = DatePickerDialog(
            context!!,
            { _, year, month, dayOfMonth ->
                run {
                    txtDate.text = String.format("%d / %d / %d", dayOfMonth, month + 1, year)
                    handleTimeChanged()
                }
            }, year, month, day);

        // Set interval to current time + 30 days
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        currentTime.add(Calendar.MONTH, 1)
        datePicker.datePicker.maxDate = currentTime.timeInMillis
        currentTime.add(Calendar.MONTH, -1)

        txtDate.setOnClickListener { datePicker.show() }


        // Handling TimePicker
        val txtTime = binding.btnTime
        txtTime.text = String.format("%02d : %02d", currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE))

        val timePicker = TimePickerDialog (
            context!!,
            0,
            getTimePickerListener(txtTime),
            currentTime.get(Calendar.HOUR_OF_DAY),
            currentTime.get(Calendar.MINUTE),
            true
        )

        // ToDo: Limit time to 11:00 to 22:00 (not incl in class, need manual check)
        txtTime.setOnClickListener { timePicker.show() }


        // DEBUG START



        /*
        ------------------------------------------------------------------------------------------------------
                                                  BUILD MAIN VIEW HERE
        ------------------------------------------------------------------------------------------------------ */


        // Max: 7x9
        val gridHorizontal = 7
        val gridVertical   = 9

        val prefab = binding.btnGrid1
        var row = binding.tableRow1

        for (x in 0 until gridVertical){
            for (y in 0 until gridHorizontal){
                val button = AppCompatButton(context!!)
                button.layoutParams = prefab.layoutParams
                button.background = prefab.background
                button.text = ""

                button.id

                val index = x * gridHorizontal + y

                map[index] = button.id
                list.add(index, button)

                button.setOnClickListener {
                    handleButtonClick(index)
                }

                row.addView(button)
            }



            // Add new row
            row = TableRow(context!!)
            row.layoutParams = binding.tableRow1.layoutParams
            row.setBackgroundColor(Color.BLACK)
            binding.tableGrid.addView(row)
        }

        // Remove prefab button
        row.removeView(prefab)

        prefab.setOnClickListener { handleButtonClick(0) }



        return root
    }

    private fun handleButtonClick(id: Int){
        println("Button $id has been clicked!")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleTimeChanged(){
        println("Updating table view...")
    }

    private fun getTimePickerListener(txt: Button) =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            // After selecting time - set text in TextView
            "%02d:%02d".format(hourOfDay, minute).also { txt.text = it } .also { handleTimeChanged() }
        }

}