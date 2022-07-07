package org.cyntho.fh.kotlin.kartoffelpuffer.ui.imprint

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import kotlinx.coroutines.runBlocking
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.app.KartoffelApp
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentImprintBinding
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetManager
import org.cyntho.fh.kotlin.kartoffelpuffer.net.NetPacket

class ImprintFragment : Fragment() {

    private lateinit var viewModel: ImprintViewModel
    private var _binding: FragmentImprintBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(ImprintViewModel::class.java)

        _binding = FragmentImprintBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val app: KartoffelApp = (requireActivity().application as KartoffelApp)


        var response: NetPacket? = null
        runBlocking {
            response = NetManager().send("/getImprint", NetPacket(System.currentTimeMillis(), app.getUserToken(), 0, ""))
        }

        var imprintText = "Copyright &copy; 2022 - github.com/cyntho/KartoffelPuffer"

        if (response != null){
            imprintText = response!!.data
        }

        val imprintContent = binding.txtImprintContent
        imprintContent.text = imprintText

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}