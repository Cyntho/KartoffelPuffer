package org.cyntho.fh.kotlin.kartoffelpuffer.ui.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LayoutEditorViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the editor"
    }
    val text: LiveData<String> = _text
}