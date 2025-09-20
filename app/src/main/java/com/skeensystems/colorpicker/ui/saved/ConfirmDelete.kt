package com.skeensystems.colorpicker.ui.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.skeensystems.colorpicker.R
import com.skeensystems.colorpicker.themeColour

@Composable
fun ConfirmDelete(
    title: String,
    confirmingDelete: Boolean,
    onDelete: () -> Unit,
    exitConfirmingDeleteMode: () -> Unit,
) {
    AnimatedVisibility(
        visible = confirmingDelete,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        AlertDialog(
            onDismissRequest = { exitConfirmingDeleteMode() },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        exitConfirmingDeleteMode()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(
                    onClick = { exitConfirmingDeleteMode() },
                ) { Text("Cancel") }
            },
            title = { Text(title) },
            text = { Text("This action cannot be undone.") },
            containerColor = themeColour(R.attr.mainColour),
        )
    }
}
