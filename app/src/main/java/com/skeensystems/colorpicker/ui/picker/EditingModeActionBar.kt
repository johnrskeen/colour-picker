package com.skeensystems.colorpicker.ui.picker

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.database.SavedColour

@Composable
fun EditingModeActionBar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    editingColour: SavedColour,
    currentColour: Color,
    exitEditingMode: (SavedColour) -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth().padding(10.dp)) {
        ExtendedFloatingActionButton(
            modifier = Modifier.weight(1f).padding(5.dp),
            containerColor = MaterialTheme.colorScheme.background,
            onClick = {
                val newColour =
                    viewModel.saveColour(
                        (currentColour.red * 255).toInt(),
                        (currentColour.green * 255).toInt(),
                        (currentColour.blue * 255).toInt(),
                    )
                exitEditingMode(newColour)
            },
            icon = { Icon(Icons.Filled.ContentCopy, "Save copy.") },
            text = { Text("Save copy") },
        )
        ExtendedFloatingActionButton(
            modifier = Modifier.weight(1f).padding(5.dp),
            containerColor = MaterialTheme.colorScheme.background,
            onClick = {
                viewModel.deleteColour(editingColour)
                val newColour =
                    viewModel.saveColour(
                        r = (currentColour.red * 255).toInt(),
                        g = (currentColour.green * 255).toInt(),
                        b = (currentColour.blue * 255).toInt(),
                        id = editingColour.id,
                        favourite = editingColour.favourite,
                    )
                exitEditingMode(newColour)
            },
            icon = { Icon(Icons.Outlined.ChangeCircle, "Confirm.") },
            text = { Text("Confirm") },
        )
    }
}
