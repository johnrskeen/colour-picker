package com.skeensystems.colorpicker

import android.os.Looper
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skeensystems.colorpicker.database.SavedColour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _savedColours = mutableStateMapOf<Long, SavedColour>()
    val savedColours: Map<Long, SavedColour> = _savedColours

    private val _cameraColour = MutableStateFlow(Color.Black)
    val cameraColour: StateFlow<Color> = _cameraColour.asStateFlow()

    fun addColour(savedColour: SavedColour) {
        _savedColours[savedColour.getId()] = savedColour
    }

    fun removeColour(savedColour: SavedColour) {
        _savedColours.remove(savedColour.getId())
    }

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
