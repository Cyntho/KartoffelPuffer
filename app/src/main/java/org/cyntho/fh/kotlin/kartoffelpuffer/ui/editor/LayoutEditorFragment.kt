package org.cyntho.fh.kotlin.kartoffelpuffer.ui.editor

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
import java.util.*

class LayoutEditorFragment : Fragment() {

    private lateinit var layoutEditorViewModel: LayoutEditorViewModel
    private var _binding: FragmentLayoutEditorBinding? = null

    private val binding get() = _binding!!
    private var mode: Int = 1 // 1 := wall, 2 := table, 3 := delete
    private var arr: Array2D = Array2D(7, 9)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layoutEditorViewModel = ViewModelProvider(this).get(LayoutEditorViewModel::class.java)
        _binding = FragmentLayoutEditorBinding.inflate(inflater, container, false)

        val root: View = binding.root

        // Assign buttons to func
        binding.btnWallMode.setOnClickListener { mode = 1 }
        binding.btnTableMode.setOnClickListener { mode = 2 }
        binding.btnDeleteMode.setOnClickListener { mode = 3 }
        binding.btnSave.setOnClickListener {attemptSaving()}
        binding.btnTest.setOnClickListener {

            // only send array2d
            /*
            val typetoken = object : TypeToken<Array<Array<Int>>>() {}.type
            val wrapper = Gson().toJson(arr.arrayContents, typetoken)
            println("Wrapped: $wrapper")
            */

            val layout = LayoutWrapper(0, arr.width, arr.height, "Something", 0, 0, true, null)
            layout.fillFromArray2D(arr)

            val wrapper = Gson().toJson(layout)

            println("Wrapped: $wrapper")

            val unwrapped = Gson().fromJson(wrapper, LayoutWrapper::class.java)
            println("Unwrapped: $unwrapped")

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
        val app = activity!!.application as KartoffelApp
        val gson = GsonBuilder().create()
        val packedArray = gson.toJson(arr)
        val wrapper = LayoutWrapper(
            0,
            arr.width,
            arr.height,
            "Some Layout",
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            true,
            null
        )
        wrapper.fillFromArray2D(arr)

        val pack: NetPacket = NetPacket(
            System.currentTimeMillis(),
            app.getUserToken(),
            55,
            gson.toJson(wrapper)
        )

        println("Wrapped layout into:")
        println(pack)

        var response: NetPacket? = null

        runBlocking {
            response = NetManager().send("updateLayout", pack)
        }

        val diag = AlertDialog.Builder(context!!)
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