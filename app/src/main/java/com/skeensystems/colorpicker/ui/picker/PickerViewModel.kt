package com.skeensystems.colorpicker.ui.picker

import android.os.Looper
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PickerViewModel : ViewModel() {
    private val _pickerColour = MutableStateFlow(Color.Black)
    val pickerColour: StateFlow<Color> = _pickerColour.asStateFlow()

    fun updateColour(
        r: Int,
        g: Int,
        b: Int,
    ) {
        val color = Color(r, g, b)

        if (Looper.myLooper() == Looper.getMainLooper()) {
            _pickerColour.value = color
        } else {
            viewModelScope.launch {
                _pickerColour.emit(color)
            }
        }
    }
}
