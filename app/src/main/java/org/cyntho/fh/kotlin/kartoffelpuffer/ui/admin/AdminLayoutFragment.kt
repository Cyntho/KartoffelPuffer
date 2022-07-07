package org.cyntho.fh.kotlin.kartoffelpuffer.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.data.Dish
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentAdminLayoutBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.LayoutWrapper
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket

class AdminLayoutFragment : Fragment() {

    private var _binding: FragmentAdminLayoutBinding? = null
    private val binding get() = _binding!!

    private var layoutMap: MutableMap<Int, LayoutWrapper> = mutableMapOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val app = (requireActivity().application) as KartoffelApp

        val btnAddNewLayout = binding.btnAddNewLayout
        val btnCancel       = binding.btnLayoutManagerBack

        val mainContainer   = binding.layoutEntryContainerPref
        val containerPref   = binding.layoutEntryPrefab
        val txtNamePref     = binding.txtLayoutNamePrefab
        val btnEditPref     = binding.btnEditLayoutEntryPrefab
        val btnDeletePref   = binding.btnDeleteLayoutPrefab

        btnAddNewLayout.setOnClickListener {
            findNavController().navigate(R.id.navigation_layoutEditor)
        }

        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.navigation_settings)
        }

        var layoutResponse: NetPacket? = null
        runBlocking {
            layoutResponse = NetManager().send("/getAllLayouts",
                NetPacket(
                    System.currentTimeMillis(),
                    app.getUserToken(),
                    0,
                    ""
                )
            )
        }

        val diag = MaterialAlertDialogBuilder(requireContext())
        if (layoutResponse == null){
            diag.setTitle(getString(R.string.alert_failure))
            diag.setMessage(getString(R.string.connection_refused))
            diag.setPositiveButton(android.R.string.ok) {_,_ ->
                findNavController().navigate(R.id.navigation_home)
            }
            diag.show()
            return root
        } else if (layoutResponse!!.type == -1){
            diag.setTitle(getString(R.string.alert_failure))
            diag.setMessage("Unauthorized Request!!")
            diag.setPositiveButton(android.R.string.ok) {_,_ ->
                findNavController().navigate(R.id.navigation_home)
            }
            diag.show()
            return root
        }

        try {
            val raw = GsonBuilder().create().fromJson(layoutResponse!!.data, Array<LayoutWrapper>::class.java)
            val layouts = raw.asList().toMutableList()
            layoutMap = mutableMapOf()

            mainContainer.removeAllViews()

            for (entry in layouts){
                layoutMap[entry.id] = entry

                val btnEdit = AppCompatButton(requireContext())
                val btnDel  = AppCompatButton(requireContext())
                val txtName = TextView(requireContext())
                val cont    = LinearLayout(requireContext())

                txtName.layoutParams = txtNamePref.layoutParams
                txtName.text = entry.name

                btnEdit.layoutParams = btnEditPref.layoutParams
                btnEdit.text = btnEditPref.text
                btnEdit.background = btnEditPref.background
                btnEdit.setOnClickListener {
                    handleEdit(entry.id)
                }

                btnDel.layoutParams = btnDeletePref.layoutParams
                btnDel.text = btnDeletePref.text
                btnDel.background = btnDeletePref.background
                btnDel.setOnClickListener {
                    handleDelete(entry.id)
                }

                cont.layoutParams = containerPref.layoutParams
                cont.background = containerPref.background

                cont.addView(txtName)
                cont.addView(btnEdit)
                cont.addView(btnDel)

                mainContainer.addView(cont)
            }




        } catch (any: java.lang.Exception){
            any.printStackTrace()
        }
        return root
    }

    private fun handleDelete(id: Int){
        val diag = MaterialAlertDialogBuilder(requireContext())
        diag.setTitle(getString(R.string.alert_attention))
        diag.setMessage("Möchten Sie dieses Layout unwiderruflich löschen?")
        diag.setPositiveButton(getString(R.string.btn_confirm)) {_,_ ->

            var response: NetPacket? = null
            runBlocking {
                response = NetManager().send("/deleteLayout",
                    NetPacket(
                        System.currentTimeMillis(),
                        (requireActivity().application as KartoffelApp).getUserToken(),
                        0,
                        ""
                    )
                )
            }

            val feedback = MaterialAlertDialogBuilder(requireContext())
            if (response != null && response!!.type == 0){
                feedback.setTitle(getString(R.string.alert_success))
                feedback.setMessage("Layout erfolgreich gelöscht!")
                feedback.setPositiveButton("Okay") {_, _ ->
                    findNavController().navigate(R.id.navigation_admin_layout_manager)
                }
            } else {
                feedback.setTitle(getString(R.string.alert_failure))
                feedback.setMessage("Etwas ist schief gelaufen!")
                feedback.setPositiveButton("Okay") {_, _ ->
                    findNavController().navigate(R.id.navigation_admin_layout_manager)
                }
            }

            feedback.show()

        }
        diag.setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        diag.show()
    }
    private fun handleEdit(id: Int){

        val tmp = GsonBuilder().create().toJson(layoutMap[id])
        println("Wrapping: ${layoutMap[id]}")
        val bundle = bundleOf(
            "layout_wrapper" to tmp
        )
        findNavController().navigate(R.id.navigation_layoutEditor, bundle)
    }

}