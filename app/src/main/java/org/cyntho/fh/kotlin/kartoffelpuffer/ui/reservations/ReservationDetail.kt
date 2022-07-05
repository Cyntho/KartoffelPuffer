package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
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
import kotlinx.coroutines.DelicateCoroutinesApi
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentReservationDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val app: KartoffelApp = requireActivity().application as KartoffelApp
        val currentAttempt = app.getCurrentReservation() ?: return root

        val txtDate = binding.reservationDateButton
        val txtTime = binding.dishCountp
        val btnSetAmount = binding.btnReservationSize1

        txtDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(currentAttempt.time)
        txtTime.text = SimpleDateFormat("HH:mm", Locale.GERMAN).format(currentAttempt.time)
        btnSetAmount.text = String.format("%s / %s", currentAttempt.pplCurrent, currentAttempt.pplMax)

        val imgPrefab = binding.foodPrefab
        val layoutContainer = binding.foodSelect

        // Handle dishes
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

        // Check if we need to add stuff
        val dishID     = arguments?.getInt("dish_id")     ?: -1
        val dishAmount = arguments?.getInt("dish_amount") ?: -1

        if (dishID != -1 && dishAmount != -1){
            println("Dish with id $dishID should be added $dishAmount times.")
        }

        // Handle for reservation pre-ordered dishes
        val txtDishNamePrefab   = binding.txtPrefabName
        val txtDishAmountPrefab = binding.txtPrefabAmount
        val btnDishDeletePrefab = binding.btnPrefabDelete
        val dishRowPrefab       = binding.dishEntryPrefab

        val dishContainer       = binding.dishesList

        // Load dishMap from arguments
        val wrapper = arguments?.getString("wrapper")
        println("Reservation_Details received wrapper: $wrapper")

        if (wrapper != null){
            val type = object : TypeToken<MutableMap<Int, Int>>() {}.type
            dishMap = GsonBuilder().create().fromJson(wrapper, type)


            for (entry in dishMap){
                val dish = app.getDishById(entry.key) ?: continue
                val ctx  = requireContext()

                val textViewName    = TextView(ctx)
                val textViewAmount  = TextView(ctx)
                val btnDelete       = AppCompatButton(ctx)

                textViewName.layoutParams = txtDishNamePrefab.layoutParams
                textViewAmount.layoutParams = txtDishAmountPrefab.layoutParams
                btnDelete.layoutParams = btnDishDeletePrefab.layoutParams

                textViewName.text = dish.name
                textViewAmount.text = entry.value.toString()
                btnDelete.setOnClickListener { handleListEntryDelete(dish.dishId) }

                val row = LinearLayout(ctx)
                row.layoutParams = dishRowPrefab.layoutParams

                row.addView(textViewName)
                row.addView(textViewAmount)
                row.addView(btnDelete)

                dishContainer.addView(row)
            }
        }

        // clean up
        dishRowPrefab.removeAllViews()
        dishContainer.removeView(dishRowPrefab)

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