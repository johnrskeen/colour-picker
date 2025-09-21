package com.skeensystems.colorpicker.ui.saved.selectionmode

import androidx.compose.ui.graphics.vector.ImageVector

data class ActionBarItemSpec(
    val onClick: () -> Unit,
    val icon: ImageVector,
    val text: String,
    val contentDescription: String,
)
