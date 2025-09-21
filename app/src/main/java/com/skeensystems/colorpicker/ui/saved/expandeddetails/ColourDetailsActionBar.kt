package com.skeensystems.colorpicker.ui.saved.expandeddetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skeensystems.colorpicker.ui.IconAndTextButton

@Composable
fun ColourDetailsActionBar(
    favouriteStatus: Boolean,
    onHideDetailsView: () -> Unit,
    onChangeFavouriteStatus: () -> Unit,
    onDelete: () -> Unit,
    textColour: Color,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        IconAndTextButton(
            modifier = Modifier.weight(1f),
            onClick = {
                onHideDetailsView()
            },
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            text = "Back",
            contentDescription = "Close colour details preview.",
            colour = textColour,
            afterOnClickPadding = PaddingValues(vertical = 20.dp),
        )
        IconAndTextButton(
            modifier = Modifier.weight(1f),
            onClick = { onChangeFavouriteStatus() },
            icon = if (favouriteStatus) Icons.Outlined.Star else Icons.Outlined.StarOutline,
            text = "Favourite",
            contentDescription = "Toggle favourite colour.",
            colour = textColour,
            afterOnClickPadding = PaddingValues(vertical = 20.dp),
        )
        IconAndTextButton(
            modifier = Modifier.weight(1f),
            onClick = { onDelete() },
            icon = Icons.Outlined.Delete,
            text = "Delete",
            contentDescription = "Delete colour.",
            colour = textColour,
            afterOnClickPadding = PaddingValues(vertical = 20.dp),
        )
    }
}
