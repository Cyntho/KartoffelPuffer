package org.cyntho.fh.kotlin.kartoffelpuffer.ui.imprint

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import org.cyntho.fh.kotlin.kartoffelpuffer.R
import org.cyntho.fh.kotlin.kartoffelpuffer.databinding.FragmentImprintBinding

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

        val txTitle: TextView = binding.txImprintTitle
        viewModel.title.observe(viewLifecycleOwner, Observer {
            txTitle.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}