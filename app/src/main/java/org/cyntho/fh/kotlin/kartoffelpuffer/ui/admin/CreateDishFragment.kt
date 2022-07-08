package org.cyntho.fh.kotlin.kartoffelpuffer.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Dish
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentCreateDishBinding

class CreateDishFragment : Fragment() {

    private var _binding: FragmentCreateDishBinding? = null
    private val binding get() = _binding!!
    private var dish: Dish? = null
    private var imagePath: String = ""
    private var toggleMap: HashMap<Int, Boolean> = hashMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateDishBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (arguments != null){
            if (arguments!!.getString("dish", "ERR") != "ERR"){
                dish = GsonBuilder().create().fromJson(arguments!!.getString("dish"), Dish::class.java)
            }
        }

        val txtDishName        = binding.txtCreateDishName
        val txtDishDescription = binding.txtDishDescriptionInput

        val imgView            = binding.dishCreateImagePrev

        val btnGlutenToggle = binding.btnGlutenToggle
        val btnEggToggle    = binding.btnEggToggle
        val btnFishToggle   = binding.btnFishToggle
        val btnSoyToggle    = binding.btnSojaToggle
        val btnNutsToggle   = binding.btnNutsToggle
        val btnMilkToggle   = binding.btnMilkToggle
        val btnLupinesToggle= binding.btnLupinenToggle
        val btnSesameToggle = binding.btnSesamToggle

        val btnConfirm      = binding.btnCreateDishConfirm
        val btnCancel       = binding.btnCreateDishBack

        val toggleList = mutableListOf<AppCompatButton>(
            btnGlutenToggle,
            btnEggToggle,
            btnFishToggle,
            btnSoyToggle,
            btnNutsToggle,
            btnMilkToggle,
            btnLupinesToggle,
            btnSesameToggle
        )

        for (btn in toggleList){
            toggleMap[btn.hashCode()] = false
            btn.setOnClickListener {
                var value = toggleMap[btn.hashCode()]
                if (value == true){
                    value = false
                }

                if (value == true){
                    btn.setBackgroundResource(R.drawable.roundedbuttongreen)
                } else {
                    btn.setBackgroundResource(R.drawable.roundedbutton)
                }
            }
        }

        imgView.setOnClickListener {
            // ToDo: Enable image upload..
        }

        btnConfirm.setOnClickListener {
            dish = Dish(
                -1, true, imagePath, txtDishName.text.toString(),
                mutableListOf(), txtDishDescription.text.toString()
            )
        }

        btnCancel.setOnClickListener {
            val diag = MaterialAlertDialogBuilder(requireContext())
            diag.setTitle(getString(R.string.alert_attention))
            diag.setMessage("Wollen Sie wirklich abbrechen?\nNicht gespeicherte Daten gehen verloren!")
            diag.setPositiveButton("Ja") { _, _ ->
                // ToDo
            }
            diag.setPositiveButton("Nein") { _, _ -> }
            diag.show()
        }





        return root
    }
}