package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationDetailBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReservationDetail : Fragment() {

    private var _binding: FragmentReservationDetailBinding? = null
    private val binding get() = _binding!!

    private var dishMap = mutableMapOf<Int, Int>() // Id --> Amount
    private var peopleCounter: Int = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentReservationDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val app: KartoffelApp = requireActivity().application as KartoffelApp
        val currentAttempt = app.getCurrentReservation() ?: return root

        val txtDate = binding.reservationDateButton
        val txtTime = binding.dishCountp
        val btnSetAmount = binding.btnReservationSize1

        val btnAddPeople = binding.btnAddPerson
        val btnRemPeople = binding.btnRemovePerson

        val currentReservation = app.getCurrentReservation()
        peopleCounter = currentReservation?.pplCurrent ?: 1

        btnAddPeople.setOnClickListener {
            if (peopleCounter < 4){
                peopleCounter++
                val tmp = "$peopleCounter / ${currentAttempt.pplMax}"
                btnSetAmount.text = tmp
                currentReservation!!.pplCurrent = peopleCounter
                app.setCurrentReservation(currentReservation)
            }
        }

        btnRemPeople.setOnClickListener {
            if (peopleCounter > 1){
                peopleCounter--
                val tmp = "$peopleCounter / ${currentAttempt.pplMax}"
                btnSetAmount.text = tmp
                currentReservation!!.pplCurrent = peopleCounter
                app.setCurrentReservation(currentReservation)
            }
        }

        txtDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(currentAttempt.time)
        txtTime.text = SimpleDateFormat("HH:mm", Locale.GERMAN).format(currentAttempt.time)
        btnSetAmount.text = String.format("%s / %s", currentAttempt.pplCurrent, currentAttempt.pplMax)

        val imgPrefab = binding.foodPrefab
        val layoutContainer = binding.foodSelect

        // Handle dishes -> Download icons if needed
        for (dish in app.getDishList()){
            val link = "http://10.0.2.2:8080/static_dishes/${dish.iconHash}"
            val entry = ImageView(requireContext())
            entry.layoutParams = imgPrefab.layoutParams

            try {
                val client: HttpClient = HttpClient {
                    install(ContentNegotiation){
                        json(Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        })
                    }
                    install(HttpTimeout){
                        requestTimeoutMillis = 1000
                    }
                }
                val file = File(app.filesDir.path + "/${dish.iconHash}")
                val mustDownload = file.exists()
                if (!file.exists() && !file.createNewFile()){
                    println("Unable to store downloaded files into system.")
                    continue
                }

                if (mustDownload){
                    runBlocking {
                        val url = Url(link)
                        try {
                            val l = client.get(url).bodyAsChannel().copyAndClose(file.writeChannel())
                            println("Received and wrote [$l] bytes to file [${file.length()}]")
                        } catch (e: java.lang.Exception){
                            println(e.message)
                        }
                    }

                    if (file.exists()){
                        println("Download successful: ${file.absolutePath} length: ${file.length()}")
                        // ToDo: verify hash
                    }
                }

                entry.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                entry.setOnClickListener {
                    displayDetailsFor(dish.dishId)
                }
                layoutContainer.addView(entry)

            } catch (any: Exception){
                any.printStackTrace()
            }

            layoutContainer.removeView(imgPrefab)
        }

        // Handle for reservation pre-ordered dishes
        val txtDishNamePrefab   = binding.txtPrefabName
        val txtDishAmountPrefab = binding.txtPrefabAmount
        val btnDishDeletePrefab = binding.btnPrefabDelete
        val dishRowPrefab       = binding.dishEntryPrefab
        val dishContainer       = binding.dishesList

        //dishMap = app.getCurrentReservation()!!.dishes!!
        if (app.getCurrentReservation()!!.dishes == null){
            val tmp = app.getCurrentReservation()
            tmp!!.dishes = mutableMapOf()
            app.setCurrentReservation(tmp)
        }
        dishMap = app.getCurrentReservation()!!.dishes!!

        for (entry in dishMap){
            val dish = app.getDishById(entry.key) ?: continue
            val ctx  = requireContext()

            val textViewName    = TextView(ctx)
            val textViewAmount  = TextView(ctx)
            val btnDelete       = AppCompatButton(ctx)

            textViewName.layoutParams = txtDishNamePrefab.layoutParams
            textViewAmount.layoutParams = txtDishAmountPrefab.layoutParams
            textViewAmount.gravity = txtDishAmountPrefab.gravity
            textViewAmount.background = txtDishAmountPrefab.background
            textViewAmount.setTextColor(txtDishAmountPrefab.textColors)


            btnDelete.layoutParams = btnDishDeletePrefab.layoutParams
            btnDelete.gravity = btnDishDeletePrefab.gravity
            btnDelete.background = btnDishDeletePrefab.background
            btnDelete.setTextColor(btnDishDeletePrefab.textColors)
            btnDelete.text = btnDishDeletePrefab.text

            textViewName.text = dish.name
            textViewAmount.text = entry.value.toString()
            btnDelete.setOnClickListener {
                dishMap.remove(entry.key)
                val tmp = app.getCurrentReservation()
                val arr = tmp!!.dishes
                arr!!.remove(entry.key)
                tmp.dishes = arr
                app.setCurrentReservation(tmp)
                findNavController().navigate(R.id.navigation_reservation_details)
                //handleListEntryDelete(dish.dishId)
            }

            val row = LinearLayout(ctx)
            row.layoutParams = dishRowPrefab.layoutParams

            row.addView(textViewName)
            row.addView(textViewAmount)
            row.addView(btnDelete)

            dishContainer.addView(row)
        }

        // clean up
        dishRowPrefab.removeAllViews()
        dishContainer.removeView(dishRowPrefab)


        // Handle "next"/"cancel" buttons
        val btnContinue = binding.dishAdd
        val btnCancel   = binding.dishCancel

        // Cancel Reservation? --> AlertDialog
        btnCancel.setOnClickListener {
            val diag = MaterialAlertDialogBuilder(requireContext())
            diag.background = ResourcesCompat.getDrawable(resources,R.drawable.roundedbutton,null)
            diag.setTitle(R.string.alert_attention)
            diag.setMessage("Wenn Sie jetzt abbrechen geht die Reservierung verloren!")
            diag.setPositiveButton("Zurück zur Reservierung") {
                    _, _ ->
            }
            diag.setNegativeButton("Reservierung abbrechen") {_, _ ->
                // Reset and forward
                app.resetCurrentReservation()
                findNavController().navigate(R.id.navigation_home)
            }
            diag.show()
        }

        // Continue to confirmation
        btnContinue.setOnClickListener {
            // Save data and forward
            val current = app.getCurrentReservation()
            if (current == null){
                println("app.getCurrentReservation() Shouldn't be null!")
            } else {
                current.dishes = dishMap
                current.pplCurrent = peopleCounter
                app.setCurrentReservation(current)
                findNavController().navigate(R.id.navigation_reservation_confirmation)
            }
        }


        return root
    }

    private fun displayDetailsFor(id: Int){
        /*
        val bundle = bundleOf("dish_id" to id)
        findNavController().navigate(R.id.navigation_dish_details, bundle)
         */
        // val data = bundleOf("wrapper" to GsonBuilder().create().toJson(dishMap))
        val data = bundleOf()
            //Pair("wrapper", GsonBuilder().create().toJson(dishMap)),
            //Pair("dish_id", id)
        data.putString("wrapper", GsonBuilder().create().toJson(dishMap))
        data.putInt("dish_id", id)


        println("Reservation_Details passing to Dish_Details: ${data.getString("wrapper")}")
        findNavController().navigate(R.id.navigation_dish_details, data)
    }

    private fun handleListEntryDelete(id: Int){

    }


}