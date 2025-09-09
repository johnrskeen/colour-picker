package com.skeensystems.colorpicker.ui.picker.slider

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.ui.picker.PickerViewModel

@Composable
fun SliderThumb(
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
    widthPx: Float,
) {
    val position by viewModel.h
    val thumbColour by viewModel.cornerColour.collectAsState()
    val thumbSize = with(LocalDensity.current) { 15.dp.roundToPx() }
    Box(
        modifier =
            Modifier
                .offset {
                    IntOffset((widthPx * position / 360f).toInt() - thumbSize, 0)
                }.size(30.dp)
                .background(color = thumbColour, shape = CircleShape),
    )
}
