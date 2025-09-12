package com.skeensystems.colorpicker

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.skeensystems.colorpicker.database.SavedColour

class MainViewModel : ViewModel() {
    private val _savedColours = mutableStateListOf<SavedColour>()
    val savedColours: List<SavedColour> = _savedColours

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
}
