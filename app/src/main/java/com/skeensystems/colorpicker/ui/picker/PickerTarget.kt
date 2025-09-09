package com.skeensystems.colorpicker.ui.picker

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.skeensystems.colorpicker.calculateTextColour
import kotlin.math.roundToInt

@Composable
fun PickerTarget(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
    widthPx: Float,
    heightPx: Float,
) {
    val s by viewModel.s
    val v by viewModel.v
    val touchPosition = IntOffset((s * widthPx).roundToInt(), ((1f - v) * heightPx).roundToInt())

    val pickerColour by viewModel.pickerColour.collectAsState()
    val size = with(LocalDensity.current) { 18.dp.roundToPx() }
    Box(
        modifier =
            modifier
                .size(
                    36.dp,
                ).offset { touchPosition.minus(IntOffset(size, size)) }
                .background(color = pickerColour, shape = CircleShape)
                .border(
                    width = 2.dp,
                    color = pickerColour.calculateTextColour(),
                    shape = CircleShape,
                ),
    )
}
