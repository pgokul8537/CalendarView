package com.example.monthycalenderview

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.monthycalenderview.components.CheckInOutData
import com.example.monthycalenderview.components.UIEvent

class SelectDateTimeViewModel : ViewModel() {

    private var _checkState = mutableStateOf(CheckInOutData())
    val checkState: State<CheckInOutData> = _checkState

    fun onEvent(event: UIEvent) {
        when (event) {
            is UIEvent.CheckInChanged -> {
                _checkState.value = _checkState.value.copy(
                    checkInDate = event.date,
                )
            }

            is UIEvent.CheckOutChanged -> {
                _checkState.value = _checkState.value.copy(
                    checkOutDate = event.date,
                )
            }

            UIEvent.Reset -> {
                TODO()
            }
        }
    }

}