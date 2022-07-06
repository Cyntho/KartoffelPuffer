package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

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

class ReservationDish : Fragment() {

    private var _binding: FragmentReservationDishBinding? = null
    private val binding get() = _binding!!


    private var dishCounter = 1
    private var dish: Dish? = null
    private var _backup: MutableMap<Int, Int> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentReservationDishBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val wrapper = arguments?.getString("wrapper")
        if (wrapper != null){
            println("Dish_Details received wrapper: $wrapper")

            val type = object : TypeToken<MutableMap<Int, Int>>() {}.type
            _backup = GsonBuilder().create().fromJson(wrapper, type)
        } else {
            _backup = mutableMapOf()
        }

        val dishID = arguments?.getInt("dish_id") ?: -1
        println("Dish_Details received id: $dishID")

        if (dishID == -1){
            println("Something went terribly wrong.")
            return root
        }
        val app = (activity!!.application) as KartoffelApp ?: return root

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
            println("setting dishID[$dishID] to $dishCounter")

            val data = bundleOf(Pair("wrapper", GsonBuilder().create().toJson(_backup)))

            println("Dish_Details sending wrapper. ${data.getString("wrapper")}")

            findNavController().navigate(R.id.navigation_reservation_details, data)
        }
        binding.dishCancel.setOnClickListener {
            val data = bundleOf(Pair("wrapper", GsonBuilder().create().toJson(_backup)))
            findNavController().navigate(R.id.navigation_reservation_details, data)
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