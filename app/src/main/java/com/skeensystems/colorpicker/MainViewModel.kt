package com.skeensystems.colorpicker

import android.os.Looper
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _cameraColour = MutableStateFlow(Color.Black)
    val cameraColour: StateFlow<Color> = _cameraColour.asStateFlow()

    fun updateColour(
        r: Int,
        g: Int,
        b: Int,
    ) {
        val color = Color(r, g, b)

        if (Looper.myLooper() == Looper.getMainLooper()) {
            _cameraColour.value = color
        } else {
            viewModelScope.launch {
                _cameraColour.emit(color)
            }
        }
    }
}
