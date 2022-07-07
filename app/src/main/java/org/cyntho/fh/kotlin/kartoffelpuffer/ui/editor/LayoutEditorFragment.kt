package org.cyntho.fh.kotlin.kartoffelpuffer.ui.editor

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentLayoutEditorBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class LayoutEditorFragment : Fragment() {

    private lateinit var layoutEditorViewModel: LayoutEditorViewModel
    private var _binding: FragmentLayoutEditorBinding? = null

    private val binding get() = _binding!!
    private var mode: Int = 1 // 1 := wall, 2 := table, 0 := empty
    private var arr: Array2D = Array2D(7, 9)

    private var timeYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var timeMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var timeDayOfMonth: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layoutEditorViewModel = ViewModelProvider(this).get(LayoutEditorViewModel::class.java)
        _binding = FragmentLayoutEditorBinding.inflate(inflater, container, false)

        val root: View = binding.root
        binding.btnWallMode.paintFlags = binding.btnWallMode.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // Assign buttons to func
        binding.btnWallMode.setOnClickListener {
            mode = 1
            binding.btnWallMode.paintFlags = binding.btnWallMode.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            binding.btnTableMode.paintFlags = 0
            binding.btnDeleteMode.paintFlags = 0
        }

        binding.btnTableMode.setOnClickListener {
            mode = 2
            binding.btnWallMode.paintFlags = 0
            binding.btnTableMode.paintFlags = binding.btnTableMode.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            binding.btnDeleteMode.paintFlags = 0
        }
        binding.btnDeleteMode.setOnClickListener {
            mode = 0
            binding.btnWallMode.paintFlags = 0
            binding.btnTableMode.paintFlags = 0
            binding.btnDeleteMode.paintFlags = binding.btnDeleteMode.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        binding.btnSave.setOnClickListener {attemptSaving()}
        binding.btnLayoutEditorBack.setOnClickListener {
            findNavController().navigate(R.id.navigation_settings)
        }

        // Pick time
        val btnTime = binding.btnLayoutEditorTime
        val datePicker = DatePickerDialog(
            requireContext(),
            {_, year, month, dayOfMonth ->
                run {
                    btnTime.text = String.format("%d / %d / %d", dayOfMonth, month + 1, year)
                    timeYear = year
                    timeMonth = month + 1
                    timeDayOfMonth = dayOfMonth
                }
            }, timeYear, timeMonth, timeDayOfMonth
        )
        datePicker.datePicker.minDate = System.currentTimeMillis()
        btnTime.text = String.format("%d / %d / %d", timeDayOfMonth, timeMonth, timeYear)

        btnTime.setOnClickListener {
            datePicker.show()
        }


        // Generate grid with static size
        val gridHorizontal = 7
        val gridVertical   = 9

        val pref = binding.btnGrid1
        var row = binding.tableRow1

        for (x in 0 until gridVertical){
            for (y in 0 until gridHorizontal){

                val index = x * gridHorizontal + y
                val button = AppCompatButton(context!!)
                button.layoutParams = pref.layoutParams
                button.setBackgroundColor(ContextCompat.getColor(context!!, R.color.grid_empty))

                button.id = (pref.id + index)
                button.text = index.toString()
                button.setOnClickListener {
                    when (mode) {
                        1 -> {
                            button.setBackgroundColor(ContextCompat.getColor(context!!, R.color.grid_wall))
                        }
                        2 -> {
                            button.setBackgroundColor(ContextCompat.getColor(context!!, R.color.grid_table))
                        }
                        else -> {
                            button.setBackgroundColor(ContextCompat.getColor(context!!, R.color.grid_empty))
                        }
                    }
                    arr.arrayContents[y][x] = mode // axis reverse :c
                    println("Array[$y][$x] = $mode")
                }
                row.addView(button)
            }
            row = TableRow(context!!)
            row.layoutParams = binding.tableRow1.layoutParams
            row.setBackgroundColor(Color.BLACK)
            binding.tableGrid.addView(row)
        }
        row.removeView(pref)

        return root
    }

    private fun attemptSaving(){
        val app = requireActivity().application as KartoffelApp
        val gson = GsonBuilder().create()
        val timer = SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN)
        val t = timer.parse("$timeYear-${timeMonth}-$timeDayOfMonth")

        val wrapper = LayoutWrapper(
            -1,
            arr.width,
            arr.height,
            binding.txtLayoutEditorName.text.toString(),
            System.currentTimeMillis(),
            Timestamp(t.time).time,
            true,
            null
        )
        wrapper.fillFromArray2D(arr)

        println("Wrapper ist: $wrapper")

        val pack = NetPacket(
            System.currentTimeMillis(),
            app.getUserToken(),
            55,
            gson.toJson(wrapper)
        )


        var response: NetPacket? = null

        runBlocking {
            response = NetManager().send("/updateLayout", pack)
        }

        val diag = MaterialAlertDialogBuilder(requireContext())
        diag.background = ResourcesCompat.getDrawable(resources,R.drawable.roundedbutton,null)
        if (response == null){
            diag.setTitle("Connection failed")
            diag.setMessage("Unable to connect to the server. Make sure you are connected to the internet.")
            diag.setPositiveButton(android.R.string.ok) {_, _ -> }
        } else if (response!!.type == 0) {

            diag.setTitle("Success")
            diag.setMessage("Layout saved successfully!")

            diag.setPositiveButton(android.R.string.ok) { _, _ -> }
        }

        diag.show()
    }

}