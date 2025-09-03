package com.skeensystems.colorpicker.ui.saved.expandeddetails

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.ui.IconAndTextButton

@Composable
fun ColourDetailsActionBar(
    hideDetailsView: () -> Unit,
    textColour: Color,
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
        IconAndTextButton(
            modifier = Modifier.weight(1f),
            onClick = {
                hideDetailsView()
            },
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            text = "Back",
            contentDescription = "Close colour details preview.",
            colour = textColour,
        )
        IconAndTextButton(
            modifier = Modifier.weight(1f),
            onClick = { TODO("Not yet implemented") },
            icon = Icons.Outlined.Star,
            text = "Favourite",
            contentDescription = "Toggle favourite colour.",
            colour = textColour,
        )
        IconAndTextButton(
            modifier = Modifier.weight(1f),
            onClick = { TODO("Not yet implemented") },
            icon = Icons.Outlined.Delete,
            text = "Delete",
            contentDescription = "Delete colour.",
            colour = textColour,
        )
    }
}
