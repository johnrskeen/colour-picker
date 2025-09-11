package com.skeensystems.colorpicker

import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skeensystems.colorpicker.database.SavedColour
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _savedColours = mutableStateListOf<SavedColour>()
    val savedColours: List<SavedColour> = _savedColours

    private val _cameraColour = MutableStateFlow(Color.Black)
    val cameraColour: StateFlow<Color> = _cameraColour.asStateFlow()

    // TODO temporary, while colours are manually added, will be removed with full migration
    fun clearSavedColours() {
        _savedColours.clear()
    }

    fun addColour(savedColour: SavedColour) {
        _savedColours.add(savedColour)
    }

    fun removeColour(savedColour: SavedColour) {
        _savedColours.remove(savedColour)
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
