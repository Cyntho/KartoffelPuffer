package org.cyntho.fh.kotlin.kartoffelpuffer.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import io.ktor.util.*
import kotlinx.coroutines.*
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.data.ReservationHolder
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentHomeBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.suspendCoroutine

class HomeFragment : Fragment() {

    // Bindings
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // References to table layout
    private val idToCoordinateMap: HashMap<Int, Array<Int>> = HashMap() // ButtonId = arrayOf(x, y)
    private val storedReservations: HashMap<Int, Timestamp> = HashMap() // ButtonId = Timestamp (table free)
    private var array: Array2D? = null

    // Handling of times
    private var cal: Calendar = Calendar.getInstance()
    private var timeYear: Int = cal.get(Calendar.YEAR)
    private var timeMonth: Int = cal.get(Calendar.MONTH) + 1
    private var timeDay: Int = cal.get(Calendar.DAY_OF_MONTH)
    private var timeHour: Int = cal.get(Calendar.HOUR_OF_DAY)
    private var timeMinute: Int = cal.get(Calendar.MINUTE)
    private var timestamp: Timestamp = Timestamp(System.currentTimeMillis())

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
        txtDate.text = String.format("%d / %d / %d", timeDay, timeMonth, timeYear)

        if (context == null) return root

        val datePicker = DatePickerDialog(
            context!!,
            { _, year, month, dayOfMonth ->
                run {
                    txtDate.text = String.format("%d / %d / %d", dayOfMonth, month + 1, year)
                    timeYear = year
                    timeMonth = month
                    timeDay = dayOfMonth
                    handleTimeChanged()
                }
            }, timeYear, timeMonth, timeDay);

        // Set interval to current time + 30 days
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        cal.add(Calendar.MONTH, 1)
        datePicker.datePicker.maxDate = cal.timeInMillis
        cal.add(Calendar.MONTH, -1)

        txtDate.setOnClickListener { datePicker.show() }


        // Handling TimePicker
        val txtTime = binding.btnTime
        txtTime.text = String.format("%02d : %02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))

        val timePicker = TimePickerDialog (
            context!!,
            0,
            getTimePickerListener(txtTime),
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        )


        // ToDo: Limit time to 11:00 to 22:00 (not incl in class, need manual check)
        txtTime.setOnClickListener {timePicker.show()}

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
        array = wrapper.asArray2D()
        var counter = 0
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

                idToCoordinateMap[index] = arrayOf(x, y, array!!.arrayContents[x][y])

                button.setOnClickListener {
                    handleButtonClick(index)
                }

                button.tag = "gridButton_$index"

                when (array!!.arrayContents[x][y]){
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

    /**
     * Handle all one-time updates here
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleTimeChanged()
    }



    private fun handleButtonClick(id: Int){
        try {
            val x = idToCoordinateMap[id]!![0]
            val y = idToCoordinateMap[id]!![1]
            val type = when (idToCoordinateMap[id]!![2]){
                0 -> "Free"
                1 -> "Wall"
                2 -> "Table"
                else -> {"unknown"}
            }

            val reserved = storedReservations[id]

            // If table taken, display feedback and return
            val diag = AlertDialog.Builder(context!!)
            if (reserved != null){
                diag.setTitle(getString(R.string.Besetzt))
                diag.setMessage("Dieser Tisch ist leider vergeben.\n" +
                        "Er wird frei ab:\n\n $reserved")
                diag.setNegativeButton(android.R.string.cancel) {_, _ -> }
                diag.show()
            } else if (type == "Table") {

            // Table appears to be free. Prompt details
                diag.setTitle(getString(R.string.Frei))
                diag.setMessage(
                    "Datum: $timeDay.$timeMonth.$timeYear\n" +
                    "Uhrzeit: $timeHour:$timeMinute Uhr\n\n" +
                    "Max. Personen: 4" // ToDO: Remove hard code
                )
                diag.setPositiveButton(getString(R.string.btn_confirm)) {
                    _, _ ->

                    // set ref to current attempt
                    val app = ((activity!!.application) as KartoffelApp)
                    app.setCurrentReservation(
                        ReservationHolder(app.getCurrentLayoutID(), x, y, timestamp, 1, 4, null)
                    )
                    findNavController().navigate(R.id.navigation_reservation_details)
                }
                diag.setNegativeButton(android.R.string.cancel) {_, _ ->}
                diag.show()
            }
        } catch (any: Exception){
            any.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleTimeChanged(){

        val df = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.GERMAN)
        val tmp = df.parse("$timeYear.$timeMonth.$timeDay $timeHour:$timeMinute")
        if (tmp != null) {
            timestamp = Timestamp(tmp.time)
            println("timestamp: ${timestamp}")
        } else {
            println("timestamp null")
        }

        // ToDo Timestamp negativ???
        val currentLayout = (activity!!.application as KartoffelApp).getCurrentLayoutID()
        val selectedTime = SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.GERMAN).format(timestamp)


        println("Requesting reservation list for layout [$currentLayout] at time: $selectedTime")

        val pack = NetPacket(System.currentTimeMillis(), (activity!!.application as KartoffelApp).getUserToken(), currentLayout, timestamp.time.toString())
        val response = runBlocking {
            NetManager().send("/getReservationsFor", pack)
        }
        if (response == null){
            println("Unable to reach server.")
            return
        }

        val test = GsonBuilder().create().fromJson(response.data, Array<ReservationWrapper>::class.java)
        val app = (requireActivity().application as KartoffelApp)
        for (t in test){
            try {
                if (array!!.arrayContents[t.x][t.y] == 2){
                    // Update table view
                    val index = t.y * array!!.width + t.x
                    val btn = view!!.findViewWithTag<AppCompatButton>("gridButton_$index")
                    btn.setBackgroundColor(Color.RED)

                    // Store data for buttons
                    storedReservations[index] = Timestamp(t.time)

                    // Store layout id
                    app.setCurrentLayoutID(t.layout)
                } else {
                    println("array[${t.x}][${t.y}] := ${array!!.arrayContents[t.x][t.y]}")
                }
            } catch (any:Exception){
                any.printStackTrace()
            }
        }
    }

    private fun getTimePickerListener(txt: Button) =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            // After selecting time - set text in TextView
            "%02d:%02d".format(hourOfDay, minute).also { txt.text = it } . also {
                timeHour = hourOfDay
                timeMinute = minute
            } .also { handleTimeChanged() }
        }

}