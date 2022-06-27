package org.cyntho.fh.kotlin.kartoffelpuffer.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsPopupViewModel : ViewModel() {

    private val _title = MutableLiveData<String>().apply {
        value = ""
    }

}