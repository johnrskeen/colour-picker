package com.skeensystems.colorpicker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.ui.graphics.vector.ImageVector

enum class Destination(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
) {
    SAVED("Saved Colours", Icons.Default.Bookmarks, "Saved Colours"),
    CAMERA("Camera", Icons.Default.CameraAlt, "Camera"),
    PICKER("Manual Picker", Icons.Default.ColorLens, "Manual Picker"),
}
