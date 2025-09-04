package com.skeensystems.colorpicker.ui.saved

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.skeensystems.colorpicker.R

@Composable
fun getActionBarItems(onCancel: () -> Unit): List<ActionBarItemSpec> =
    listOf(
        ActionBarItemSpec(
            onClick = onCancel,
            icon = Icons.Outlined.Close,
            text = "Cancel",
            contentDescription = "Cancel selection",
        ),
        ActionBarItemSpec(
            onClick = {},
            icon = Icons.Outlined.ContentCopy,
            text = "Copy",
            contentDescription = "Copy selection",
        ),
        ActionBarItemSpec(
            onClick = {},
            icon = Icons.Outlined.StarOutline,
            text = "Favourite",
            contentDescription = "Favourite all in selection",
        ),
        ActionBarItemSpec(
            onClick = {},
            icon = ImageVector.vectorResource(R.drawable.ic_star_outline_crossed),
            text = "Remove\nfavourite",
            contentDescription = "Remove favourite from all in selection",
        ),
        ActionBarItemSpec(
            onClick = {},
            icon = Icons.Outlined.Delete,
            text = "Delete",
            contentDescription = "Delete selection",
        ),
    )
