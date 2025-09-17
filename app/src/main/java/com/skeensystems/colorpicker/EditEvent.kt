package com.skeensystems.colorpicker

import com.skeensystems.colorpicker.database.SavedColour

data class EditEvent(
    val id: Long = System.currentTimeMillis(),
    val colour: SavedColour,
)
