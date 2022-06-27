package org.cyntho.fh.kotlin.kartoffelpuffer.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Some settings go here"
    }

    val text: LiveData<String> = _text
}