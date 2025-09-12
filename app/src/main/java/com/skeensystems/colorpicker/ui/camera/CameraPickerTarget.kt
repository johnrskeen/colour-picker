package com.skeensystems.colorpicker.ui.camera

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.calculateTextColour

@Composable
fun CameraPickerTarget(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    val targetedColour by viewModel.cameraColour.collectAsState()
    val backgroundColour = targetedColour.calculateTextColour()
    Box(modifier = modifier.size(80.dp)) {
        Text(modifier = Modifier.size(30.dp, 3.dp).align(Alignment.CenterStart).background(backgroundColour), text = "")
        Text(modifier = Modifier.size(30.dp, 3.dp).align(Alignment.CenterEnd).background(backgroundColour), text = "")
        Text(modifier = Modifier.size(3.dp, 30.dp).align(Alignment.TopCenter).background(backgroundColour), text = "")
        Text(modifier = Modifier.size(3.dp, 30.dp).align(Alignment.BottomCenter).background(backgroundColour), text = "")

        Text(modifier = Modifier.fillMaxSize().border(width = 10.dp, color = targetedColour, shape = CircleShape), text = "")
    }
}
