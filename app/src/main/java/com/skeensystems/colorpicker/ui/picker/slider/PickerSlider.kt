package com.skeensystems.colorpicker.ui.picker.slider

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skeensystems.colorpicker.ui.picker.H
import com.skeensystems.colorpicker.ui.picker.PickerViewModel

@Composable
fun PickerSlider(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    var widthPx by remember { mutableFloatStateOf(0f) }
    BoxWithConstraints(
        modifier =
            modifier
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val width = size.width.toFloat()
                        viewModel.updateValue(H, 360f * offset.x / width)
                    }
                }.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val width = size.width.toFloat()
                            viewModel.updateValue(H, 360f * offset.x / width)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val width = size.width.toFloat()
                            viewModel.updateValue(H, 360f * change.position.x / width)
                        },
                    )
                },
    ) {
        widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        SliderBackground()
        SliderThumb(widthPx = widthPx)
    }
}
