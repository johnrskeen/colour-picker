package com.skeensystems.colorpicker.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun CaptureColourButton(
    viewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    snackbarHostState: SnackbarHostState,
    colour: Color,
    icon: ImageVector,
) {
    val scope = rememberCoroutineScope()
    val localDensity = LocalDensity.current

    val bottomPadding by animateDpAsState(
        targetValue =
            if (snackbarHostState.currentSnackbarData !=
                null
            ) {
                with(localDensity) { WindowInsets.safeContent.getBottom(localDensity).toDp() }
            } else {
                0.dp
            },
    )

    LargeFloatingActionButton(
        modifier = Modifier.windowInsetsPadding(WindowInsets(bottom = bottomPadding)),
        onClick = {
            val newColour =
                viewModel.saveColour(
                    (colour.red * 255).toInt(),
                    (colour.green * 255).toInt(),
                    (colour.blue * 255).toInt(),
                )
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Saved colour ${newColour.getHEXString()} (${newColour.getClosestMatchString()})",
                )
            }
        },
    ) {
        Icon(icon, "Capture colour.")
    }
}
