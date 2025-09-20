package com.skeensystems.colorpicker.ui.picker.finedetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AdjustButtons(
    modifier: Modifier = Modifier,
    onAdjust: (Int) -> Unit,
) {
    Row(modifier = modifier) {
        Box(modifier = Modifier.weight(1f).clickable { onAdjust(-1) }) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Minus 1.",
            )
        }
        Box(modifier = Modifier.weight(1f).clickable { onAdjust(1) }) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = "Add 1.",
            )
        }
    }
}
