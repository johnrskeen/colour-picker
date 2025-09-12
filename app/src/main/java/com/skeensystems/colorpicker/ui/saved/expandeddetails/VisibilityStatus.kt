package com.skeensystems.colorpicker.ui.saved.expandeddetails

import androidx.compose.ui.geometry.Offset

sealed class VisibilityStatus {
    data class Show(
        val from: Offset,
        val savedColourId: Long,
    ) : VisibilityStatus()

    data class Hide(
        val to: Offset,
    ) : VisibilityStatus()
}
