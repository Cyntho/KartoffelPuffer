package org.cyntho.fh.kotlin.kartoffelpuffer.ui.setup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SetupViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Setup"
    }
    val text: LiveData<String> = _text
}