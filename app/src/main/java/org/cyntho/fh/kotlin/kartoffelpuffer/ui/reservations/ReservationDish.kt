package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Dish
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentReservationDishBinding
import java.io.File

class ReservationDish : Fragment() {

    private var _binding: FragmentReservationDishBinding? = null
    private val binding get() = _binding!!


    private var dishCounter = 1
    private var dish: Dish? = null
    private var _backup: MutableMap<Int, Int> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentReservationDishBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val dishID = arguments?.getInt("dish_id") ?: -1
        println("Dish_Details received id: $dishID")

        if (dishID == -1){
            println("Something went terribly wrong.")
            return root
        }
        val app = (activity!!.application) as KartoffelApp ?: return root
        _backup = app.getCurrentReservation()!!.dishes!!

        dish = app.getDishById(dishID)
        if (dish == null) return root

        // Bindings
        val txtDishName     = binding.dishName
        val txtDescription  = binding.dishDescription

        val btnGluten       = binding.gluten
        val btnSoy          = binding.Soja
        val btnMilk         = binding.Milch
        val btnEggs         = binding.Eier

        val btnLupin        = binding.Lupinen
        val btnFish         = binding.Fisch
        val btnNuts         = binding.NSse
        val btnSesame       = binding.Sesam

        val btnAmountPlus   = binding.dishCountp
        val btnAmountMinus  = binding.dishCountm
        val txtDishAmount   = binding.dishCount

        txtDishName.text = dish!!.name
        txtDescription.text = dish!!.description
        dishCounter = _backup[dishID] ?: 1
        txtDishAmount.text = dishCounter.toString()

        // Load and display image
        val iconFile = File((requireActivity().application as KartoffelApp).filesDir.path + "/${dish!!.iconHash}")
        try {
            if (iconFile.exists()){
                val dishImage = binding.dishImage
                dishImage.setImageBitmap(BitmapFactory.decodeFile(iconFile.absolutePath))
            }
        } catch (any: Exception){
            println("Unable to load image in 'fragment_reservation_dish' for dish '$dishID'")
        }

        // Set button background, if dish has that allergy
        for (allergy in dish!!.allergies){

            println("Found allergies:")
            println(allergy)



            when (allergy.name){
                "Fisch" -> btnFish.setBackgroundResource(R.drawable.roundedbuttongreen)
                "Soja" -> btnSoy.setBackgroundResource(R.drawable.roundedbuttongreen)
                "Gluten" -> btnGluten.setBackgroundResource(R.drawable.roundedbuttongreen)
                "Eier" -> btnEggs.setBackgroundResource(R.drawable.roundedbuttongreen)
                "Nüsse" -> btnNuts.setBackgroundResource(R.drawable.roundedbuttongreen)
                "Lupinen" -> btnLupin.setBackgroundResource(R.drawable.roundedbuttongreen)
                "Sesam" -> btnSesame.setBackgroundResource(R.drawable.roundedbuttongreen)
                "Milch" -> btnMilk.setBackgroundResource(R.drawable.roundedbuttongreen)
                else -> println("Weird allergy returned: ${allergy.name}")
            }
        }

        // handle amount +-
        btnAmountPlus.setOnClickListener { handleAmountIncreased() }
        btnAmountMinus.setOnClickListener { handleAmountDecreased() }

        // handle confirm/cancel buttons
        binding.dishAdd.setOnClickListener {

            _backup[dishID] = dishCounter

            val current = app.getCurrentReservation()!!
            current.dishes = _backup
            app.setCurrentReservation(current)
            findNavController().navigate(R.id.navigation_reservation_details)
        }
        binding.dishCancel.setOnClickListener {
            findNavController().navigate(R.id.navigation_reservation_details)
        }

        return root
    }

    private fun handleAmountIncreased(){
        if (dishCounter < 10){
            dishCounter++
            binding.dishCount.text = dishCounter.toString()
        }
    }
    private fun handleAmountDecreased(){
        if (dishCounter > 1){
            dishCounter--
            binding.dishCount.text = dishCounter.toString()
        }
    }
}