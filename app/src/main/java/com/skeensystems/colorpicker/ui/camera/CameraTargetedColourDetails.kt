package com.skeensystems.colorpicker.ui.camera

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.MainViewModel
import com.skeensystems.colorpicker.calculateTextColour
import com.skeensystems.colorpicker.getClosestColour
import com.skeensystems.colorpicker.getHexString

@Composable
fun CameraTargetedColourDetails(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    val targetedColour by viewModel.cameraColour.collectAsState()
    val closestColour = targetedColour.getClosestColour()
    val hexString = targetedColour.getHexString()
    val textColour = targetedColour.calculateTextColour()
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(color = targetedColour, shape = RoundedCornerShape(10.dp))
                .padding(20.dp),
        text = "\u2248 $closestColour\n$hexString",
        color = textColour,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
    )
}
