package com.skeensystems.colorpicker.ui.picker

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GradientContainer(
    modifier: Modifier = Modifier,
    viewModel: PickerViewModel = viewModel(LocalActivity.current as ComponentActivity),
) {
    var widthPx by remember { mutableFloatStateOf(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }

    val cornerColour by viewModel.cornerColour.collectAsState()

    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            horizontalGradient(
                                colors = listOf(Color.White, cornerColour),
                            ),
                    ),
        )
        BoxWithConstraints(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                            ),
                    ).pointerInput(Unit) {
                        detectTapGestures { offset ->
                            updateValues(viewModel, offset.x, offset.y, widthPx, heightPx)
                        }
                    }.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                updateValues(viewModel, offset.x, offset.y, widthPx, heightPx)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                updateValues(viewModel, change.position.x, change.position.y, widthPx, heightPx)
                            },
                        )
                    },
        ) {
            with(LocalDensity.current) {
                widthPx = maxWidth.toPx()
                heightPx = maxHeight.toPx()
            }
            PickerTarget(widthPx = widthPx, heightPx = heightPx)
        }
    }
}

fun updateValues(
    viewModel: PickerViewModel,
    x: Float,
    y: Float,
    widthPx: Float,
    heightPx: Float,
) {
    viewModel.updateS(x.coerceIn(0f, widthPx) / widthPx)
    viewModel.updateV(1f - (y.coerceIn(0f, heightPx) / heightPx))
}
