package org.cyntho.fh.kotlin.kartoffelpuffer.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Dish
import org.cyntho.fh.kotlin.kartoffelpuffer.data.ReservationHolder
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentAdminDishesBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket

class AdminDishesManagerFragment : Fragment() {

    var _binding: FragmentAdminDishesBinding? = null
    val binding get() = _binding !!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminDishesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val app = requireActivity().application as KartoffelApp

        val txtNamePref = binding.txtDishListPrefabName
        val btnCreateNew = binding.btnDishListCreateNew
        val btnEditPrefab = binding.btnDishListPrefabEdit
        val btnDeletePrefab = binding.btnDishListPrefabDelete
        val containerPref = binding.dishListPrefabEntry
        val viewContainer = binding.dishesList

        val btnCancel = binding.btnDishListCreateCancel
        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.navigation_settings)
        }

        if (arguments?.getBoolean("debug", true) == true){
            btnCreateNew.isInvisible = true
            btnEditPrefab.isVisible = true
            btnDeletePrefab.isInvisible = true
        }

        var response: NetPacket? = null
        runBlocking {
            response = NetManager().send("/getDishes", NetPacket(System.currentTimeMillis(), app.getUserToken(), 0, ""))
        }

        if (response != null){
            try {
                val arr = GsonBuilder().create().fromJson(response!!.data, Array<Dish>::class.java)

                viewContainer.removeAllViews()

                for (entry in arr){

                    val txtName = TextView(requireContext())
                    txtName.layoutParams = txtNamePref.layoutParams
                    txtName.background = txtNamePref.background
                    txtName.text = entry.name

                    val btnEdit = AppCompatButton(requireContext())
                    btnEdit.layoutParams = btnEditPrefab.layoutParams
                    btnEdit.background = btnEditPrefab.background
                    btnEdit.text = btnEditPrefab.text
                    btnEdit.isVisible = btnEditPrefab.isVisible

                    btnEdit.setOnClickListener {
                        val bundle = bundleOf(
                            Pair("dish_id", entry.dishId),
                            Pair("readOnly", true)
                        )

                        val currentReservation = app.getCurrentReservation()
                        if (currentReservation == null){
                            val tmp = ReservationHolder(
                                0,0,0, System.currentTimeMillis(), 0,0, mutableMapOf()
                            )
                            app.setCurrentReservation(tmp)
                        }

                        findNavController().navigate(R.id.navigation_dish_details, bundle)
                    }

                    val btnDelete = AppCompatButton(requireContext())
                    btnDelete.layoutParams = btnDeletePrefab.layoutParams
                    btnDelete.background = btnDeletePrefab.background
                    btnDelete.text = btnDeletePrefab.text
                    btnDelete.isVisible = btnDeletePrefab.isVisible

                    val containerRow = LinearLayout(requireContext())
                    containerRow.layoutParams = containerPref.layoutParams
                    containerRow.background = containerPref.background

                    containerRow.addView(txtName)
                    containerRow.addView(btnEdit)
                    containerRow.addView(btnDelete)

                    viewContainer.addView(containerRow)
                }

            } catch (any: java.lang.Exception){
                println("Error while parsing data")
                return root
            }
        }



        return root
    }
}