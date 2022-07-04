package org.cyntho.fh.kotlin.kartoffelpuffer.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.FIND_VIEWS_WITH_TEXT
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentHomeBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.LayoutWrapper
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
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

        // Handle admin display?
        val app = activity!!.application as KartoffelApp
        if (app.displayAdminView()){
            val background = binding.homeHeader
            background.setBackgroundColor(ContextCompat.getColor(context!!, R.color.app_background_admin))
        }

        /*
        ------------------------------------------------------------------------------------------------------
                                                  BUILD MAIN VIEW HERE
        ------------------------------------------------------------------------------------------------------ */

        val layoutResponse: NetPacket? =
            runBlocking {
                NetManager().send(
                    "/getCurrentLayout",
                    NetPacket(
                        System.currentTimeMillis(),
                        (app.getUserToken()),
                        0,
                        ""
                    )
                )
            }

        if (layoutResponse == null){

            val diag = AlertDialog.Builder(context!!)
            diag.setTitle("Connection failed")
            diag.setMessage("Unable to connect to the server. Make sure you are connected to the internet.")
            diag.setPositiveButton(android.R.string.ok) {_, _ -> }
            diag.show()
            return root
        }

        // Max: 7x9
        val wrapper = GsonBuilder().create().fromJson(layoutResponse.data, LayoutWrapper::class.java)

        val prefab = binding.btnGrid1
        var row = binding.tableRow1
        val arr = wrapper.asArray2D()
        var counter = 0

        println("Received:")
        arr?.prettyPrint()

        val colorEmpty = ContextCompat.getColor(context!!, R.color.grid_empty)
        val colorWall  = ContextCompat.getColor(context!!, R.color.grid_wall)
        val colorTable = ContextCompat.getColor(context!!, R.color.grid_free)

        for (y in 0 until wrapper.sizeY){
            for (x in 0 until wrapper.sizeX){
                val button = AppCompatButton(prefab.context)
                button.layoutParams = prefab.layoutParams
                button.text = ""
                val index = counter

                button.id = prefab.id + ++counter

                map[index] = button.id
                list.add(index, button)

                button.setOnClickListener {
                    handleButtonClick(index)
                }

                button.tag = "gridButton_$index"

                when (arr!!.arrayContents[x][y]){
                    0 -> button.setBackgroundColor(colorEmpty)
                    1 -> button.setBackgroundColor(colorWall)
                    2 -> button.setBackgroundColor(colorTable)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pack = NetPacket(System.currentTimeMillis(), (activity!!.application as KartoffelApp).getUserToken(), 0, System.currentTimeMillis().toString())

        CoroutineScope(Dispatchers.IO).launch {
            val response = NetManager().send("/getReservationsFor", pack)
            println("Response: $response")

        }


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