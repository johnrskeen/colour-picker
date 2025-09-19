package com.skeensystems.colorpicker.ui.saved.selectionmode

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
fun getActionBarItems(
    onCancel: () -> Unit,
    onCopy: () -> Unit,
    onSetFavouriteStatus: (favourite: Boolean) -> Unit,
    onDelete: () -> Unit,
): List<ActionBarItemSpec> =
    listOf(
        ActionBarItemSpec(
            onClick = onCancel,
            icon = Icons.Outlined.Close,
            text = "Cancel\n",
            contentDescription = "Cancel selection",
        ),
        ActionBarItemSpec(
            onClick = onCopy,
            icon = Icons.Outlined.ContentCopy,
            text = "Copy\n",
            contentDescription = "Copy selection",
        ),
        ActionBarItemSpec(
            onClick = { onSetFavouriteStatus(true) },
            icon = Icons.Outlined.StarOutline,
            text = "Favourite\n",
            contentDescription = "Favourite all in selection",
        ),
        ActionBarItemSpec(
            onClick = { onSetFavouriteStatus(false) },
            icon = ImageVector.vectorResource(R.drawable.ic_star_outline_crossed),
            text = "Remove\nfavourite",
            contentDescription = "Remove favourite from all in selection",
        ),
        ActionBarItemSpec(
            onClick = onDelete,
            icon = Icons.Outlined.Delete,
            text = "Delete\n",
            contentDescription = "Delete selection",
        ),
    )
