package com.skeensystems.colorpicker.ui.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView

fun ComposeView.setCameraContent() {
    setContent {
        CameraScreen()
    }
}

@Composable
fun CameraScreen() {
    Scaffold(floatingActionButton = {
        LargeFloatingActionButton(
            onClick = {
            },
        ) {
            Icon(Icons.Filled.Add, "Capture colour.")
        }
    }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview()

            CameraPickerTarget(modifier = Modifier.align(Alignment.Center))

            CameraTargetedColourDetails(modifier = Modifier.padding(paddingValues).align(Alignment.TopCenter))
        }
    }
}
