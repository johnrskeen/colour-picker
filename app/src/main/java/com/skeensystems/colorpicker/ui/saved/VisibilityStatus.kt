package com.skeensystems.colorpicker.ui.saved

import androidx.compose.ui.geometry.Offset
import com.skeensystems.colorpicker.database.SavedColour

sealed class VisibilityStatus {
    data class Show(
        val from: Offset,
        val savedColour: SavedColour,
    ) : VisibilityStatus()

    data class Hide(
        val to: Offset,
    ) : VisibilityStatus()
}
