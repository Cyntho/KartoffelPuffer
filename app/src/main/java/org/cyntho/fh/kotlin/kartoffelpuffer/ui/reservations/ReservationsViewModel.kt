package org.cyntho.fh.kotlin.kartoffelpuffer.ui.reservations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Reservations view"
    }
    val text: LiveData<String> = _text
}