package org.cyntho.fh.kotlin.kartoffelpuffer.ui.imprint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImprintViewModel : ViewModel() {

    private val _title = MutableLiveData<String>().apply {
        value = "@string/Imprint"
    }
    private val _imprintText = MutableLiveData<String>().apply {
        value = "@string/txt_imprint_text"
    }

    val title: LiveData<String> = _title
    val content: LiveData<String> = _imprintText

}