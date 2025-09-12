package com.skeensystems.colorpicker.ui.camera

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.database.SavedColour

@Composable
fun CaptureColourButton(
    viewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
    localViewModel: CameraViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    val targetedColour by localViewModel.cameraColour.collectAsState()

    LargeFloatingActionButton(
        onClick = {
            viewModel.addColour(
                SavedColour(
                    System.currentTimeMillis(),
                    (targetedColour.red * 255).toInt(),
                    (targetedColour.green * 255).toInt(),
                    (targetedColour.blue * 255).toInt(),
                    false,
                ),
            )
        },
    ) {
        Icon(Icons.Filled.Add, "Capture colour.")
    }
}
