package com.skeensystems.colorpicker.ui.saved

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.skeensystems.colorpicker.database.SavedColour
import com.skeensystems.colorpicker.ui.saved.expandeddetails.VisibilityStatus

class SavedColoursViewModel : ViewModel() {
    private val _selectingMode = mutableStateOf(false)
    val selectingMode: State<Boolean> = _selectingMode

    private val _selectedItems = mutableStateSetOf<SavedColour>()
    val selectedItems: Set<SavedColour> = _selectedItems

    private val _visibilityStatus =
        mutableStateOf<VisibilityStatus>(
            VisibilityStatus.Hide(Offset(0f, 0f)),
        )
    val visibilityStatus: State<VisibilityStatus> = _visibilityStatus

    val savedColourCoordinates = mutableMapOf<Long, Offset>()

    var animationDuration = 0
    var screenWidth = 0.dp
    var screenHeight = 0.dp
    var colourViewDimension = 0.dp

    fun enterSelectingMode(savedColour: SavedColour) {
        _selectingMode.value = true
        addSelectedItem(savedColour)
    }

    fun exitSelectingMode() {
        _selectedItems.clear()
        _selectingMode.value = false
    }

    fun addSelectedItem(savedColour: SavedColour) {
        _selectedItems.add(savedColour)
    }

    fun removeSelectedItem(savedColour: SavedColour) {
        _selectedItems.remove(savedColour)
        if (_selectedItems.isEmpty()) _selectingMode.value = false
    }

    fun setVisibilityStatus(visibilityStatus: VisibilityStatus) {
        _visibilityStatus.value = visibilityStatus
    }
}
