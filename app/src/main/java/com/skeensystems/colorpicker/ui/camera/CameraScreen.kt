package com.skeensystems.colorpicker.ui.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
        CaptureColourButton()
    }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview()

            CameraPickerTarget(modifier = Modifier.align(Alignment.Center))

            CameraTargetedColourDetails(modifier = Modifier.padding(paddingValues).align(Alignment.TopCenter))
        }
    }
}
